package websocket.messages;

import com.google.gson.Gson;
import model.GameData;

public class LoadGameMessage extends ServerMessage {
    private GameData game;
    private String playerColor;

    public LoadGameMessage(ServerMessageType type, GameData game, String playerColor) {
        super(type);
        this.game = game;
        this.playerColor = playerColor;
    }

    public String toString() {
        return new Gson().toJson(this);
    }

    public GameData getGameData() {
        return game;
    }

    public String getPlayerColor() {
        return playerColor;
    }
}
