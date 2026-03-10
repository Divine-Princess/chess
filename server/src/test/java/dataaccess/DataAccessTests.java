package dataaccess;

import dataaccess.authdao.AuthDAO;
import dataaccess.authdao.MySQLAuthDAO;
import dataaccess.userdao.MySQLUserDAO;
import dataaccess.userdao.UserDAO;
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

}
