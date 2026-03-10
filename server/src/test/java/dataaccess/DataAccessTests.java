package dataaccess;

import dataaccess.authdao.AuthDAO;
import dataaccess.authdao.MemoryAuthDAO;
import dataaccess.gamedao.GameDAO;
import dataaccess.gamedao.MemoryGameDAO;
import dataaccess.userdao.MemoryUserDAO;
import dataaccess.userdao.MySQLUserDAO;
import dataaccess.userdao.UserDAO;
import model.data.UserData;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;

public class DataAccessTests {

    UserDAO userDAO;

    @BeforeEach
    public void initialize() throws DataAccessException {
        DatabaseConfigurator db = new DatabaseConfigurator();
        userDAO = new MySQLUserDAO(db);
        userDAO.clear();
    }

    @Test
    @Order(1)
    @DisplayName("Get Success")
    public void getSuccess() throws DataAccessException {

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
    @DisplayName("Get Failure")
    public void getFailure() throws DataAccessException {
        Assertions.assertNull(userDAO.getUser("emma"), "Expected null, returned other");
    }

    @Test
    @Order(3)
    @DisplayName("Create Success")
    public void createSuccess() throws DataAccessException {
        UserData userData = new UserData("emma", "pw", "emma@email.com");

        userDAO.createUser(userData);

        Assertions.assertDoesNotThrow(() -> userDAO.getUser("emma"));
        UserData newUserData = userDAO.getUser("emma");
        Assertions.assertNotNull(newUserData);
        Assertions.assertEquals(userData, newUserData);
    }



//    @Test
//    @Order(1)
//    @DisplayName("Clear Success")
//    public void clearSuccess() {
//
//    }

}
