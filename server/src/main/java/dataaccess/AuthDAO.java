package dataaccess;

import model.AuthData;

public interface AuthDAO {

    AuthData createAuth(String username) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    boolean deleteAuth(AuthData authData) throws DataAccessException;

    void clearAuthData() throws DataAccessException;
}
