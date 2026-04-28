package client.websocket;

import com.google.gson.Gson;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;

import java.io.IOException;
import java.net.URI;
import java.util.Scanner;

public class WebSocketFacade extends Endpoint implements MessageHandler {

    Session session;
    GameHandler gameHandler;

    public WebSocketFacade(String url, GameHandler gameHandler) throws RuntimeException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.gameHandler = gameHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                public void onMessage(String message) {
                    System.out.println(message);
                    // determine what type of message it is and send it to the appropriate type
                }
            });

        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public void connect() throws RuntimeException {
        try {
            
        }
    }


    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }


}
