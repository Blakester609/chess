package servicetests;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    static final UserService service = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());

    @BeforeEach
    void setUp() {
    }

    @Test
    void register() {
    }

    @Test
    void login() {
        UserData someUser = new UserData("Blakester", "12345", "asdf@asdf.com");
        assertThrows(DataAccessException.class, () -> service.login(someUser));
    }

    @Test
    void logout() {
    }
}