package client.websocket;

import com.google.gson.Gson;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import websocket.commands.ConnectCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

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
                    System.out.println(message);
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    gameHandler.printMessage(serverMessage);
                }
            });

            sendCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID, playerColor, username);

        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    private void sendCommand(UserGameCommand.CommandType commandType,
                             String authToken, int gameID, String playerColor, String username) throws IOException {

        switch (commandType) {
            case CONNECT:
                UserGameCommand command = new ConnectCommand(commandType, authToken, gameID, playerColor, username);
                String json = new Gson().toJson(command);
                session.getBasicRemote().sendText(json);

        }


    }


    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }


}
