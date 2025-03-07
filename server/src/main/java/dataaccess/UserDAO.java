package dataaccess;

import model.UserData;

public interface UserDAO {

    UserData getUser(String username, String password) throws DataAccessException;
    UserData getUser(String username, String password, String email) throws DataAccessException;

    UserData createUser(UserData userData) throws DataAccessException;





}
