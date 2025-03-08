package dataaccess;

import model.GameData;

import java.util.ArrayList;

public interface GameDAO {

    GameData createGame(GameData gameData) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    boolean updateGame(String playerColor, int gameID, String username) throws DataAccessException;

    ArrayList<GameData> listGames();

    void clearAllGameData() throws DataAccessException;
}
