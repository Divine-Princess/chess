package dataaccess;

import dataaccess.authdao.AuthDAO;
import dataaccess.authdao.MySQLAuthDAO;
import dataaccess.userdao.MySQLUserDAO;
import dataaccess.userdao.UserDAO;
import model.data.AuthData;
import model.data.UserData;
import org.junit.jupiter.api.*;


public class DataAccessTests {

    UserDAO userDAO;
    AuthDAO authDAO;

    @BeforeEach
    public void initialize() throws DataAccessException {
        DatabaseConfigurator db = new DatabaseConfigurator();
        userDAO = new MySQLUserDAO(db);
        authDAO = new MySQLAuthDAO(db);
        userDAO.clear();
        authDAO.clear();
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
    @DisplayName("USER: Get Failure")
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
    public void createUserFailureNullUser() throws DataAccessException {
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
    @DisplayName("AUTH: Get Failure")
    public void getAuthFailure() throws DataAccessException {
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
    public void createAuthFailureNullAuth() throws DataAccessException {
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
    @Order(12)
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

}
