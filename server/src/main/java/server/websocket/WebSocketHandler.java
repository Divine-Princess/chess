package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.data.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import websocket.commands.ConnectCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.management.Notification;
import java.io.IOException;
import java.util.HashMap;


public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final GameService gameService;
    private final ConnectionManager connections = new ConnectionManager();
    private UserGameCommand command;
    private final HashMap<Session, String> sessionColors = new HashMap<>();
    private final HashMap<Session, Integer> gameIDs = new HashMap<>();
    private final HashMap<Session, String> usernames = new HashMap<>();

    public WebSocketHandler(GameService gameService) {
        this.gameService = gameService;
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
                case LEAVE -> leave(ctx.session);
                case RESIGN -> resign(ctx.session);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void connect(@NotNull WsMessageContext ctx) throws IOException {
        try {
            Session session = ctx.session;
            ConnectCommand command = new Gson().fromJson(ctx.message(), ConnectCommand.class);
            String playerColor = command.getPlayerColor();
            String username = command.getUsername();
            sessionColors.put(session, playerColor);
            usernames.put(session, username);

            connections.addSessionToGame(command.getGameID(), session);
            GameData gameData = gameService.getGame(command);
            gameIDs.put(session, gameData.gameID());

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
                joinedMessage = username + " joined as " + playerColor.toLowerCase();
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

        String username = usernames.get(ctx.session);
        String color = sessionColors.get(ctx.session);

        if (color == null) {
            connections.sendMessage(ctx.session, "Error: Observers cannot make moves");
        }

        try {
            ChessGame
        }

        // LOAD GAME
        // NOTIFICATION
    }

    private void leave(Session session) {
        // STOP SENDING MESSAGES
    }

    private void resign(Session session) {
        // NO MOVES CAN BE MADE
    }


}
