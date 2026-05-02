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
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;


public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final GameService gameService;
    private final ConnectionManager connections = new ConnectionManager();
    private UserGameCommand command;

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
                case CONNECT -> connect(ctx.session);
                case MAKE_MOVE -> makeMove(ctx.session);
                case LEAVE -> leave(ctx.session);
                case RESIGN -> resign(ctx.session);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void connect(Session session) throws DataAccessException {
        try {
            connections.addSessionToGame(command.getGameID(), session);
            ChessGame chessGame = gameService.getGame(command);
            LoadGameMessage loadGameMessage =
                    new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, chessGame);
            String jsonMessage = new Gson().toJson(loadGameMessage);
            connections.broadcastMessage(null, jsonMessage, command.getGameID());

        }
        catch (Exception ex) {
            throw new DataAccessException("Unable to access game.");
        }
    }

    private void makeMove(Session session) {
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
