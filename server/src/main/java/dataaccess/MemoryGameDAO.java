package dataaccess;

import chess.ChessGame;
import model.GameData;
import java.util.ArrayList;
import java.util.HashMap;


public class MemoryGameDAO implements GameDAO{
    private final HashMap<Integer, GameData> games = new HashMap<>();


    public GameData getGame(int gameID){
        return games.get(gameID);
    }

    @Override
    public GameData createGame(GameData game) throws DataAccessException {
        GameData newGame = new GameData(game.gameID(), null, null, game.gameName(), new ChessGame());
        games.put(newGame.gameID(), newGame);
        return newGame;
    }

    @Override
    public GameData updateGame(GameData game) throws DataAccessException {
        games.put(game.gameID(), game);
        return game;
    }

    @Override
    public ArrayList<GameData> listGames() throws DataAccessException {
        return new ArrayList<>(games.values());
    }

    @Override
    public void clearGames() throws DataAccessException {
        games.clear();
    }

    @Override
    public boolean gameExists(int gameID) {
        for (int id : games.keySet()) {
            if (id == gameID) {
                return true;
            }
        }
        return false;
    }
}