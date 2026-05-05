package client.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

import java.io.IOException;
import java.net.URI;

public class WebSocketFacade extends Endpoint implements MessageHandler {

    Session session;
    GameHandler gameHandler;

    public void connect(String url, GameHandler gameHandler,
                        String authToken, int gameID, String playerColor, String username) throws RuntimeException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.gameHandler = gameHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                public void onMessage(String message) {
                    gameHandler.printMessage(message, playerColor);
                }
            });

            sendCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID,null);

        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public void makeMove(ChessMove move, String authToken, int gameID) {
        try {
            sendCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, move);
        }
        catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    private void sendCommand(UserGameCommand.CommandType commandType,
                             String authToken, int gameID, ChessMove move) throws IOException {

        switch (commandType) {
            case CONNECT:
                UserGameCommand connectCommand = new UserGameCommand(commandType, authToken, gameID);
                String connectJson = new Gson().toJson(connectCommand);
                session.getBasicRemote().sendText(connectJson);
                break;
            case MAKE_MOVE:
                UserGameCommand makeMoveCommand =
                        new MakeMoveCommand(commandType, authToken, gameID, move);
                String makeMoveJson = new Gson().toJson(makeMoveCommand);
                session.getBasicRemote().sendText(makeMoveJson);
                break;
        }


    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }


}
