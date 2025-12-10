package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
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
import websocket.commands.MakeMoveCommand;
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
                case MAKE_MOVE -> makeMove(session, username, gameID, command);
                case LEAVE -> leave(session, username, gameID);
                case RESIGN -> resign(session, username, gameID);
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
        LoadGameMessage gameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
        connections.sendMessage(session, gameMessage);
    }

    public void leave(Session session, String username, Integer gameID) throws DataAccessException, IOException {
        GameData ogGame = gameService.getGame(gameID);
        GameData updatedGame;
        if (ogGame.whiteUsername() != null && ogGame.whiteUsername().equals(username)) {
            updatedGame = new GameData(ogGame.gameID(), null, ogGame.blackUsername(), ogGame.gameName(), ogGame.game());
        } else if (ogGame.blackUsername() != null && ogGame.blackUsername().equals(username)) {
            updatedGame = new GameData(ogGame.gameID(), ogGame.whiteUsername(), null, ogGame.gameName(), ogGame.game());
        } else {
            updatedGame = ogGame;
        }
        gameService.updateGame(updatedGame);
        System.out.println("broadcasting leave game for user " + username);
        NotificationMessage message = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, String.format("%s left the game", username));
        connections.broadcast(gameID, username, message);
        connections.remove(gameID, username);
    }

    public void resign(Session session, String username, Integer gameID) throws DataAccessException, IOException {
        GameData gameData = gameService.getGame(gameID);
        ChessGame game = gameData.game();
        if (gameData.whiteUsername().equals(username) || gameData.blackUsername().equals(username)) {
            game.setGameOver();
            NotificationMessage message = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, String.format("%s has resigned", username));
            connections.broadcastToAll(gameID, message);
            GameData endedGame = new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
            gameService.updateGame(endedGame);
        } else {
            connections.sendMessage(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Unable to resign this game"));
        }
    }

    public void makeMove(Session session, String username, Integer gameID, UserGameCommand command) throws DataAccessException, IOException {
        GameData gameData = gameService.getGame(gameID);
        ChessGame game = gameData.game();
        MakeMoveCommand moveCommand = new Gson().fromJson(new Gson().toJson(command), MakeMoveCommand.class);
        ChessMove move = moveCommand.getMove();
        if (game.isGameOver()) {
            connections.sendMessage(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "game is over and no moves may be made"));
        }
        ChessGame.TeamColor turn = game.getTeamTurn();
        boolean isWhite = username.equals(gameData.whiteUsername());
        boolean isBlack = username.equals(gameData.blackUsername());

        if ((turn.equals(ChessGame.TeamColor.WHITE) && !isWhite) ||
                (turn.equals(ChessGame.TeamColor.BLACK) && !isBlack)) {
            connections.sendMessage(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "not this user's turn"));
            return;
        }
        if (!isBlack && !isWhite) {
            connections.sendMessage(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "you are not a player"));
        }
        try {
            game.makeMove(move);
            GameData newGame = new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
            gameService.updateGame(newGame);
            LoadGameMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
            connections.broadcastToAll(gameID, loadGameMessage);
            NotificationMessage notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    String.format("the move %s to %s has been made by %s",
                            positionToString(move.getStartPosition()),
                            positionToString(move.getEndPosition())
                            , turn));
            connections.broadcast(gameID, username, notificationMessage);
            ChessGame.TeamColor oppColor = (((turn.equals(ChessGame.TeamColor.WHITE)) ? ChessGame.TeamColor.BLACK : (ChessGame.TeamColor.WHITE)));
            String opp = (oppColor == ChessGame.TeamColor.WHITE ? gameData.whiteUsername() : gameData.blackUsername());
            if (game.isInCheck(oppColor)) {
                NotificationMessage checkMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, String.format("%s is in check", opp));
                connections.broadcastToAll(gameID, checkMessage);
            }
            if (game.isInStalemate(oppColor)) {
                NotificationMessage staleMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, String.format("%s is in stalemate", opp));
                game.setGameOver();
                gameService.updateGame(new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game));
                connections.broadcastToAll(gameID, staleMessage);
            }
            if (game.isInCheckmate(oppColor)) {
                NotificationMessage mateMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, String.format("%s is in checkmate, %s wins", opp, username));
                game.setGameOver();
                gameService.updateGame(new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game));
                connections.broadcastToAll(gameID, mateMessage);
            }

        } catch (InvalidMoveException e) {
            connections.sendMessage(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "not a valid move"));
        }
    }

    private String positionToString(ChessPosition pos) {
        char col = (char) ('a' + pos.getColumn() - 1);
        return "" + col + pos.getRow();
    }
}
