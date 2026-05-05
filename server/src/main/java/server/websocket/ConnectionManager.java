package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ConnectionManager {

    private final HashMap<Integer, Set<Session>> gameSessions = new HashMap<>();

    public ConnectionManager() {

    }

    public void addSessionToGame(int gameID, Session session) {
        gameSessions.computeIfAbsent(gameID, id -> new HashSet<>()).add(session);
    }

    public void removeSessionFromGame(int gameID, Session session) {
        Set<Session> sessions = gameSessions.get(gameID);
        if (sessions != null) {
            sessions.remove(session);
        }
    }

//    public Set<Session> getSessionsForGame(int gameID) {
//
//    }

    public void broadcastMessage(Session excludeSession, String message, int gameID) throws IOException {
        var set = gameSessions.getOrDefault(gameID, Set.of());
        for (Session s : set) {
            if (s.isOpen()) {
                if (!s.equals(excludeSession)) {
                    s.getRemote().sendString(message);
                }
            }

        }
    }

    public void sendMessage(Session session, String message) throws IOException {
        if (session.isOpen()) {
            session.getRemote().sendString(message);
        }
    }

}
