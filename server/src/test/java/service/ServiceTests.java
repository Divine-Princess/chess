package service;

import dataaccess.authdao.MemoryAuthDAO;
import dataaccess.gamedao.MemoryGameDAO;
import dataaccess.userdao.MemoryUserDAO;
import model.data.UserData;
import model.request.*;
import model.result.*;
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

    @Test
    @Order(1)
    @DisplayName("Register Success")
    public void registerSuccess() {
        RegisterRequest testRegisterRequest = new RegisterRequest("emma", "1234", "emma@email.com");
        RegisterResult testRegisterResult = testUserService.register(testRegisterRequest);

        Assertions.assertNotNull(testRegisterResult, "Expected RegisterRequest, returned null");
        Assertions.assertEquals(testRegisterRequest.username(), testRegisterResult.username(), "Returned wrong username");
        Assertions.assertFalse(testRegisterResult.authToken().isEmpty(), "AuthToken is empty!");
    }

    @Test
    @Order(2)
    @DisplayName("Register Bad Request")
    public void registerBadRequest() {
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

        CreateGameRequest testReq = new CreateGameRequest(authToken, "beepbeep");

        CreateGameResult testResult = testGameService.createGame(testReq);

        Assertions.assertNotNull(testResult, "Expected CreateGameRequest, returned null");
    }

    @Test
    @Order(11)
    @DisplayName("Create Game Unauthorized")
    public void createGameUnauthorized() {
        CreateGameRequest emptyRequest = new CreateGameRequest("", "");

        Assertions.assertThrows(UnauthorizedException.class, () -> testGameService.createGame(emptyRequest));
    }

    @Test
    @Order(12)
    @DisplayName("Create Game Bad Request")
    public void createGameBadRequest() {
        RegisterRequest testRegisterRequest = new RegisterRequest("emma", "1234", "emma@email.com");
        testUserService.register(testRegisterRequest);

        LoginRequest testLoginRequest = new LoginRequest("emma", "1234");
        LoginResult testLoginResult = testUserService.login(testLoginRequest);

        String authToken = testLoginResult.authToken();

        CreateGameRequest badRequest = new CreateGameRequest(authToken, null);

        Assertions.assertThrows(BadRequestException.class, () -> testGameService.createGame(badRequest));
    }

    @Test
    @Order(13)
    @DisplayName("Join Game Success")
    public void joinGameSuccess() {
        RegisterRequest testRegisterRequest = new RegisterRequest("emma", "1234", "emma@email.com");
        testUserService.register(testRegisterRequest);

        LoginRequest testLoginRequest = new LoginRequest("emma", "1234");
        LoginResult testLoginResult = testUserService.login(testLoginRequest);

        String authToken = testLoginResult.authToken();

        // create player 2
        RegisterRequest testRegisterRequest1 = new RegisterRequest("connor", "5678", "connor@email.com");
        testUserService.register(testRegisterRequest1);

        LoginRequest testLoginRequest1 = new LoginRequest("connor", "5678");
        LoginResult testLoginResult1 = testUserService.login(testLoginRequest1);

        String authToken1 = testLoginResult1.authToken();

        CreateGameRequest testCreateReq = new CreateGameRequest(authToken, "1.0");

        CreateGameResult testCreateResult = testGameService.createGame(testCreateReq);

        int gameID = testCreateResult.gameID();

        JoinGameRequest[] requests = {
                new JoinGameRequest(authToken, "WHITE", gameID),
                new JoinGameRequest(authToken1,"BLACK", gameID)
        };

        for (JoinGameRequest request : requests) {
            JoinGameResult testResult = testGameService.joinGame(request);
            Assertions.assertNotNull(testResult, "Expected ListGamesRequest, returned null");
        }
    }

    @Test
    @Order(14)
    @DisplayName("Join Game Already Taken")
    public void joinAlreadyTaken() {
        RegisterRequest testRegisterRequest = new RegisterRequest("emma", "1234", "emma@email.com");
        testUserService.register(testRegisterRequest);

        LoginRequest testLoginRequest = new LoginRequest("emma", "1234");
        LoginResult testLoginResult = testUserService.login(testLoginRequest);

        String authToken = testLoginResult.authToken();

        RegisterRequest testRegisterRequest1 = new RegisterRequest("connor", "5678", "connor@email.com");
        testUserService.register(testRegisterRequest1);

        // create player 2
        LoginRequest testLoginRequest1 = new LoginRequest("connor", "5678");
        LoginResult testLoginResult1 = testUserService.login(testLoginRequest1);

        String authToken1 = testLoginResult1.authToken();

        CreateGameRequest testCreateReq = new CreateGameRequest(authToken, "beepbeep");

        CreateGameResult testCreateResult = testGameService.createGame(testCreateReq);

        int gameID = testCreateResult.gameID();

        JoinGameRequest request = new JoinGameRequest(authToken, "WHITE", gameID);

        testGameService.joinGame(request);

        JoinGameRequest request1 = new JoinGameRequest(authToken1,"WHITE", gameID);

        Assertions.assertThrows(AlreadyTakenException.class, () -> testGameService.joinGame(request1));

    }

    @Test
    @Order(15)
    @DisplayName("Clear Success")
    public void testClear() {
        testUserDAO = new MemoryUserDAO();
        testAuthDAO = new MemoryAuthDAO();
        testGameDAO = new MemoryGameDAO();
        testUserService = new UserService(testUserDAO, testAuthDAO);
        testGameService = new GameService(testGameDAO, testAuthDAO);
        testAuthService = new AuthService(testAuthDAO);

        UserData[] testUsers = {
                new UserData("emma", "1234", "emma.email.com"),
                new UserData("beans", "NOOOO", "bean.bean.com"),
                new UserData("Mario", "itsAMe", "mario@nintendo.com")
        };

        for (UserData testUser : testUsers) {
            testUserDAO.createUser(testUser);
        }

        testUserService.clear();
        testGameService.clear();
        testAuthService.clear();

    }



}