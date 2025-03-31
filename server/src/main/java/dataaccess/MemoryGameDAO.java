package dataaccess;

import chess.ChessGame;
import exception.DataAccessException;
import model.GameData;

import java.util.ArrayList;
import java.util.Objects;

public class MemoryGameDAO implements GameDAO {
    private final ArrayList<GameData> gameDataList = new ArrayList<>();

    @Override
    public GameData createGame(GameData gameData) throws DataAccessException {
        if(gameData.gameName() == null) {
            throw new DataAccessException("Error: bad request", 400);
        }
        for(GameData game : gameDataList) {
            if(game.gameName().equals(gameData.gameName())) {
                throw new DataAccessException("Error: bad request", 400);
            }
        }
        GameData newGame = new GameData(gameDataList.size() + 1, null, null, gameData.gameName(), new ChessGame());
        gameDataList.add(newGame);
        return newGame;
    }

    @Override
    public ArrayList<GameData> listGames() {
        return gameDataList;
    }

    @Override
    public void clearAllGameData() throws DataAccessException {
        try {
            gameDataList.clear();
        } catch (Error e) {
            throw new DataAccessException("Error: Could not clear games", 500);
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        for(GameData game : gameDataList) {
            if(game.gameID() == gameID) {
                return game;
            }
        }
        throw new DataAccessException("Error: bad request", 400);
    }



    @Override
    public boolean updateGame(String playerColor, int gameID, String username) throws DataAccessException {
        for(GameData game : gameDataList) {
            if(game.gameID() == gameID) {
                switch (playerColor) {
                    case "WHITE":
                        if(game.getWhiteUsername() == null || Objects.equals(game.getWhiteUsername(), "")
                        || game.getWhiteUsername().equals("unclaimed")) {
                            game.setWhiteUsername(username);
                            return true;
                        }
                        throw new DataAccessException("Error: already taken", 403);
                    case "BLACK":
                        if(game.getBlackUsername() == null || Objects.equals(game.getBlackUsername(), "")
                        || game.getBlackUsername().equals("unclaimed")) {
                            game.setBlackUsername(username);
                            return true;
                        }
                        throw new DataAccessException("Error: already taken", 403);
                    default:
                        throw new DataAccessException("Error: bad request", 400);
                }
            }
        }
        throw new DataAccessException("Error: an unknown error occurred", 500);
    }
}
