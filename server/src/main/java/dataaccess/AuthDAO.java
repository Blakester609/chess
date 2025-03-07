package dataaccess;

import model.AuthData;

public interface AuthDAO {

    AuthData createAuth(String username);

    AuthData getAuth(AuthData auth) throws DataAccessException;


    AuthData deleteAuth(AuthData authData) throws DataAccessException;
}
