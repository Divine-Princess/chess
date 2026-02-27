package service;

import dataaccess.authDAO.MemoryAuthDAO;
import dataaccess.gameDAO.MemoryGameDAO;
import dataaccess.userDAO.MemoryUserDAO;
import model.data.UserData;
import model.request.ListGamesRequest;
import model.request.LoginRequest;
import model.request.LogoutRequest;
import model.request.RegisterRequest;
import model.result.ListGamesResult;
import model.result.LoginResult;
import model.result.LogoutResult;
import model.result.RegisterResult;
import org.junit.jupiter.api.*;
import server.AlreadyTakenException;
import server.BadRequestException;
import server.UnauthorizedException;

public class ServiceTests {

    private UserService testUserService;
    private GameService testGameService;
    private AuthService testAuthService;
    private MemoryUserDAO testUserDAO;
    private MemoryAuthDAO testAuthDAO;
    private MemoryGameDAO testGameDAO;

    @BeforeEach
    public void initialize() {
        testUserDAO = new MemoryUserDAO();
        testAuthDAO = new MemoryAuthDAO();
        testGameDAO = new MemoryGameDAO();
        testUserService = new UserService(testUserDAO, testAuthDAO);
        testGameService = new GameService(testGameDAO, testAuthDAO);
        testAuthService = new AuthService(testAuthDAO);
    }

    // Register Success:
    @Test
    @Order(1)
    @DisplayName("Register Success")
    public void registerSuccess() {
        // empty request
        RegisterRequest testRegisterRequest = new RegisterRequest("emma", "1234", "emma@email.com");
        RegisterResult testRegisterResult = testUserService.register(testRegisterRequest);

        Assertions.assertNotNull(testRegisterResult, "Expected RegisterRequest, returned null");
        Assertions.assertEquals(testRegisterRequest.username(), testRegisterResult.username(), "Returned wrong username");
        Assertions.assertFalse(testRegisterResult.authToken().isEmpty(), "AuthToken is empty!");
    }

    // Register Error: BadRequestException:
    @Test
    @Order(2)
    @DisplayName("Register Bad Request")
    public void registerBadRequest() {
        // empty request
        RegisterRequest[] badRequests = {
                new RegisterRequest("", "", ""),
                new RegisterRequest("emma", "1234", ""),
                new RegisterRequest("emma", "", "emma@email.com"),
                new RegisterRequest("", "1234", "emma@email.com")
        };

        for (RegisterRequest badRequest : badRequests) {
            Assertions.assertThrows(BadRequestException.class, () -> testUserService.register(badRequest));
        }

    }

    // Register Error: AlreadyTakenException:
    @Test
    @Order(3)
    @DisplayName("Register Already Taken")
    public void registerAlreadyTaken() {
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
            Assertions.assertThrows(AlreadyTakenException.class, () -> testUserService.register(existingUser));
        }
    }


    @Test
    @Order(4)
    @DisplayName("Login Success")
    public void loginSuccess() {
        // empty request
        RegisterRequest testRegisterRequest = new RegisterRequest("emma", "1234", "emma@email.com");
        testUserService.register(testRegisterRequest);

        LoginRequest testLoginRequest = new LoginRequest("emma", "1234");
        LoginResult testLoginResult = testUserService.login(testLoginRequest);

        Assertions.assertNotNull(testLoginResult, "Expected LoginRequest, returned null");
        Assertions.assertEquals(testLoginRequest.username(), testLoginResult.username(), "Returned wrong username");
        Assertions.assertFalse(testLoginResult.authToken().isEmpty(), "AuthToken is empty!");
    }

    @Test
    @Order(5)
    @DisplayName("Login Bad Request")
    public void loginBadRequest() {
        // empty request
        LoginRequest[] badRequests = {
                new LoginRequest("", ""),
                new LoginRequest("emma",""),
                new LoginRequest("", "1234")
        };

        for (LoginRequest badRequest : badRequests) {
            Assertions.assertThrows(BadRequestException.class, () -> testUserService.login(badRequest));
        }

    }


    @Test
    @Order(6)
    @DisplayName("Logout Success")
    public void logoutSuccess() {
        // empty request
        RegisterRequest testRegisterRequest = new RegisterRequest("emma", "1234", "emma@email.com");
        testUserService.register(testRegisterRequest);

        LoginRequest testLoginRequest = new LoginRequest("emma", "1234");
        LoginResult testLoginResult = testUserService.login(testLoginRequest);

        String authToken = testLoginResult.authToken();

        LogoutRequest testLogoutRequest = new LogoutRequest(authToken);
        LogoutResult testLogoutResult = testUserService.logout(testLogoutRequest);

        Assertions.assertNotNull(testLogoutResult, "Expected LogoutRequest, returned null");

    }

    @Test
    @Order(7)
    @DisplayName("Logout Unauthorized")
    public void logoutUnauthorized() {
        LogoutRequest emptyRequest = new LogoutRequest("");

        Assertions.assertThrows(UnauthorizedException.class, () -> testUserService.logout(emptyRequest));
    }


    @Test
    @Order(8)
    @DisplayName("List Games Success")
    public void listGamesSuccess() {
        RegisterRequest testRegisterRequest = new RegisterRequest("emma", "1234", "emma@email.com");
        testUserService.register(testRegisterRequest);

        LoginRequest testLoginRequest = new LoginRequest("emma", "1234");
        LoginResult testLoginResult = testUserService.login(testLoginRequest);

        String authToken = testLoginResult.authToken();

        ListGamesRequest testListReq = new ListGamesRequest(authToken);

        ListGamesResult testListResult = testGameService.listGames(testListReq);

        Assertions.assertNotNull(testListResult, "Expected ListGamesRequest, returned null");
        Assertions.assertNotNull(testListResult.games(), "Expected type:Collection, returned null");
    }

    @Test
    @Order(9)
    @DisplayName("List Games Unauthorized")
    public void listGamesUnauthorized() {
//        RegisterRequest testRegisterRequest = new RegisterRequest("emma", "1234", "emma@email.com");
//        testUserService.register(testRegisterRequest);
//
//        LoginRequest testLoginRequest = new LoginRequest("emma", "1234");
//        LoginResult testLoginResult = testUserService.login(testLoginRequest);
//
//        String authToken = testLoginResult.authToken();

        ListGamesRequest testListReq = new ListGamesRequest("");

        Assertions.assertThrows(UnauthorizedException.class, () -> testGameService.listGames(testListReq));
    }

    @Test
    @Order(10)
    @DisplayName("Create Game Success")
    public void createGameSuccess() {
        RegisterRequest testRegisterRequest = new RegisterRequest("emma", "1234", "emma@email.com");
        testUserService.register(testRegisterRequest);

        LoginRequest testLoginRequest = new LoginRequest("emma", "1234");
        LoginResult testLoginResult = testUserService.login(testLoginRequest);

        String authToken = testLoginResult.authToken();

         testListReq = new ListGamesRequest(authToken);

        ListGamesResult testListResult = testGameService.listGames(testListReq);

        Assertions.assertNotNull(testListResult, "Expected ListGamesRequest, returned null");
        Assertions.assertNotNull(testListResult.games(), "Expected type:Collection, returned null");
    }



//    @Test
//    @Order(4)
//    @DisplayName("Clear Success")
//    public void testClear() {
//        testUserDAO = new MemoryUserDAO();
//        testAuthDAO = new MemoryAuthDAO();
//        testGameDAO = new MemoryGameDAO();
//        testUserService = new UserService(testUserDAO, testAuthDAO);
//        testGameService = new GameService(testGameDAO, testAuthDAO);
//        testAuthService = new AuthService(testAuthDAO);
//
//        UserData[] testUsers = {
//                new UserData("emma", "1234", "emma.email.com"),
//                new UserData("beans", "NOOOO", "bean.bean.com"),
//                new UserData("Mario", "itsAMe", "mario@nintendo.com")
//        };
//
//        for (UserData testUser : testUsers) {
//            testUserDAO.createUser(testUser);
//        }
//
//        ClearRequest clearRequest = new ClearRequest();
//        testUserService.clear(clearRequest);
//        testGameService.clear(clearRequest);
//        testAuthService.clear(clearRequest);
//
//
//
//    }



}