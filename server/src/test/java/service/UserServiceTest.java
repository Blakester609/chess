package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    static UserDAO userDao = new MemoryUserDAO();
    static AuthDAO authDao = new MemoryAuthDAO();
    static GameDAO gameDao = new MemoryGameDAO();
    static final UserService service = new UserService(userDao, authDao, gameDao);

    @BeforeEach
    void setUp() throws DataAccessException {
        UserData user1 = new UserData("Blake", "cheese", "five@gmail.com");
        UserData user2 = new UserData("Joshua", "banana", "pink@yahoo.com");
        AuthData other = service.register(user1);
        AuthData expected = service.register(user2);
        GameData game = service.create(new GameData(0, null, null, "Bob-Game", null), expected.authToken());
        GameData otherGame = service.create(new GameData(2, "", null, "Blake-Game", null), other.authToken());
        boolean joinBool = service.join(new JoinRequest(ChessGame.TeamColor.WHITE, 2), other.authToken());
    }

    @AfterEach
    void clearAfter() throws DataAccessException {
        service.clear();
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
        UserData loginUser = new UserData("Joshua", "banana", "");
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

    @Test
    void logoutFailure() throws DataAccessException {
      assertThrows(DataAccessException.class, () -> service.logout("aasdfasdfasfasdfasf"));
    }

    @Test
    void createGame() throws DataAccessException {
        UserData loginUser = new UserData("Joshua", "banana", "pink@yahoo.com");
        AuthData expected = service.login(loginUser);
        GameData newGame = service.create(new GameData(0, "", "", "Josh-Game", null),
                expected.authToken());
        assertEquals("Josh-Game", newGame.gameName());
    }

    @Test
    void createGameFailure() throws DataAccessException {
        UserData loginUser = new UserData("Joshua", "banana", "pink@yahoo.com");
        AuthData expected = service.login(loginUser);
        assertThrows(DataAccessException.class, () -> service.create(new GameData(0, "", "", "Bob-Game", null), 
                expected.authToken())
        );
    }

    @Test
    void testJoinGame() throws DataAccessException {
        UserData loginUser = new UserData("Joshua", "banana", "pink@yahoo.com");
        AuthData expected = service.login(loginUser);
        assertTrue(service.join(new JoinRequest(ChessGame.TeamColor.WHITE, 1), expected.authToken()));
    }

    @Test
    void testJoinFail() throws DataAccessException {
        UserData loginUser = new UserData("Joshua", "banana", "pink@yahoo.com");
        AuthData expected = service.login(loginUser);
        assertThrows(DataAccessException.class, () -> service.join(new JoinRequest(ChessGame.TeamColor.WHITE, 2), expected.authToken()));
    }

    @Test
    void testListGames1() throws DataAccessException {
        UserData loginUser = new UserData("Joshua", "banana", "pink@yahoo.com");
        AuthData expected = service.login(loginUser);
        var gamesList = service.list(expected.authToken());
        assertEquals("Bob-Game", gamesList.getFirst().gameName());
    }

    @Test
    void testListGames2() {
        assertThrows(DataAccessException.class, () -> service.list("asdfasfasdf"));
    }

    @Test
    void testClear() throws DataAccessException {
        assertTrue(service.clear());
    }

    @Test
    void testClear2() throws DataAccessException {
        service.clear();
        assertThrows(DataAccessException.class, () -> userDao.getUser("Joshua", "banana"));
    }
}