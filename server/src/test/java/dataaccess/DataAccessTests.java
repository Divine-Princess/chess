package dataaccess;

import chess.ChessGame;
import dataaccess.authdao.AuthDAO;
import dataaccess.authdao.MySQLAuthDAO;
import dataaccess.gamedao.GameDAO;
import dataaccess.gamedao.MySQLGameDAO;
import dataaccess.userdao.MySQLUserDAO;
import dataaccess.userdao.UserDAO;
import model.data.AuthData;
import model.data.GameData;
import model.data.UserData;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Collection;


public class DataAccessTests {

    UserDAO userDAO;
    AuthDAO authDAO;
    GameDAO gameDAO;

    @BeforeEach
    public void initialize() throws DataAccessException {
        DatabaseConfigurator db = new DatabaseConfigurator();
        userDAO = new MySQLUserDAO(db);
        authDAO = new MySQLAuthDAO(db);
        gameDAO = new MySQLGameDAO(db);
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
    }

    @Test
    @Order(1)
    @DisplayName("USER: Get Success")
    public void getUserSuccess() throws DataAccessException {

        UserData userData = new UserData("emma", "pw", "emma@email.com");

        userDAO.createUser(userData);

        Assertions.assertDoesNotThrow(() -> userDAO.getUser("emma"));
        UserData newUserData = userDAO.getUser("emma");
        Assertions.assertEquals(userData, newUserData);
        Assertions.assertEquals(userData.username(), newUserData.username());
        Assertions.assertEquals(userData.email(), newUserData.email());
    }

    @Test
    @Order(2)
    @DisplayName("USER: Get Null")
    public void getUserFailure() throws DataAccessException {
        Assertions.assertNull(userDAO.getUser("emma"), "Expected null, returned other");
    }

    @Test
    @Order(3)
    @DisplayName("USER: Create Success")
    public void createUserSuccess() throws DataAccessException {
        UserData userData = new UserData("emma", "pw", "emma@email.com");

        userDAO.createUser(userData);

        Assertions.assertDoesNotThrow(() -> userDAO.getUser("emma"));
        UserData newUserData = userDAO.getUser("emma");
        Assertions.assertNotNull(newUserData);
        Assertions.assertEquals(userData, newUserData);
    }

    @Test
    @Order(4)
    @DisplayName("USER: Create Failure - Duplicate User")
    public void createUserFailureDuplicateUser() throws DataAccessException {
        UserData userData = new UserData("emma", "pw", "emma@email.com");

        userDAO.createUser(userData);

        Assertions.assertThrows(DataAccessException.class, () -> userDAO.createUser(userData));
    }

    @Test
    @Order(5)
    @DisplayName("USER: Create Failure - Null user")
    public void createUserFailureNullUser() {
        UserData userData = new UserData("emma", null, "emma@email.com");

        Assertions.assertThrows(DataAccessException.class, () -> userDAO.createUser(userData));
    }

    @Test
    @Order(6)
    @DisplayName("USER: Clear Success")
    public void userClearSuccess() throws DataAccessException {

        UserData[] userDatas = {
                new UserData("emma", "pw", "emma@email.com"),
                new UserData("beans", "NOOOO", "bean.bean.com"),
                new UserData("Mario", "itsAMe", "mario@nintendo.com")
        };
        for (UserData userData : userDatas) {
            userDAO.createUser(userData);
        }

        userDAO.clear();

        Assertions.assertNull(userDAO.getUser("emma"));
        Assertions.assertNull(userDAO.getUser("beans"));
        Assertions.assertNull(userDAO.getUser("Mario"));
    }

    @Test
    @Order(7)
    @DisplayName("AUTH: Get Success")
    public void getAuthSuccess() throws DataAccessException {

        AuthData authData = new AuthData("authToken", "emma");

        authDAO.createAuth(authData);

        Assertions.assertDoesNotThrow(() -> authDAO.getAuth("authToken"));
        AuthData newAuthData = authDAO.getAuth("authToken");
        Assertions.assertEquals(authData, newAuthData);
        Assertions.assertEquals(authData.authToken(), newAuthData.authToken());
        Assertions.assertEquals(authData.username(), newAuthData.username());
    }

    @Test
    @Order(8)
    @DisplayName("AUTH: Get Null")
    public void getAuthNull() throws DataAccessException {
        Assertions.assertNull(authDAO.getAuth("authToken"), "Expected null, returned other");
    }

    @Test
    @Order(9)
    @DisplayName("AUTH: Create Success")
    public void createAuthSuccess() throws DataAccessException {
        AuthData authData = new AuthData("authToken", "emma");

        authDAO.createAuth(authData);

        Assertions.assertDoesNotThrow(() -> authDAO.getAuth("authToken"));
        AuthData newAuthData = authDAO.getAuth("authToken");
        Assertions.assertNotNull(newAuthData);
        Assertions.assertEquals(authData, newAuthData);
    }

    @Test
    @Order(10)
    @DisplayName("AUTH: Create Failure - Duplicate Auth")
    public void createAuthFailureDuplicateAuth() throws DataAccessException {
        AuthData authData = new AuthData("authToken", "emma");

        authDAO.createAuth(authData);

        Assertions.assertThrows(DataAccessException.class, () -> authDAO.createAuth(authData));
    }

    @Test
    @Order(11)
    @DisplayName("AUTH: Create Failure - Null Auth")
    public void createAuthFailureNullAuth() {
        AuthData authData = new AuthData("authToken", null);
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.createAuth(authData));
    }

    @Test
    @Order(12)
    @DisplayName("AUTH: Clear Success")
    public void authClearSuccess() throws DataAccessException {

        AuthData[] authDatas = {
                new AuthData("authToken", "emma"),
                new AuthData("authToken1", "beans"),
                new AuthData("authToken2", "Mario"),
        };
        for (AuthData authData : authDatas) {
            authDAO.createAuth(authData);
        }

        authDAO.clear();

        Assertions.assertNull(authDAO.getAuth("authToken"));
        Assertions.assertNull(authDAO.getAuth("authToken1"));
        Assertions.assertNull(authDAO.getAuth("authToken2"));
    }

    @Test
    @Order(13)
    @DisplayName("AUTH: Delete Auth Success")
    public void authDeleteAuthSuccess() throws DataAccessException {

        AuthData[] authDatas = {
                new AuthData("authToken", "emma"),
                new AuthData("authToken1", "beans"),
                new AuthData("authToken2", "Mario"),
        };
        for (AuthData authData : authDatas) {
            authDAO.createAuth(authData);
            authDAO.deleteAuth(authData.authToken());
        }

        Assertions.assertNull(authDAO.getAuth("authToken"));
        Assertions.assertNull(authDAO.getAuth("authToken1"));
        Assertions.assertNull(authDAO.getAuth("authToken2"));
    }

    @Test
    @Order(14)
    @DisplayName("GAME: Create Success")
    public void createGameSuccess() throws DataAccessException {
        GameData gameData = new GameData(1, null,
                null, "test", new ChessGame());
        gameDAO.createGame(gameData);

        gameDAO.getGame(1);

        Assertions.assertDoesNotThrow(() -> gameDAO.getGame(1));
        GameData newGameData = gameDAO.getGame(1);
        Assertions.assertNotNull(newGameData);
        Assertions.assertEquals(gameData, newGameData);
    }

    @Test
    @Order(15)
    @DisplayName("GAME: Create Failure - Duplicate")
    public void createGameFailureDuplicate() throws DataAccessException {
        GameData gameData = new GameData(1, null,
                null, "test", new ChessGame());

        gameDAO.createGame(gameData);

        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.createGame(gameData));
    }

    @Test
    @Order(16)
    @DisplayName("GAME: Get Success")
    public void getGameSuccess() throws DataAccessException {

        GameData gameData = new GameData(1, "peanut butter",
                "jelly", "test", new ChessGame());
        gameDAO.createGame(gameData);

        gameDAO.getGame(1);

        Assertions.assertDoesNotThrow(() -> gameDAO.getGame(1));
        GameData newGameData = gameDAO.getGame(1);
        Assertions.assertEquals(gameData, newGameData);
        Assertions.assertEquals(gameData.gameID(), newGameData.gameID());
        Assertions.assertEquals(gameData.whiteUsername(), newGameData.whiteUsername());
        Assertions.assertEquals(gameData.blackUsername(), newGameData.blackUsername());
        Assertions.assertEquals(gameData.gameName(), newGameData.gameName());
        Assertions.assertEquals(gameData.game(), newGameData.game());
    }

    @Test
    @Order(17)
    @DisplayName("GAME: Get Null")
    public void getGameNull() throws DataAccessException {
        Assertions.assertNull(gameDAO.getGame(10), "Expected null, returned other");
    }

    @Test
    @Order(18)
    @DisplayName("GAME: List Games Success")
    public void listGamesSuccess() throws DataAccessException {
        Collection<GameData> gameList = new ArrayList<>();

        GameData[] gameDatas = {
                new GameData(1, "peanut butter",
                        "jelly", "test", new ChessGame()),
                new GameData(2, "peanut butter",
                        "jelly", "test2", new ChessGame()),
                new GameData(3, "peanut butter",
                        "jelly", "test3", new ChessGame()),
                new GameData(4, "peanut butter",
                        "jelly", "test4", new ChessGame()),
        };

        for (GameData gameData : gameDatas) {
            gameList.add(gameData);
            gameDAO.createGame(gameData);
        }

        Collection<GameData> newGameList = gameDAO.listGames();

        Assertions.assertIterableEquals(gameList, newGameList);
    }

    @Test
    @Order(19)
    @DisplayName("GAME: List Games Empty")
    public void listGamesEmpty() throws DataAccessException {

        Collection<GameData> gameList = new ArrayList<>();

        Collection<GameData> newGameList = gameDAO.listGames();

        Assertions.assertIterableEquals(gameList, newGameList);
    }

    @Test
    @Order(20)
    @DisplayName("GAME: Update Success")
    public void updateGameSuccess() throws DataAccessException {
        GameData gameData = new GameData(1, null,null, "test", new ChessGame());
        gameDAO.createGame(gameData);

        GameData expectedGameData = new GameData(1, "peanut butter",
                null, "test", new ChessGame());

        String playerColor = "WHITE";

        Assertions.assertDoesNotThrow(() -> gameDAO.updateGame(playerColor,1, expectedGameData.whiteUsername()));
        gameDAO.updateGame(playerColor,1, expectedGameData.whiteUsername());
        GameData newGameData = gameDAO.getGame(1);
        Assertions.assertNotNull(newGameData);
        Assertions.assertEquals(expectedGameData, newGameData);
    }

    @Test
    @Order(20)
    @DisplayName("GAME: Update Wrong ID")
    public void updateGameWrongID() throws DataAccessException {
        GameData gameData = new GameData(1, null,null, "test", new ChessGame());
        gameDAO.createGame(gameData);

        String playerColor = "WHITE";

        Assertions.assertThrows(DataAccessException.class,
                () -> gameDAO.updateGame(playerColor,2, "peanut butter"));
    }
}
