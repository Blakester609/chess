package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DataAccessTests {
    private UserDAO getUserDataAccess(Class<? extends UserDAO> databaseClass) throws DataAccessException {
        UserDAO db;
        if (databaseClass.equals(MySqlUserDataAccess.class)) {
            db = new MySqlUserDataAccess();
        } else {
            db = new MemoryUserDAO();
        }
        db.clearAllUserData();
        return db;
    }

    private AuthDAO getAuthDataAccess(Class<? extends AuthDAO> databaseClass) throws DataAccessException {
        AuthDAO db;
        if (databaseClass.equals(MySqlAuthDataAccess.class)) {
            db = new MySqlAuthDataAccess();
        } else {
            db = new MemoryAuthDAO();
        }
        db.clearAuthData();
        return db;
    }

    private GameDAO getGameDataAccess(Class<? extends GameDAO> databaseClass) throws DataAccessException {
        GameDAO db;
        if (databaseClass.equals(MySqlGameDataAccess.class)) {
            db = new MySqlGameDataAccess();
        } else {
            db = new MemoryGameDAO();
        }
        db.clearAllGameData();
        return db;
    }


    @ParameterizedTest
    @ValueSource(classes = {MySqlAuthDataAccess.class, MemoryAuthDAO.class})
    void testCreateAuth(Class<? extends AuthDAO> dbClass) throws DataAccessException {
        AuthDAO dataAccess = getAuthDataAccess(dbClass);

        assertDoesNotThrow(() -> dataAccess.createAuth("Blake"));
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlAuthDataAccess.class, MemoryAuthDAO.class})
    void testCreateAuthFail(Class<? extends AuthDAO> dbClass) throws DataAccessException {
        AuthDAO dataAccess = getAuthDataAccess(dbClass);
        assertThrows(DataAccessException.class, () -> dataAccess.createAuth(null));
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlAuthDataAccess.class, MemoryAuthDAO.class})
    void testGetAuth(Class<? extends AuthDAO> dbClass) throws DataAccessException {
        AuthDAO dataAccess = getAuthDataAccess(dbClass);
        AuthData newAuth = dataAccess.createAuth("Jeremy");
        System.out.println(newAuth.authToken());
        assertDoesNotThrow(() -> dataAccess.getAuth(newAuth.authToken()));
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlAuthDataAccess.class, MemoryAuthDAO.class})
    void testGetAuthFail(Class<? extends AuthDAO> dbClass) throws DataAccessException {
        AuthDAO dataAccess = getAuthDataAccess(dbClass);

        assertThrows(DataAccessException.class, () -> dataAccess.getAuth("asdfasdfasdf"));
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlAuthDataAccess.class, MemoryAuthDAO.class})
    void testDeleteAuth(Class<? extends AuthDAO> dbClass) throws DataAccessException {
        AuthDAO dataAccess = getAuthDataAccess(dbClass);
        AuthData newAuth = dataAccess.createAuth("Jeremy");
        assertDoesNotThrow(() -> dataAccess.deleteAuth(newAuth));
    }


    @ParameterizedTest
    @ValueSource(classes = {MySqlAuthDataAccess.class, MemoryAuthDAO.class})
    void testDeleteAuthFail(Class<? extends AuthDAO> dbClass) throws DataAccessException {
        AuthDAO dataAccess = getAuthDataAccess(dbClass);

        assertThrows(DataAccessException.class, () -> dataAccess.deleteAuth(new AuthData("asdfasf", "Bob")));
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlUserDataAccess.class, MemoryUserDAO.class})
    void addUser(Class<? extends UserDAO> dbClass) throws DataAccessException {
        UserDAO dataAccess = getUserDataAccess(dbClass);

        var newUser = new UserData("Blake", "cheese", "five@gmail.com");
        assertDoesNotThrow(() -> dataAccess.createUser(newUser));
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlUserDataAccess.class, MemoryUserDAO.class})
    void addUserFail(Class<? extends UserDAO> dbClass) throws DataAccessException {
        UserDAO dataAccess = getUserDataAccess(dbClass);

        var newUser = new UserData("John", "applesauce", "seven@gmail.com");
        dataAccess.createUser(newUser);
        var otherUser = new UserData("John", "applesauce", "seven@gmail.com");
        assertThrows(DataAccessException.class, () -> dataAccess.createUser(otherUser));
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlUserDataAccess.class, MemoryUserDAO.class})
    void ensureClear(Class<? extends UserDAO> dbClass) throws DataAccessException {
        UserDAO dataAccess = getUserDataAccess(dbClass);

        var newUser = new UserData("Juan", "alfredo", "six@gmail.com");
        dataAccess.createUser(newUser);

        assertDoesNotThrow(dataAccess::clearAllUserData);
    }

    @Test
    void setupDatabase() {
        assertDoesNotThrow(MySqlDataAccess::new);
    }


}
