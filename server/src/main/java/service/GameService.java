package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import handlers.CreateGameResult;
import handlers.*;
import model.*;
import service.exceptions.AlreadyTakenException;
import service.exceptions.BadRequestException;
import service.exceptions.UnauthorizedException;

public class GameService {
    private final DataAccess dataAccess;
    int id = 1;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public ListGamesResult listGames(String authToken) throws UnauthorizedException, DataAccessException {
        if (authToken == null || dataAccess.getAuth(authToken) == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        return new ListGamesResult(dataAccess.listGames());
    }

    public CreateGameResult createGame(CreateGameRequest request, String authToken) throws UnauthorizedException, BadRequestException,
            DataAccessException {
        if (request == null || request.gameName() == null) {
            throw new BadRequestException("Error: bad request");
        }
        if (authToken == null || dataAccess.getAuth(authToken) == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        GameData game = new GameData(id, null, null, request.gameName(), new ChessGame());
        id = dataAccess.createGame(game);
        CreateGameResult res = new CreateGameResult(id);
        id += 1;
        return res;
    }

    public void joinGame(JoinGameRequest request, String authToken) throws UnauthorizedException, BadRequestException,
            AlreadyTakenException, DataAccessException {
        if (request == null || request.playerColor() == null || request.gameID() == null) {
            throw new BadRequestException("Error: bad request");
        }
        if (authToken == null || dataAccess.getAuth(authToken) == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        String player = dataAccess.getAuth(authToken).username();
        ChessGame.TeamColor playerColor = request.playerColor();
        GameData game = dataAccess.getGame(request.gameID());
        if (game == null) {
            throw new BadRequestException("Error: bad request");
        }
        if (!playerColor.equals(ChessGame.TeamColor.WHITE) && !playerColor.equals(ChessGame.TeamColor.BLACK)) {
            throw new BadRequestException("Error: bad request");
        }
        if (playerColor.equals(ChessGame.TeamColor.WHITE)) {
            if (game.whiteUsername() != null) {
                throw new AlreadyTakenException("Error: already taken");
            }
            GameData newGame = new GameData(game.gameID(), player, game.blackUsername(), game.gameName(), game.game());
            dataAccess.updateGame(newGame);
        }
        if (playerColor.equals(ChessGame.TeamColor.BLACK)) {
            if (game.blackUsername() != null) {
                throw new AlreadyTakenException("Error: already taken");
            }
            GameData newGame = new GameData(game.gameID(), game.whiteUsername(), player, game.gameName(), game.game());
            dataAccess.updateGame(newGame);
        }
    }

    public void clear() throws DataAccessException {
        dataAccess.clear();
    }

    public GameData getGame(Integer id) throws DataAccessException {
        return dataAccess.getGame(id);
    }
}
