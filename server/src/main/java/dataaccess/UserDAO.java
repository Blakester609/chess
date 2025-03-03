package dataaccess;

import model.AuthData;
import model.UserData;

public interface UserDAO {

    UserData getUser(String username) throws DataAccessException;

    void createUser(UserData userData) throws DataAccessException;





}
