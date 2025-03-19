package dataaccess;

import model.GameData;

import java.util.ArrayList;

public class MySqlGameDataAccess extends MySqlDataAccess implements GameDAO {
    public MySqlGameDataAccess() throws DataAccessException {
    }

    @Override
    public GameData createGame(GameData gameData) throws DataAccessException {
        return null;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public boolean updateGame(String playerColor, int gameID, String username) throws DataAccessException {
        return false;
    }

    @Override
    public ArrayList<GameData> listGames() {
        return null;
    }

    @Override
    public void clearAllGameData() throws DataAccessException {
        var statement = "TRUNCATE Game";
        executeUpdate(statement);
    }
}
