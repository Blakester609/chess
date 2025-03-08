package server;


import com.google.gson.Gson;
import dataaccess.*;
import model.AuthData;
import model.UserData;
import model.GameData;
import service.JoinRequest;
import service.UserService;
import spark.*;
import java.lang.Exception;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private UserService userService;


    public Server() {
        UserDAO userDao = new MemoryUserDAO();
        AuthDAO authDao = new MemoryAuthDAO();
        GameDAO gameDao = new MemoryGameDAO();
        userService = new UserService(userDao, authDao, gameDao);
    }


    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/session", this::loginHandler);
        Spark.delete("/session", this::logoutHandler);
        Spark.post("/user", this::registerHandler);
        Spark.post("/game", this::createGameHandler);
        Spark.put("/game", this::joinGameHandler);
        Spark.get("/game", this::listGamesHandler);
        Spark.delete("/db", this::clearDatabaseHandler);
        Spark.exception(DataAccessException.class, this::exceptionHandler);
        //This line initializes the server and can be removed once you have a functioning endpoint
//        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void setService(UserService userService) {
        this.userService = userService;
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void exceptionHandler(DataAccessException ex, Request req, Response res) {
        res.status(ex.StatusCode());
        res.body(ex.toJson());
    }

    private Object loginHandler(Request req, Response res) throws DataAccessException {
        UserData user = new Gson().fromJson(req.body(), UserData.class);
        AuthData auth = userService.login(user);
        return new Gson().toJson(auth);
    }

    private Object registerHandler(Request req, Response res) throws DataAccessException {
        UserData user = new Gson().fromJson(req.body(), UserData.class);
        AuthData auth = userService.register(user);
        return new Gson().toJson(auth);
    }

    private Object logoutHandler(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("authorization");
        boolean success = userService.logout(authToken);
        return new Gson().toJson(Map.of());
    }

    private Object createGameHandler(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("authorization");
        GameData game = new Gson().fromJson(req.body(), GameData.class);
        GameData gameData = userService.create(game, authToken);
        return new Gson().toJson(Map.of("gameID", gameData.gameID()));
    }

    private Object joinGameHandler(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("authorization");
        JoinRequest joinData = new Gson().fromJson(req.body(), JoinRequest.class);
        boolean joinResult = userService.join(joinData, authToken);
        return new Gson().toJson(Map.of());
    }

    private Object listGamesHandler(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("authorization");
        var gamesList = userService.list(authToken).toArray();
        return new Gson().toJson(Map.of("games", gamesList));
    }

    private Object clearDatabaseHandler(Request req, Response res) throws DataAccessException {
        boolean cleared = userService.clear();
        return new Gson().toJson(Map.of());
    }
}
