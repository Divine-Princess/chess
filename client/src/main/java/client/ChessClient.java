package client;

import serverfacade.ServerFacade;

public class ChessClient {

    private final ServerFacade server;
    private State state = State.LOGGEDOUT;

    public ChessClient(String url) {
        server = new ServerFacade(url);
    }

    private enum State {
        LOGGEDOUT,
        LOGGEDIN
    }

    public void run() {

    }
}
