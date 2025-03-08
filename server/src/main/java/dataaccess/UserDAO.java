package dataaccess;

import model.UserData;

public interface UserDAO {

    UserData getUser(String username, String password) throws DataAccessException;

    UserData createUser(UserData userData) throws DataAccessException;

    void clearAllUserData() throws DataAccessException;

}
