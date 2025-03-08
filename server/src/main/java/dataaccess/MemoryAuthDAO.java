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
    public AuthData getAuth(String authToken) throws DataAccessException {
        for(AuthData temp : this.authDataList) {
            if(temp.authToken().equals(authToken)) {
                return temp;
            }
        }
        throw new DataAccessException("Error: unauthorized", 401);
    }


    @Override
    public boolean deleteAuth(AuthData authData) throws DataAccessException {
        try {
            return this.authDataList.remove(authData);
        } catch (Error e) {
            throw new DataAccessException("Error: could not logout", 500);
        }
    }

    @Override
    public String toString() {
        return "MemoryAuthDAO{" +
                "authDataList=" + authDataList +
                '}';
    }
}
