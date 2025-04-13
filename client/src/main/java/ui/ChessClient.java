package ui;

import chess.ChessGame;
import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import exception.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import server.ServerFacade;
import service.JoinRequest;
import service.ListResult;
import websocket.ServerMessageObserver;
import websocket.WebSocketFacade;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static chess.ChessGame.TeamColor.WHITE;
import static ui.EscapeSequences.*;

public class ChessClient {
    private String userAuth = null;
    private final ServerFacade server;
    private final String serverUrl;
    private WebSocketFacade ws;
    private ServerMessageObserver serverMessageObserver;
    private HashMap<Integer, Integer> gameIdsMap = new HashMap<>();
    private boolean signedIn = false;
    private final PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

    public ChessClient(String serverUrl, ServerMessageObserver serverMessageObserver) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.serverMessageObserver = serverMessageObserver;
    }

    public String eval(String input) {
        try {
            var tokens = input.split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch(cmd) {
                case "login" -> login(params);
                case "register" -> register(params);
                case "help" -> displayHelp();
                case "create" -> createGame(params);
                case "logout" -> logout();
                case "join" -> playGame(params);
                case "list" -> listGames();
                case "observe" -> observeGame(params);
                case "quit" -> "quit";
                default -> "try again";
            };
        } catch (DataAccessException e) {
            return e.getMessage();
        }
    }

    public String login(String... params) throws DataAccessException {
        if(params.length >= 2) {
            var username = params[0];
            var password = params[1];
            AuthData auth;
            try {
                auth = server.login(new UserData(username, password, null));
            } catch(Exception e) {
                throw new DataAccessException("Incorrect username or password", 400);
            }
            userAuth = auth.authToken();
            signedIn = true;
            ws = new WebSocketFacade(serverUrl, serverMessageObserver);
            return String.format("Signed in as %s", username);
        }
        throw new DataAccessException("Expected: <username> <password>", 400);
    }

    public String listGames() throws DataAccessException {
        assertSignedIn();
        if(gameIdsMap != null) {
            gameIdsMap.clear();
        }
        var gamesList = server.listGames(userAuth);
        var results = new StringBuilder();
        var gson = new Gson();
        ArrayList<ListResult> games = (ArrayList<ListResult>) gamesList.get("games");
        var result = gson.fromJson(String.valueOf(games.get(0)), ListResult.class);
        for(int i = 0; i < games.size(); i++) {
            result = gson.fromJson(String.valueOf(games.get(i)), ListResult.class);
            results.append(i+1);
            results.append(". ");
            results.append(result.gameName());
            results.append("\n");
            results.append("White: ");
            results.append(result.whiteUsername());
            results.append("  Black: ");
            results.append(result.blackUsername());
            gameIdsMap.put(i+1, result.gameID());
            results.append("\n\n");
        }
        return results.toString();
    }

    public String observeGame(String... params) throws DataAccessException {
        assertSignedIn();
        if(params.length == 1) {
            var gameID = params[0];
            int realGameID = Integer.parseInt(gameID);
            var gamesList = server.listGames(userAuth);
            var gson = new Gson();
            ArrayList<ListResult> games = (ArrayList<ListResult>) gamesList.get("games");
            try {
                var result = gson.fromJson(String.valueOf(games.get(gameIdsMap.get(realGameID)-1)), ListResult.class);
            } catch (Exception e) {
                throw new DataAccessException("Must provide a valid game ID as an integer", 400);
            }
            drawBoardWhitePerspective();
            return "";
        }
        throw new DataAccessException("Expected: <gameID>", 400);
    }

    public String register(String... params) throws DataAccessException {
        if(params.length >= 3) {
            var username = params[0];
            var password = params[1];
            var email = params[2];
            AuthData auth;
            try {
                auth = server.register(new UserData(username, password, email));
            } catch (Exception e) {
                throw new DataAccessException("Username is already taken. Try again.", 400);
            }
            userAuth = auth.authToken();
            signedIn = true;
            return String.format("Welcome to Chess, %s!", username);
        }
        throw new DataAccessException("Expected: <username> <password> <email>", 400);
    }

    public String logout() throws DataAccessException {
        assertSignedIn();
        server.logout(userAuth);
        userAuth = null;
        signedIn = false;
        return "You have logged out";
    }

    public String createGame(String... params) throws DataAccessException {
        assertSignedIn();
        if(params.length >= 1) {
            var gameName = params[0];
            var gameInfo = server.createGame(
                    new GameData(0, "unclaimed", "unclaimed", gameName, new ChessGame()), userAuth);
            return String.format("Created game %s", gameName);
        }
        throw new DataAccessException("Expected: <GAMENAME>", 400);
    }

    public String playGame(String... params) throws DataAccessException {
        assertSignedIn();
        if(params.length >= 2) {
            var gameID = params[0];
            var playerColor = params[1];
            ChessGame.TeamColor actualPlayerColor = null;
            if (playerColor.equals("white") || playerColor.equals("WHITE")) {
                actualPlayerColor = WHITE;
            } else if (playerColor.equals("black") || playerColor.equals("BLACK")) {
                actualPlayerColor = ChessGame.TeamColor.BLACK;
            } else {
                throw new DataAccessException("Must choose black or white team color", 400);
            }
            try {
                server.joinGame(new JoinRequest(actualPlayerColor, Integer.parseInt(gameID)), userAuth);
                ws.connectToGame(userAuth, Integer.parseInt(gameID));
            } catch (Exception e) {
                throw new DataAccessException("Must provide a valid game ID as an integer, e.g. join 1 white/black", 400);
            }
//            if(actualPlayerColor == ChessGame.TeamColor.WHITE) {
//                drawBoardWhitePerspective();
//            } else {
//                drawBoardBlackPerspective();
//            }
            return "";
        }
        throw new DataAccessException("Expected: <GAMEID> [WHITE|BLACK]", 400);
    }

    public String drawBoard(ChessBoard board, String playerColor) {
        StringBuilder boardString = new StringBuilder();
        boardString.append(ERASE_SCREEN);
        boardString.append(SET_BG_COLOR_LIGHT_GREY);
        boardString.append(SET_TEXT_COLOR_BLACK);
        boardString.append(EMPTY);
        boardString.append(EMPTY);
        String[] columnLabels = {"a", "b", "c", "d", "e", "f", "g", "h"};
        for (String columnLabel : columnLabels) {
            boardString.append(columnLabel).append("  ");
        }
        boardString.append("  ").append(RESET_BG_COLOR).append("\n");
        for(int i = 7; i >= 0; i--) {
            boardString.append(SET_BG_COLOR_LIGHT_GREY);
            boardString.append(SET_TEXT_COLOR_BLACK);
            boardString.append(EMPTY);
            boardString.append(i + 1).append(" ");
            for(int j = 7; j >= 0; j--) {
                ChessPiece piece = board.getPiece(new ChessPosition(i+1, j+1));
                if(((j % 2 == 1) && (i % 2 == 1)) || ((i % 2 == 0) && (j % 2 == 0))) {
                    if(piece != null) {
                        boardString.append(SET_BG_COLOR_WHITE);
                    } else {
                        boardString.append(SET_BG_COLOR_WHITE + "   ");
                    }
                } else {
                    if (piece != null) {
                        boardString.append(SET_BG_COLOR_DARK_GREY);
                    } else {
                        boardString.append(SET_BG_COLOR_DARK_GREY + "   ");
                    }
                }
                drawPieces(piece, boardString);
            }
            boardString.append(SET_BG_COLOR_LIGHT_GREY);
            boardString.append(SET_TEXT_COLOR_BLACK);
            boardString.append(" ").append(i+1).append(" ");
            boardString.append(RESET_BG_COLOR).append("\n");
        }
        boardString.append(SET_BG_COLOR_LIGHT_GREY);
        boardString.append(SET_TEXT_COLOR_BLACK);
        boardString.append(EMPTY);
        boardString.append(EMPTY);
        for (String columnLabel : columnLabels) {
            boardString.append(columnLabel).append("  ");
        }
        boardString.append("  ");
        boardString.append(RESET_BG_COLOR).append("\n");
        return boardString.toString();
    }

    private void drawPieces(ChessPiece piece, StringBuilder boardString) {
        if(piece != null) {
            if(piece.getTeamColor() == WHITE) {
                boardString.append(SET_TEXT_COLOR_RED);
            } else {
                boardString.append(SET_TEXT_COLOR_BLUE);
            }
            switch(piece.getPieceType()) {
                case ChessPiece.PieceType.PAWN -> boardString.append(" P ");
                case ChessPiece.PieceType.QUEEN -> boardString.append(" Q ");
                case ChessPiece.PieceType.BISHOP -> boardString.append(" B ");
                case ChessPiece.PieceType.KNIGHT -> boardString.append(" N ");
                case ChessPiece.PieceType.ROOK -> boardString.append(" R ");
                case ChessPiece.PieceType.KING -> boardString.append(" K ");
                default -> boardString.append(" X ");
            }
        }
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
                - logout <AUTHTOKEN> - use your authtoken to logout
                - quit
                - help
                """;
    }

    private void assertSignedIn() throws DataAccessException {
        if (!signedIn) {
            throw new DataAccessException("You must sign in", 400);
        }
    }

    private void drawBoardBlackPerspective() {
        out.print(ERASE_SCREEN);
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);
        out.print(EMPTY);
        out.print(EMPTY);
        String[] columnLabels = {"a", "b", "c", "d", "e", "f", "g", "h"};
        for(int i = 7; i >= 0; i--) {
            out.print(columnLabels[i] + "  ");
        }
        out.print("  ");
        out.print(RESET_BG_COLOR);
        out.println();
        printCheckeredRow(SET_TEXT_COLOR_RED, "1", "2",
                false, true);
        printStartRowNumber("3");
        drawEmptyWhiteLeftRow();
        printEndRowNumber("3");

        printStartRowNumber("4");
        drawEmptyBlackLeftRow();
        printEndRowNumber("4");

        printStartRowNumber("5");
        drawEmptyWhiteLeftRow();
        printEndRowNumber("5");

        printStartRowNumber("6");
        drawEmptyBlackLeftRow();
        printEndRowNumber("6");
        printCheckeredRow(SET_TEXT_COLOR_BLUE, "7", "8",
                true, true);
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);
        out.print(EMPTY);
        out.print(EMPTY);
        for(int i = 7; i >= 0; i--) {
            out.print(columnLabels[i] + "  ");
        }
        out.print("  ");
        out.print(RESET_BG_COLOR);
        out.println();
    }

    private void drawBoardWhitePerspective() {
        out.print(ERASE_SCREEN);
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);
        out.print(EMPTY);
        out.print(EMPTY);
        String[] columnLabels = {"a", "b", "c", "d", "e", "f", "g", "h"};
        for (String columnLabel : columnLabels) {
            out.print(columnLabel + "  ");
        }
        out.print("  ");
        out.print(RESET_BG_COLOR);
        out.println();
        printCheckeredRow(SET_TEXT_COLOR_BLUE, "8", "7",
                false, false);
        printStartRowNumber("6");
        drawEmptyWhiteLeftRow();
        printEndRowNumber("6");

        printStartRowNumber("5");
        drawEmptyBlackLeftRow();
        printEndRowNumber("5");

        printStartRowNumber("4");
        drawEmptyWhiteLeftRow();
        printEndRowNumber("4");

        printStartRowNumber("3");
        drawEmptyBlackLeftRow();
        printEndRowNumber("3");
        printCheckeredRow(SET_TEXT_COLOR_RED, "2", "1",
                true, false);
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);
        out.print(EMPTY);
        out.print(EMPTY);
        for (String columnLabel : columnLabels) {
            out.print(columnLabel + "  ");
        }
        out.print("  ");
        out.print(RESET_BG_COLOR);
        out.println();
    }

    private void printStartRowNumber(String num) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);
        out.print(EMPTY);
        out.print(num + " ");
    }

    private void printEndRowNumber(String num) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);
        out.print(" " + num + " ");
        out.print(RESET_BG_COLOR);
        out.println();
    }

    private void printCheckeredRow(String textColor, String firstRow, String secondRow,
                                   boolean pawnsFirst, boolean blackPerspective) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);
        out.print(EMPTY);
        out.print(firstRow + " ");
        out.print(textColor);
        if(pawnsFirst) {
            printPawns(EscapeSequences.SET_BG_COLOR_WHITE, EscapeSequences.SET_BG_COLOR_DARK_GREY);
        } else {
            printNotPawns(EscapeSequences.SET_BG_COLOR_WHITE, EscapeSequences.SET_BG_COLOR_DARK_GREY, blackPerspective);
        }
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);
        out.print(" " + firstRow + " ");
        out.print(RESET_BG_COLOR);
        out.println();
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(EMPTY);
        out.print(secondRow + " ");
        out.print(textColor);
        if(!pawnsFirst) {
            printPawns(EscapeSequences.SET_BG_COLOR_DARK_GREY, EscapeSequences.SET_BG_COLOR_WHITE);
        } else {
            printNotPawns(EscapeSequences.SET_BG_COLOR_DARK_GREY, EscapeSequences.SET_BG_COLOR_WHITE, blackPerspective);
        }
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);
        out.print(" " + secondRow + " ");
        out.print(RESET_BG_COLOR);
        out.println();
    }

    private void printPawns(String startRowColor, String secondTileColor) {
        for(int i = 0; i < 8; i++) {
            if(i % 2 == 1) {
                out.print(secondTileColor + " P ");
            } else {
                out.print(startRowColor + " P ");
            }
        }
    }

    private void printNotPawns(String startRowColor, String secondTileColor, boolean blackPerspective) {
        String[] pieces;
        if(blackPerspective) {
            pieces = new String[]{"R", "N", "B", "K", "Q", "B", "N", "R"};
        } else {
            pieces = new String[]{"R", "N", "B", "Q", "K", "B", "N", "R"};
        }
        for(int i = 0; i < 8; i++) {
            if(i % 2 == 1) {
                out.print(secondTileColor + " " + pieces[i] + " ");
            } else {
                out.print(startRowColor + " " + pieces[i] + " ");
            }
        }
    }

    private void drawEmptyWhiteLeftRow() {
        for(int i = 0; i < 8; i++) {
            if(i % 2 == 1) {
                out.print(SET_BG_COLOR_DARK_GREY + "   ");
            } else {
                out.print(SET_BG_COLOR_WHITE + "   ");
            }
        }
    }

    private void drawEmptyBlackLeftRow() {
        for(int i = 0; i < 8; i++) {
            if(i % 2 == 1) {
                out.print(SET_BG_COLOR_WHITE + "   ");
            } else {
                out.print(SET_BG_COLOR_DARK_GREY + "   ");
            }
        }
    }
}
