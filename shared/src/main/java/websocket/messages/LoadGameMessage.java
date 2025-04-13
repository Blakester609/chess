package websocket.messages;

import com.google.gson.Gson;
import model.GameData;

public class LoadGameMessage extends ServerMessage {
    private GameData game;

    public LoadGameMessage(ServerMessageType type, GameData game) {
        super(type);
        this.game = game;
    }

    public String toString() {
        return new Gson().toJson(this);
    }

    public GameData getGame() {
        return game;
    }
}
