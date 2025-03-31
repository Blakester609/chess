package dataaccess;

import exception.DataAccessException;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.SQLException;

public class MySqlUserDataAccess extends MySqlDataAccess implements UserDAO {
    public MySqlUserDataAccess() throws DataAccessException {
        super();
    }

    @Override
    public UserData getUser(String username, String password) throws DataAccessException {
        if((username == null || username.isEmpty()) || (password == null || password.isEmpty())) {
            throw new DataAccessException("Error: bad request", 400);
        }
        try (var conn = DatabaseManager.getConnection()) {
            var statementOne = "SELECT password FROM User WHERE username=?";
            return getUserDataFinal(username, password, conn, statementOne);
        } catch (Exception ignored) {

        }
        throw new DataAccessException("An error occurred", 500);
    }

    private UserData getUserDataFinal(String username, String password, Connection conn, String statementOne)
            throws DataAccessException {
        try (var ps = conn.prepareStatement(statementOne)) {
            ps.setString(1, username);
            try (var rs = ps.executeQuery()) {
                if(rs.next()) {
                    var hashedPassword = rs.getString("password");
                    return getUserDataWithPassword(username, password, hashedPassword, conn);
                }
            } catch(Exception e) {
                throw new DataAccessException("Incorrect password", 400);
            }
        } catch (Exception e) {
            throw new DataAccessException("Incorrect username", 400);
        }
        throw new DataAccessException("Incorrect username or password", 400);
    }

    private UserData getUserDataWithPassword(String username, String password, String hashedPassword, Connection conn)
            throws DataAccessException, SQLException {
        if (BCrypt.checkpw(password, hashedPassword)) {
            var statement = "SELECT username, password, email FROM User WHERE username=? AND password=?";
            return getUserDataAllFields(username, hashedPassword, conn, statement);
        }
        throw new DataAccessException("Incorrect password", 400);
    }

    private UserData getUserDataAllFields(String username, String hashedPassword, Connection conn, String statement)
            throws DataAccessException, SQLException {
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
        throw new DataAccessException("Incorrect username or password", 400);
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
