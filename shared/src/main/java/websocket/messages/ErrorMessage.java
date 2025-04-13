package websocket.messages;

import com.google.gson.Gson;

public class ErrorMessage extends ServerMessage {

    public String getErrorMessage() {
        return errorMessage;
    }

    private String errorMessage;

    public ErrorMessage(ServerMessageType type, String errorMessage) {
        super(type);
        this.errorMessage = errorMessage;
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
