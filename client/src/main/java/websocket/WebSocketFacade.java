package websocket;

import com.google.gson.Gson;
import jakarta.websocket.Endpoint;
import jakarta.websocket.*;
import ui.ResponseException;
import websocket.commands.UserGameCommand;
import websocket.messages.NotificationMessage;
import websocket.messages.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static websocket.messages.ServerMessage.ServerMessageType.NOTIFICATION;

public class WebSocketFacade extends Endpoint {
    private Session session;
    ServerMessageObserver observer;
    private final Gson gson = new Gson();

    public WebSocketFacade(String url, ServerMessageObserver observer) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.observer = observer;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    handleIncomingMessage(message);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        System.out.println("Client WebSocket connected.");
    }

    private void handleIncomingMessage(String message) {
        ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
        ServerMessage msg = switch (serverMessage.getServerMessageType()) {
            case NOTIFICATION -> new Gson().fromJson(message, NotificationMessage.class);
            case ERROR -> new Gson().fromJson(message, ErrorMessage.class);
            case LOAD_GAME -> new Gson().fromJson(message, LoadGameMessage.class);
        };
        observer.notify(msg);
    }

    public void connect(String authToken, Integer gameID) {
        sendCommand(new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID));
    }

    public void leave(String authToken, Integer gameID) {
        sendCommand(new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID));
    }

    public void resign(String authToken, Integer gameID) {
        sendCommand(new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID));
    }


    public void sendCommand(UserGameCommand command) throws ResponseException {
        try {
            this.session.getBasicRemote().sendText(gson.toJson(command));
        } catch (IOException e) {
            throw new ResponseException(e.getMessage());
        }
    }
}
