package ui;

public class ChessClient {
    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;
    private boolean signedIn = false;

    public ChessClient(ServerFacade server, String serverUrl) {
        this.server = server;
        this.serverUrl = serverUrl;
    }
}
