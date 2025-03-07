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
    public AuthData getAuth(String authToken) {
        return null;
    }

    @Override
    public void deleteAuth(AuthData authData) {

    }

    @Override
    public String toString() {
        return "MemoryAuthDAO{" +
                "authDataList=" + authDataList +
                '}';
    }
}
