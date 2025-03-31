package ui;

import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;

import java.util.Arrays;

import static ui.EscapeSequences.SET_TEXT_COLOR_MAGENTA;

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
                case "help" -> displayHelp();
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
            signedIn = true;
            return String.format("Welcome to Chess, %s! Here is your auth token: %s", username, auth.authToken());
        }
        throw new DataAccessException("Expected: <username> <password> <email>", 400);
    }


    public String displayHelp() {
        if(!signedIn) {
            return SET_TEXT_COLOR_MAGENTA + """
                    - register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    - login <USERNAME> <PASSWORD> - to play chess
                    - quit
                    - help
                    """;
        }
        return SET_TEXT_COLOR_MAGENTA + """
                - create <GAMENAME> - create a game
                - list - list all games
                - join <GAMEID> [WHITE|BLACK] - join a game as that color
                - observe <GAMEID> - observe a game
                - logout
                - quit
                - help
                """;
    }
}
