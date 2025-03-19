package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.UUID;

public class MySqlAuthDataAccess extends MySqlDataAccess implements AuthDAO {

    public MySqlAuthDataAccess() throws DataAccessException {
    }

    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        if((username == null) || (username.isEmpty())) {
            throw new DataAccessException("Error: bad request", 400);
        }
        AuthData newAuth = new AuthData(UUID.randomUUID().toString(), username);
        var statement = "INSERT INTO Auth (authToken, username) VALUES (?, ?)";
        executeUpdate(statement, newAuth.authToken(), newAuth.username());
        return newAuth;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM Auth WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        var realAuthToken = rs.getString("authToken");
                        var realUsername = rs.getString("username");
                        return new AuthData(realAuthToken, realUsername);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: unauthorized", 401);
        }
        throw new DataAccessException("Error: unauthorized", 401);
    }

    @Override
    public boolean deleteAuth(AuthData authData) throws DataAccessException {
        var statement = "DELETE FROM Auth WHERE authToken=?";
        if(executeUpdate(statement, authData.authToken()) == 0) {
            throw new DataAccessException("Error: unauthorized", 401);
        }
        return false;
    }

    @Override
    public void clearAuthData() throws DataAccessException {
        var statement = "TRUNCATE Auth";
        executeUpdate(statement);
    }
}
