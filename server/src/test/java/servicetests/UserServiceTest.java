package servicetests;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    static UserDAO userDao = new MemoryUserDAO();
    static AuthDAO authDao = new MemoryAuthDAO();
    static final UserService service = new UserService(userDao, authDao);

    @BeforeEach
    void setUp() throws DataAccessException {
        UserData user1 = new UserData("Blake", "cheese", "five@gmail.com");
        UserData user2 = new UserData("Joshua", "banana", "pink@yahoo.com");
        AuthData expected = service.register(user1);
        expected = service.register(user2);
    }

    @Test
    void register() throws DataAccessException {
        UserData newUser = new UserData("Johnny", "abcdf", "abc@abc.com");
        AuthData expected = service.register(newUser);
        assertEquals(expected.username(), newUser.username());
    }

    @Test
    void testRegisterUserAlreadyTaken() throws DataAccessException {
        UserData user2 = new UserData("Joshua", "banana", "pink@yahoo.com");
        assertThrows(DataAccessException.class, ()
                -> service.register(user2)
        );
    }




    @Test
    void login() {
        UserData someUser = new UserData("Blakester", "12345", "asdf@asdf.com");
        assertThrows(DataAccessException.class, () -> service.login(someUser));
    }

    @Test
    void loginSuccess() throws DataAccessException {
        UserData loginUser = new UserData("Jason", "banana", "");
        AuthData expected = service.login(loginUser);
        assertEquals(expected.username(), loginUser.username());
    }

    @Test
    void logout() throws DataAccessException {
        UserData loginUser = new UserData("Jason", "pineapple", "seven@yahoo.com");
        AuthData expected = service.register(loginUser);
        boolean logout = service.logout(expected.authToken());
        assertTrue(logout);
    }
}