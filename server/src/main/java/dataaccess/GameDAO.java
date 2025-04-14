package dataaccess;

import chess.ChessMove;
import exception.DataAccessException;
import model.GameData;

import java.util.ArrayList;

public interface GameDAO {

    GameData createGame(GameData gameData) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    boolean updateGame(String playerColor, int gameID, String username) throws DataAccessException;

    GameData updateGameState(int gameID, ChessMove move) throws DataAccessException;

    ArrayList<GameData> listGames() throws DataAccessException;

    void clearAllGameData() throws DataAccessException;
}
