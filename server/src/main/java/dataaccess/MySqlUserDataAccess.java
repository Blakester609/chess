package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

public class MySqlUserDataAccess extends MySqlDataAccess implements UserDAO {
    public MySqlUserDataAccess() throws DataAccessException {
    }

    @Override
    public UserData getUser(String username, String password) throws DataAccessException {
        if((username == null || username.isEmpty()) || (password == null || password.isEmpty())) {
            throw new DataAccessException("Error: bad request", 400);
        }
        try (var conn = DatabaseManager.getConnection()) {
            var statementOne = "SELECT password FROM User WHERE username=?";
            try (var ps = conn.prepareStatement(statementOne)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if(rs.next()) {
                        var hashedPassword = rs.getString("password");
                        if (BCrypt.checkpw(password, hashedPassword)) {
                            var statement = "SELECT username, password, email FROM User WHERE username=? AND password=?";
                            try (var ps2 = conn.prepareStatement(statement)) {
                                ps2.setString(1, username);
                                ps2.setString(2, hashedPassword);
                                try (var rs2 = ps2.executeQuery()) {
                                    if (rs2.next()) {
                                        var realUsername = rs2.getString("username");
                                        var realPassword = rs2.getString("password");
                                        var realEmail = rs2.getString("email");
                                        return new UserData(realUsername, realPassword, realEmail);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: unauthorized", 401);
        }
        throw new DataAccessException("Error: unauthorized", 401);
    }

    @Override
    public UserData createUser(UserData userData) throws DataAccessException {
        try {
            UserData user = getUser(userData.username(), userData.password());
            if(user != null) {
                throw new DataAccessException("Error: already taken", 403);
            }
        } catch (DataAccessException e) {
            if(e.statusCode() == 401) {
                var statement = "INSERT INTO User (username, password, email) VALUES (?, ?, ?)";
                String hashedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
                executeUpdate(statement, userData.username(), hashedPassword, userData.email());
                return userData;
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
