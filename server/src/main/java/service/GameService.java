package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import handlers.CreateGameResult;
import handlers.*;
import model.GameData;
import service.exceptions.AlreadyTakenException;
import service.exceptions.BadRequestException;
import service.exceptions.UnauthorizedException;

public class GameService {
    private final DataAccess dataAccess;
    int id = 1;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public ListGamesResult listGames(String authToken) throws UnauthorizedException {
        if (authToken == null || dataAccess.getAuth(authToken) == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        return new ListGamesResult(dataAccess.listGames());
    }

    public CreateGameResult createGame(CreateGameRequest request, String authToken) throws UnauthorizedException, BadRequestException {
        if (request == null || request.gameName() == null) {
            throw new BadRequestException("Error: bad request");
        }
        if (authToken == null || dataAccess.getAuth(authToken) == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        GameData game = new GameData(id, null, null, request.gameName(), new ChessGame());
        dataAccess.createGame(game);
        CreateGameResult res = new CreateGameResult(id);
        id += 1;
        return res;
    }

    public void joinGame(JoinGameRequest request, String authToken) throws UnauthorizedException, BadRequestException, AlreadyTakenException {
        if (request == null || request.playerColor() == null || request.gameID() == null) {
            throw new BadRequestException("Error: bad request");
        }
        if (authToken == null || dataAccess.getAuth(authToken) == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        String playerColor = request.playerColor();
        GameData game = dataAccess.getGame(request.gameID());
        if (game == null) {
            throw new BadRequestException("Error: bad request");
        }
        if (!playerColor.equals("White") && !playerColor.equals("Black")) {
            throw new BadRequestException("Error: bad request");
        }


    }

    public void clear() {
        dataAccess.clear();
    }
}
