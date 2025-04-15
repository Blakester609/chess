package model;

import chess.ChessGame;

public class GameData {
    private final int gameID;
    private String whiteUsername;
    private String blackUsername;
    private final String gameName;
    private ChessGame game;
    private boolean isGameOver = false;


    public GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
        this.gameID = gameID;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
        this.game = game;
    }

    public int gameID() {
        return gameID;
    }

    public String gameName() {
        return gameName;
    }

    public void setIsGameOver(boolean isGameOver) {
        this.isGameOver = isGameOver;
    }

    public boolean getIsGameOver() {
        return this.isGameOver;
    }

    public void setWhiteUsername(String whiteUsername) {
        this.whiteUsername = whiteUsername;
    }

    public void setBlackUsername(String blackUsername) {
        this.blackUsername = blackUsername;
    }


    public ChessGame getGame() {
        return game;
    }

    public String getWhiteUsername() {
        return whiteUsername;
    }

    public String getBlackUsername() {
        return blackUsername;
    }
}
