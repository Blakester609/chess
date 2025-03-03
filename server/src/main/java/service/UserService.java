package service;

import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

public class UserService {

    private final UserDAO userDao;

    public UserService(UserDAO userDao) {
        this.userDao = userDao;
    }

    public AuthData register(UserData registerRequest) {
        return null;
    }

    public AuthData login(UserData loginRequest) {
        return null;
    }

    public void logout(AuthData logoutRequest) {

    }
}
