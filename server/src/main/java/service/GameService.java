package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import handlers.*;
import model.*;

import java.util.concurrent.ThreadLocalRandom;

public class GameService {
    private final GameDAO gameDataAccess;
    private final AuthDAO authDataAccess;

    public GameService(UserDAO userDataAccess, AuthDAO authDataAccess, GameDAO gameDataAccess){
        this.gameDataAccess = gameDataAccess;
        this.authDataAccess = authDataAccess;
    }

    public void clear() throws DataAccessException{
        gameDataAccess.clearGames();
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest)throws DataAccessException {
        int gameID = 1;
        do {
            gameID = ThreadLocalRandom.current().nextInt(1, 10000);
        }while(gameDataAccess.gameExists(gameID));

        GameData game = new GameData(gameID, null, null, createGameRequest.gameName(), new ChessGame());
        GameData insertedGame = gameDataAccess.createGame(game);
        return new CreateGameResult(insertedGame.gameID());
    }

    public ListGamesResult listGames() throws DataAccessException{
        return new ListGamesResult(gameDataAccess.listGames());
    }

    public boolean invalidAuth(String authToken) {
        try {
            return authDataAccess.getAuth(authToken) == null;
        } catch (DataAccessException e) {
            return true;
        }
    }
}
