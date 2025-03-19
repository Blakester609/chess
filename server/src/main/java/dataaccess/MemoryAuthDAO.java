package dataaccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {
    private final ArrayList<AuthData> authDataList = new ArrayList<>();
    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        if((username == null) || (username.isEmpty())) {
            throw new DataAccessException("Error: bad request", 400);
        }
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
        boolean found;
        found = this.authDataList.remove(authData);
        if(!found) {
            throw new DataAccessException("Error: could not logout", 500);
        }
        return true;
    }

    @Override
    public void clearAuthData() throws DataAccessException {
        try {
            authDataList.clear();
        } catch (Error e) {
            throw new DataAccessException("Error: could not clear auth data", 500);
        }
    }

    @Override
    public String toString() {
        return "MemoryAuthDAO{" +
                "authDataList=" + authDataList +
                '}';
    }
}
