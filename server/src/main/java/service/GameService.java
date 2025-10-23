package service;

import dataaccess.DataAccess;
import handlers.CreateGameResult;
import handlers.*;
import service.exceptions.BadRequestException;
import service.exceptions.UnauthorizedException;

public class GameService {
    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public ListGamesResult listGames(String authToken) throws UnauthorizedException {
        if (authToken == null || dataAccess.getAuth(authToken) == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        return new ListGamesResult(dataAccess.listGames());
    }

    public CreateGameResult createGame(String gameName) throws UnauthorizedException, BadRequestException {

    }

    public void clear() {
        dataAccess.clear();
    }
}
