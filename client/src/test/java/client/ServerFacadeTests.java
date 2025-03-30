package client;

import dataaccess.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
        var serverUrl = "http://localhost:8080";
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



}
