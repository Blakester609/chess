package websocket.messages;

public class ErrorMessage extends ServerMessage {

    public String getErrorMessage() {
        return errorMessage;
    }

    private String errorMessage;

    public ErrorMessage(ServerMessageType type, String errorMessage) {
        super(type);
        this.errorMessage = errorMessage;
    }
}
