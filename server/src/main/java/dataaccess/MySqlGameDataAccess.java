package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import exception.DataAccessException;
import model.GameData;

import java.util.ArrayList;
import java.util.Objects;

public class MySqlGameDataAccess extends MySqlDataAccess implements GameDAO {
    public MySqlGameDataAccess() throws DataAccessException {
    }

    @Override
    public GameData createGame(GameData gameData) throws DataAccessException {
        var statement = "INSERT INTO Game (whiteUsername, blackUsername, gameName, json, isGameOver) VALUES (?, ?, ?, ?, ?)";
        GameData newGameData = new GameData(gameData.gameID(), gameData.getWhiteUsername(),
                gameData.getBlackUsername(), gameData.gameName(), new ChessGame());
        var json = new Gson().toJson(newGameData);
        int isGameOver = newGameData.getIsGameOver() ? 1 : 0;
        var id = executeUpdate(statement, newGameData.getWhiteUsername(), newGameData.getBlackUsername(), newGameData.gameName(),
                json, isGameOver);
        return new GameData(id, gameData.getWhiteUsername(), gameData.getBlackUsername(), gameData.gameName(), gameData.getGame());
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, whiteUsername, blackUsername, gameName, json FROM Game WHERE id=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        var realId = rs.getInt("id");
                        var realWhiteUsername = rs.getString("whiteUsername");
                        var realBlackUsername = rs.getString("blackUsername");
                        var realGameName = rs.getString("gameName");
                        var realJson = rs.getString("json");
                        var jsonString = new Gson().fromJson(realJson, ChessGame.class);
                        return new GameData(realId, realWhiteUsername, realBlackUsername, realGameName, jsonString);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: bad request", 400);
        }
        throw new DataAccessException("Error: bad request", 400);
    }

    @Override
    public GameData getGameWithIsOver(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, whiteUsername, blackUsername, gameName, json, isGameOver FROM Game WHERE id=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        var realId = rs.getInt("id");
                        var realWhiteUsername = rs.getString("whiteUsername");
                        var realBlackUsername = rs.getString("blackUsername");
                        var realGameName = rs.getString("gameName");
                        var realJson = rs.getString("json");
                        var realIsGameOver = rs.getInt("isGameOver");
                        boolean newIsGameOver = realIsGameOver == 1;
                        var jsonString = new Gson().fromJson(realJson, ChessGame.class);
                        GameData newGameData = new GameData(realId, realWhiteUsername, realBlackUsername, realGameName, jsonString);
                        newGameData.setIsGameOver(newIsGameOver);
                        return newGameData;
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: bad request", 400);
        }
        throw new DataAccessException("Error: bad request", 400);
    }

    @Override
    public boolean updateGame(String playerColor, int gameID, String username) throws DataAccessException {
        GameData game = getGame(gameID);
        switch (playerColor) {
            case "WHITE":
                if(game.getWhiteUsername() == null || Objects.equals(game.getWhiteUsername(), "")
                || game.getWhiteUsername().equals("unclaimed")) {
                    var statement = "UPDATE Game SET whiteUsername=? WHERE id=?";
                    executeUpdate(statement, username, gameID);
                    return true;
                }
                throw new DataAccessException("Error: already taken", 403);
            case "BLACK":
                if(game.getBlackUsername() == null || Objects.equals(game.getBlackUsername(), "")
                        || game.getBlackUsername().equals("unclaimed") ) {
                    var statement = "UPDATE Game SET blackUsername=? WHERE id=?";
                    executeUpdate(statement, username, gameID);
                    return true;
                }
                throw new DataAccessException("Error: already taken", 403);
            default:
                throw new DataAccessException("Error: bad request", 400);
        }
    }

    @Override
    public boolean removeUserFromGame(int gameID, String username) throws DataAccessException {
        GameData game = getGame(gameID);
        if(game.getWhiteUsername().equals(username)) {
            var statement = "UPDATE Game SET whiteUsername=? WHERE id=?";
            String notClaimed = "unclaimed";
            executeUpdate(statement, notClaimed, gameID);
            return true;
        } else if (game.getBlackUsername().equals(username)) {
            var statement = "UPDATE Game SET blackUsername=? WHERE id=?";
            String notClaimed = "unclaimed";
            executeUpdate(statement, notClaimed, gameID);
            return true;
        } else {
            return true;
        }
    }

    @Override
    public GameData updateGameState(int gameID, ChessMove move) throws DataAccessException {
        try {
            var statement = "UPDATE Game SET json=? WHERE id=?";
            GameData game = getGame(gameID);
            ChessGame someGame = game.getGame();
            someGame.makeMove(move);
            if(someGame.isInCheckmate(ChessGame.TeamColor.WHITE)
                    || someGame.isInCheckmate(ChessGame.TeamColor.BLACK)) {
                game = updateGameIsOver(gameID, true);
            }
            var json = new Gson().toJson(someGame);
            executeUpdate(statement, json, gameID);
            return game;
        } catch(Exception e) {
            throw new DataAccessException("Error: invalid move request", 400);
        }
    }

    @Override
    public GameData updateGameIsOver(int gameID, boolean gameIsOver) throws DataAccessException {
        try {
            var statement = "UPDATE Game SET isGameOver=? WHERE id=?";
            int isGameOver = gameIsOver ? 1 : 0;
            executeUpdate(statement, isGameOver, gameID);
            GameData game = getGameWithIsOver(gameID);
            return game;
        } catch (Exception e) {
            throw new DataAccessException("Error: could not update isGameOver", 400);
        }

    }

    @Override
    public ArrayList<GameData> listGames() throws DataAccessException {
        var result = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, whiteUsername, blackUsername, gameName, json FROM Game";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        var realId = rs.getInt("id");
                        var realWhiteUsername = rs.getString("whiteUsername");
                        var realBlackUsername = rs.getString("blackUsername");
                        var realGameName = rs.getString("gameName");
                        var realJson = rs.getString("json");
                        var jsonString = new Gson().fromJson(realJson, ChessGame.class);
                        result.add(new GameData(realId, realWhiteUsername, realBlackUsername, realGameName, jsonString));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: unauthorized", 401);
        }
        return result;
    }

    @Override
    public void clearAllGameData() throws DataAccessException {
        var statement = "TRUNCATE Game";
        executeUpdate(statement);
    }
}
