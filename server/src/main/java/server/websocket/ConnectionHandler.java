package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.*;

public class ConnectionHandler {
    private final Map<Integer, Map<String, Session>> gameConnections = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();

    //adds session for user and gameID
    public void add(int gameID, String username, Session session) {
        gameConnections.putIfAbsent(gameID, new ConcurrentHashMap<>());
        gameConnections.get(gameID).put(username, session);
        System.out.println("added " + username + " to session " + session);
    }

    //removes session for the user and gameID if there are no useres left
    public void remove(int gameID, String username) {
        if (gameConnections.containsKey(gameID)) {
            gameConnections.get(gameID).remove(username);
            if (gameConnections.get(gameID).isEmpty()) {
                gameConnections.remove(gameID);
            }
        }
    }

    //sends a message to a specific session within the game
    public void sendMessage(Session session, ServerMessage message) throws IOException {
        if (session != null && session.isOpen()) {
            session.getRemote().sendString(gson.toJson(message));
        }
    }

    //sends message to all but the username in exclude
    public void broadcast(int gameID, String excludeUsername, ServerMessage message) throws IOException {
        if (!gameConnections.containsKey(gameID)) {
            return;
        }
        System.out.println("broadcasting, excluding " + excludeUsername);
        var connections = gameConnections.get(gameID);
        for (var entry : connections.entrySet()) {
            String username = entry.getKey();
            Session session = entry.getValue();
            if (!username.equals(excludeUsername)) {
                sendMessage(session, message);
            }
        }
    }

    //sends message to all sessions in game
    public void broadcastToAll(int gameID, ServerMessage message) throws IOException {
        if (!gameConnections.containsKey(gameID)) {
            return;
        }
        for (Session session : gameConnections.get(gameID).values()) {
            sendMessage(session, message);
        }
    }
}
