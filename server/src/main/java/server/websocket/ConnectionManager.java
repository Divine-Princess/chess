package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ConnectionManager {

    private final HashMap<Integer, Set<Session>> sessionMap = new HashMap<>();

    public ConnectionManager() {

    }

    public void addSessionToGame(int gameID, Session session) {

    }

    public void removeSessionFromGame(int gameID, Session session) {

    }

    public Set<Session> getSessionsForGame(int gameID) {

    }

    public void broadcastMessage(Session excludeSession, String message) {

    }

}
