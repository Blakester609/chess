package ui;

import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;

import java.util.Arrays;

public class ChessClient {
    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;
    private boolean signedIn = false;

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch(cmd) {
                case "login" -> login(params);
                case "register" -> register(params);
                case "quit" -> "quit";
                default -> "try again";
            };
        } catch (DataAccessException e) {
            return e.getMessage();
        }
    }

    public String login(String... params) throws DataAccessException {
        if(params.length >= 1) {
            var username = params[0];
            var password = params[1];
            AuthData auth = server.login(new UserData(username, password, null));
            return String.format("Signed in as %s", auth.username());
        }
        throw new DataAccessException("Expected: <username> <password>", 400);
    }

    public String register(String... params) throws DataAccessException {
        if(params.length >= 1) {
            var username = params[0];
            var password = params[1];
            var email = params[2];
            AuthData auth = server.register(new UserData(username, password, email));
            return String.format("Welcome to Chess, %s!", auth.username());
        }
        throw new DataAccessException("Expected: <username> <password> <email>", 400);
    }
}
