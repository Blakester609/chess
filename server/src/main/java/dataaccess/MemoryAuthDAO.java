package dataaccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {
    private final ArrayList<AuthData> authDataList = new ArrayList<>();
    @Override
    public AuthData createAuth(String username) {
        AuthData newAuth = new AuthData(UUID.randomUUID().toString(), username);
        authDataList.add(newAuth);
        return newAuth;
    }

    @Override
    public AuthData getAuth(AuthData auth) throws DataAccessException {
        for(AuthData temp : this.authDataList) {
            if(temp.username().equals(auth.username())) {
                if(temp.authToken().equals(auth.authToken())) {
                    return temp;
                }
            }
            throw new DataAccessException("Error: unauthorized", 401);
        }
        throw new DataAccessException("Error: could not logout", 500);
    }


    @Override
    public AuthData deleteAuth(AuthData authData) throws DataAccessException {
        AuthData auth = getAuth(authData);
        this.authDataList.remove(auth);
        throw new DataAccessException("Error: could not logout", 500);
    }

    @Override
    public String toString() {
        return "MemoryAuthDAO{" +
                "authDataList=" + authDataList +
                '}';
    }
}
