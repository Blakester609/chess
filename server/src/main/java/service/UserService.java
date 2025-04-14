package service;

import chess.ChessMove;
import dataaccess.AuthDAO;
import exception.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;

public class UserService {

    private final UserDAO userDao;
    private final AuthDAO authDao;
    private final GameDAO gameDao;

    public UserService(UserDAO userDao, AuthDAO authDAO, GameDAO gameDao) {
        this.userDao = userDao;
        this.authDao = authDAO;
        this.gameDao = gameDao;
    }

    public AuthData register(UserData registerRequest) throws DataAccessException {
        UserData userData = userDao.createUser(registerRequest);
        return authDao.createAuth(userData.username());
    }

    public AuthData login(UserData loginRequest) throws DataAccessException {
       UserData userData = userDao.getUser(loginRequest.username(), loginRequest.password());
       return authDao.createAuth(userData.username());
    }

    public boolean logout(String authToken) throws DataAccessException {
        AuthData authData = authDao.getAuth(authToken);
        return authDao.deleteAuth(authData);
    }

    public GameData create(GameData gameData, String authToken) throws DataAccessException {
        AuthData authData = authDao.getAuth(authToken);
        return gameDao.createGame(gameData);
    }

    public AuthData retrieveAuthData(String authToken) throws DataAccessException {
        return authDao.getAuth(authToken);
    }

    public boolean join(JoinRequest joinRequest, String authToken) throws DataAccessException {
        AuthData authData = authDao.getAuth(authToken);
        GameData gameData = gameDao.getGame(joinRequest.gameID());
        return gameDao.updateGame(String.valueOf(joinRequest.playerColor()), gameData.gameID(), authData.username());
    }

    public GameData retrieveGameData(int gameID) throws DataAccessException {
        return gameDao.getGame(gameID);
    }

    public GameData updateGameState(int gameID, ChessMove move) throws DataAccessException {
        return gameDao.updateGameState(gameID, move);
    }

    public ArrayList<ListResult> list(String authToken) throws DataAccessException {
        ArrayList<ListResult> listResult = new ArrayList<>();
        AuthData authData = authDao.getAuth(authToken);
        for(GameData game : gameDao.listGames()) {
            listResult.add(new ListResult(game.gameID(), game.getWhiteUsername(), game.getBlackUsername(), game.gameName()));
        }
        return listResult;
    }

    public boolean clear() throws DataAccessException {
        authDao.clearAuthData();
        gameDao.clearAllGameData();
        userDao.clearAllUserData();
        return true;
    }
}
