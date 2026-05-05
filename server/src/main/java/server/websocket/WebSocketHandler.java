package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.data.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import service.AuthService;
import service.GameService;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.HashMap;


public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final GameService gameService;
    private final AuthService authService;
    private final ConnectionManager connections = new ConnectionManager();
    private UserGameCommand command;
    private final HashMap<Session, String> sessionColors = new HashMap<>();
    private final HashMap<Session, Integer> gameIDs = new HashMap<>();
    private final HashMap<Session, String> usernames = new HashMap<>();

    public WebSocketHandler(GameService gameService, AuthService authService) {
        this.gameService = gameService;
        this.authService = authService;
    }

    @Override
    public void handleConnect(@NotNull WsConnectContext ctx) throws Exception {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) throws Exception {
        System.out.println("Websocket closed");
    }


    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) throws Exception {

        try {
            command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (command.getCommandType()) {
                case CONNECT -> connect(ctx);
                case MAKE_MOVE -> makeMove(ctx);
                case LEAVE -> leave(ctx);
                case RESIGN -> resign(ctx);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void connect(@NotNull WsMessageContext ctx) throws IOException {
        try {
            Session session = ctx.session;
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);

            connections.addSessionToGame(command.getGameID(), session);
            GameData gameData = gameService.getGame(command);
            gameIDs.put(session, gameData.gameID());

            String username = authService.getAuth(command.getAuthToken()).username();
            String color = null;
            if (username.equals(gameData.whiteUsername())) {
                color = "WHITE";
            } else if (username.equals(gameData.blackUsername())) {
                color = "BLACK";
            }
            sessionColors.put(session, color);
            usernames.put(session, username);

            // Send/broadcast loadGameMessage
            LoadGameMessage loadGameMessage =
                    new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData.game());
            String jsonLoadGameMessage = new Gson().toJson(loadGameMessage);
            connections.sendMessage(session, jsonLoadGameMessage);

            String joinedMessage;

            if (sessionColors.get(session) == null) {
                joinedMessage = username + " joined as observer";
            }
            else {
                joinedMessage = username + " joined as " + sessionColors.get(session).toLowerCase();
            }

            ServerMessage notificationMessage =
                    new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, joinedMessage);
            joinedMessage = new Gson().toJson(notificationMessage);
            connections.broadcastMessage(session, joinedMessage, command.getGameID());

        }
        catch (Exception ex) {
            ServerMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, ex.getMessage());
            String errorMsgJson = new Gson().toJson(errorMessage);
            connections.sendMessage(ctx.session, errorMsgJson);
        }
    }

    private void makeMove(@NotNull WsMessageContext ctx) throws IOException {

        MakeMoveCommand makeMoveCommand = new Gson().fromJson(ctx.message(), MakeMoveCommand.class);

        if (gameService.checkGameOver(makeMoveCommand)) {
            ServerMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                    "Game is over");
            String errorMsgJson = new Gson().toJson(errorMessage);
            connections.sendMessage(ctx.session, errorMsgJson);
            return;
        }

        String username = usernames.get(ctx.session);
        String color = sessionColors.get(ctx.session);
        ChessGame.TeamColor teamColor = null;
        ChessGame.TeamColor otherTeamColor = null;

        if (color != null) {
            if (color.equalsIgnoreCase("WHITE")) {
                teamColor = ChessGame.TeamColor.WHITE;
                otherTeamColor = ChessGame.TeamColor.BLACK;
            }
            else if (color.equalsIgnoreCase("BLACK")) {
                teamColor = ChessGame.TeamColor.BLACK;
                otherTeamColor = ChessGame.TeamColor.WHITE;
            }
        }

        String moveStr = deserializeMove(makeMoveCommand.getMove());

        try {
            GameData gameData = gameService.makeMove(makeMoveCommand);
            ChessGame game = gameData.game();
            String otherUser;
            if (teamColor == null) {
                otherUser = null;
            }
            else if (teamColor == ChessGame.TeamColor.WHITE) {
                otherUser = gameData.blackUsername();
            } else {
                otherUser = gameData.whiteUsername();
            }

            LoadGameMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
            String jsonLoadGameMessage = new Gson().toJson(loadGameMessage);
            connections.broadcastMessage(null, jsonLoadGameMessage, command.getGameID());

            broadcastNotificationMessage(username + " made the move " + moveStr, ctx.session);

            if (game.isInCheckmate(otherTeamColor)) {
                broadcastNotificationMessage(otherUser + " is in checkmate! \n Game Over.", null);
                gameService.setGameOver(command);
            }
            else if (game.isInCheck(otherTeamColor)) {
                broadcastNotificationMessage(otherUser + " is in check!", null);
            }
            else if (game.isInStalemate(otherTeamColor)) {
                broadcastNotificationMessage(otherUser + " is in stalemate! \n Game Over.", null);
                gameService.setGameOver(command);
            }
            else if (game.isInCheckmate(teamColor)) {
                broadcastNotificationMessage(username + " is in checkmate! \n Game Over.", null);
                gameService.setGameOver(command);
            }
            else if (game.isInCheck(teamColor)) {
                broadcastNotificationMessage(username + " is in check!", null);
            }
            else if (game.isInStalemate(teamColor)) {
                broadcastNotificationMessage(username + " is in stalemate! \n Game Over.", null);
                gameService.setGameOver(command);
            }


        } catch (Exception ex) {
            ServerMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, ex.getMessage());
            String errorMsgJson = new Gson().toJson(errorMessage);
            connections.sendMessage(ctx.session, errorMsgJson);
        }
    }

    private void leave(@NotNull WsMessageContext ctx) throws IOException {
        String username = usernames.get(ctx.session);

        UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);

        sessionColors.remove(ctx.session);
        gameIDs.remove(ctx.session);
        usernames.remove(ctx.session);

        connections.removeSessionFromGame(command.getGameID(), ctx.session);

        try {
            gameService.removePlayer(command);
            broadcastNotificationMessage(username + " has left the game.", ctx.session);


        } catch (Exception ex) {
            ServerMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, ex.getMessage());
            String errorMsgJson = new Gson().toJson(errorMessage);
            connections.sendMessage(ctx.session, errorMsgJson);
        }
    }

    private void broadcastNotificationMessage(String message, Session exclude) throws IOException {
        ServerMessage notificationMessage =
                new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        String jsonMessage = new Gson().toJson(notificationMessage);
        connections.broadcastMessage(exclude, jsonMessage, command.getGameID());
    }

    private String deserializeMove(ChessMove move) {
        int startRow = move.getStartPosition().getRow();
        int startCol = move.getStartPosition().getColumn();
        int endRow = move.getEndPosition().getRow();
        int endCol = move.getEndPosition().getColumn();

        char charEndCol = (char)('a' + (endCol - 1));
        char charStartCol = (char)('a' + (startCol - 1));

        return charStartCol + "" + startRow + " " + charEndCol + endRow;
    }


    private void resign(@NotNull WsMessageContext ctx) throws IOException {
        String username = usernames.get(ctx.session);

        UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);

        if (gameService.checkGameOver(command)) {
            ServerMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                    "Game is over");
            String errorMsgJson = new Gson().toJson(errorMessage);
            connections.sendMessage(ctx.session, errorMsgJson);
            return;
        }

        try {
            gameService.resign(command);
            broadcastNotificationMessage(username + " has resigned. \nGame Over.", null);

            gameService.setGameOver(command);

        } catch (Exception ex) {
            ServerMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, ex.getMessage());
            String errorMsgJson = new Gson().toJson(errorMessage);
            connections.sendMessage(ctx.session, errorMsgJson);
        }

        // NO MOVES CAN BE MADE
    }


}
