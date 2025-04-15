package client;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.DataAccessException;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import service.JoinRequest;
import service.ListResult;
import server.ServerFacade;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        var serverUrl = "http://localhost:" + port;
        facade = new ServerFacade(serverUrl);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void sampleTest() {
        assertTrue(true);
    }

    @Test
    public void registerSuccess() throws Exception {
        var authData = facade.register(new UserData("Blake", "Pie123", "twelve@gmail.com"));
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    public void registerFail() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> facade.register(new UserData("Blake", "Pie123", "twelve@gmail.com")));
    }

    @Test
    public void loginSuccess() throws DataAccessException {
        var authData = facade.login(new UserData("Blake", "Pie123", null));
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    public void loginFailure() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> facade.login(new UserData("Joe", "Blebster", null)));
    }

    @Test
    public void logoutSuccess() throws DataAccessException {
        var authData = facade.login(new UserData("Blake", "Pie123", null));
        assertDoesNotThrow(() -> facade.logout(authData.authToken()));
    }

    @Test
    public void logoutFailure() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> facade.logout("awsdfasdfasfd"));
    }

    @Test
    public void createGameSuccess() throws DataAccessException {
        var authData = facade.login(new UserData("Blake", "Pie123", null));
        var gameMap = facade.createGame(new GameData(0, "unclaimed", "unclaimed", "other-game", new ChessGame()), authData.authToken());
        assertTrue((Double) gameMap.get("gameID") >= 1.0);
    }

    @Test
    public void createGameFail() {
        assertThrows(DataAccessException.class, () -> facade.createGame(new GameData(0, "", "", "Josiah-Game", new ChessGame()), "asdfasdfsde"));
    }

    @Test
    public void joinGameSuccess() throws DataAccessException {
        var authData = facade.login(new UserData("Blake", "Pie123", null));
        assertDoesNotThrow(() -> facade.joinGame(new JoinRequest(ChessGame.TeamColor.WHITE, 2), authData.authToken()));
    }

    @Test
    public void joinGameFail() throws DataAccessException {
        var authData = facade.login(new UserData("Blake", "Pie123", null));
        assertThrows(DataAccessException.class, () -> facade.joinGame(new JoinRequest(ChessGame.TeamColor.WHITE, 20), authData.authToken()));
    }

    @Test
    public void listGamesSuccess() throws DataAccessException {
        var authData = facade.login(new UserData("Blake", "Pie123", null));
        var gamesList = facade.listGames(authData.authToken());
        ArrayList<ListResult> games = (ArrayList<ListResult>) gamesList.get("games");
        var firstGame = new Gson().fromJson(String.valueOf(games.get(0)), ListResult.class);
        System.out.println(firstGame.whiteUsername());
        assertFalse(gamesList.isEmpty());
    }

    @Test
    public void listGamesFail() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> facade.listGames("asdfasdf"));
    }



}
