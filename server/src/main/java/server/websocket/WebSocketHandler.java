package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MySqlDataAccess;
import io.javalin.websocket.*;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import service.*;
import service.exceptions.UnauthorizedException;
import websocket.commands.UserGameCommand;
import websocket.messages.*;
import websocket.messages.ServerMessage;

import javax.management.Notification;
import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionHandler connections = new ConnectionHandler();
    private final Gson gson = new Gson();
    private final UserService userService;
    private final GameService gameService;

    public WebSocketHandler(UserService userService, GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    @Override
    public void handleMessage(WsMessageContext ctx) throws Exception {
        Session session = ctx.session;
        UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);

        AuthData auth;
        String username;
        ChessGame.TeamColor color = null;

        try {
            auth = userService.getAuth(command.getAuthToken());
            if (auth == null) {
                throw new UnauthorizedException("Invalid authentication token.");
            }
            username = auth.username();

            int gameID = command.getGameID();
            GameData game = gameService.getGame(gameID);
            if (username.equals(game.whiteUsername())) {
                color = ChessGame.TeamColor.WHITE;
            } else if (username.equals(game.blackUsername())) {
                color = ChessGame.TeamColor.BLACK;
            }

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, gameID, color);
//                case MAKE_MOVE -> makeMove(session, username, gameID, command.getMove());
//                case LEAVE -> leave(session, username, gameID);
//                case RESIGN -> resign(session, username, gameID);
                default ->
                        connections.sendMessage(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: no command type found"));
            }
        } catch (UnauthorizedException e) {
            connections.sendMessage(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: unauthorized"));
        } catch (Exception e) {
            connections.sendMessage(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: " + e.getMessage()));
        }
    }

    public void connect(Session session, String username, Integer gameID, ChessGame.TeamColor color) throws DataAccessException, IOException {
        connections.add(gameID, username, session);
        GameData gameData = gameService.getGame(gameID);
        if (gameData == null) {
            return;
        }
        ChessGame game = gameData.game();
        String playerType;
        if (color == ChessGame.TeamColor.WHITE) {
            playerType = "white";
        } else if (color == ChessGame.TeamColor.BLACK) {
            playerType = "black";
        } else {
            playerType = "observer";
        }
        String message = username + " joined the game " + gameID + " as " + playerType;
        NotificationMessage connectMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(gameID, username, connectMessage);
        LoadGameMessage gameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game, playerType);
        connections.sendMessage(session, gameMessage);
    }

    public void leave(Session session, )
}
