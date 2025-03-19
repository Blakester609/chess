package dataaccess;

import model.UserData;

public class MySqlUserDataAccess extends MySqlDataAccess implements UserDAO {
    public MySqlUserDataAccess() throws DataAccessException {
    }

    @Override
    public UserData getUser(String username, String password) throws DataAccessException {
        if((username == null || username.isEmpty()) || (password == null || password.isEmpty())) {
            throw new DataAccessException("Error: bad request", 400);
        }
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM User WHERE username=? AND password=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                ps.setString(2, password);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        var realUsername = rs.getString("username");
                        var realPassword = rs.getString("password");
                        var realEmail = rs.getString("email");
                        return new UserData(realUsername, realPassword, realEmail);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: unauthorized", 401);
        }
        return null;
    }

    @Override
    public UserData createUser(UserData userData) throws DataAccessException {
        try {
            UserData user = getUser(userData.username(), userData.password());
            if(user == null) {
                var statement = "INSERT INTO User (username, password, email) VALUES (?, ?, ?)";
                executeUpdate(statement, userData.username(), userData.password(), userData.email());
                return userData;
            }
            throw new DataAccessException("Error: already taken", 403);
        } catch (DataAccessException e) {
            if(e.statusCode() == 401) {
                throw e;
            }
            if(e.statusCode() == 403) {
                throw e;
            }
            if(e.statusCode() == 400) {
                throw e;
            }
        }
        throw new DataAccessException("Error: Could not create user", 500);
    }

    @Override
    public void clearAllUserData() throws DataAccessException {
        var statement = "TRUNCATE User";
        executeUpdate(statement);
    }
}
