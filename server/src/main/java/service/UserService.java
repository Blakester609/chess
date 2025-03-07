package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

public class UserService {

    private final UserDAO userDao;
    private final AuthDAO authDao;

    public UserService(UserDAO userDao, AuthDAO authDAO) {
        this.userDao = userDao;
        this.authDao = authDAO;
    }

    public AuthData register(UserData registerRequest) throws DataAccessException {
        UserData userData = userDao.createUser(registerRequest);
        return authDao.createAuth(userData.username());
    }

    public AuthData login(UserData loginRequest) throws DataAccessException {
       UserData userData = userDao.getUser(loginRequest.username(), loginRequest.password());
       return authDao.createAuth(userData.username());
    }

    public void logout(AuthData logoutRequest) {

    }
}
