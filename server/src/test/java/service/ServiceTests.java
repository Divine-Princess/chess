package service;

import dataaccess.authDAO.MemoryAuthDAO;
import dataaccess.userDAO.MemoryUserDAO;
import model.data.UserData;
import model.request.RegisterRequest;
import model.result.RegisterResult;
import org.junit.jupiter.api.*;
import server.AlreadyTakenException;
import server.BadRequestException;

public class ServiceTests {

    private UserService testService;
    private RegisterRequest testRequest;
    private RegisterResult testResult;
    MemoryUserDAO testUserDAO;
    MemoryAuthDAO testAuthDAO;

    // Register Service Tests

    // Register Success:
    @Test
    @Order(1)
    @DisplayName("Register Success")
    public void registerSuccess() {
        // empty request
        testUserDAO = new MemoryUserDAO();
        testAuthDAO = new MemoryAuthDAO();
        testService = new UserService(testUserDAO, testAuthDAO);
        testRequest = new RegisterRequest("emma","1234","emma@email.com");
        testResult = testService.register(testRequest);

        Assertions.assertNotNull(testResult, "Expected RegisterRequest, returned null");
        Assertions.assertEquals(testRequest.username(), testResult.username(), "Returned wrong username");
        Assertions.assertFalse(testResult.authToken().isEmpty(), "AuthToken is empty!");
    }

    // Register Error: BadRequestException:
    @Test
    @Order(2)
    @DisplayName("Register Bad Request")
    public void registerBadRequest() {
        // empty request
        testUserDAO = new MemoryUserDAO();
        testAuthDAO = new MemoryAuthDAO();
        testService = new UserService(testUserDAO, testAuthDAO);
        RegisterRequest[] badRequests = {
                new RegisterRequest("", "", ""),
                new RegisterRequest("emma", "1234", ""),
                new RegisterRequest("emma", "", "emma@email.com"),
                new RegisterRequest("", "1234", "emma@email.com")
        };

        for (RegisterRequest badRequest : badRequests) {
            Assertions.assertThrows(BadRequestException.class, () -> testService.register(badRequest));
        }

    }

    // Register Error: AlreadyTakenException:
    @Test
    @Order(3)
    @DisplayName("Register Already Taken")
    public void registerAlreadyTaken() {
        testUserDAO = new MemoryUserDAO();
        testAuthDAO = new MemoryAuthDAO();
        testService = new UserService(testUserDAO, testAuthDAO);

        UserData[] testUsers = {
                new UserData("emma", "1234", "emma.email.com"),
                new UserData("beans", "NOOOO", "bean.bean.com"),
                new UserData("Mario", "itsAMe", "mario@nintendo.com")
        };

        for (UserData testUser : testUsers) {
            testUserDAO.createUser(testUser);
        }

        RegisterRequest[] existingUsers = {
                new RegisterRequest("emma", "1234", "emma.email.com"),
                new RegisterRequest("beans", "NOOOO", "bean.bean.com"),
                new RegisterRequest("Mario", "itsAMe", "mario@nintendo.com")
        };

        for (RegisterRequest existingUser : existingUsers) {
            Assertions.assertThrows(AlreadyTakenException.class, () -> testService.register(existingUser));
        }
    }



}