package client;

import model.request.*;
import model.result.*;
import org.junit.jupiter.api.*;
import server.Server;
import serverfacade.ServerFacade;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private static ServerFacade facade2;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
        facade2 = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }


    /* TODO -> IMPLEMENT POS/NEG TESTS FOR EACH SERVER FACADE METHOD */

    @BeforeEach
    public void setup() {
        facade.clear();
    }

    @Test
    @Order(1)
    @DisplayName("1 Client Register Success")
    public void registerSuccess() {

        RegisterRequest testRegisterRequest = new RegisterRequest("emma", "1234", "emma.email.com");

        RegisterResult testRegisterResult = facade.register(testRegisterRequest);

        Assertions.assertNotNull(testRegisterResult, "Expected RegisterRequest, returned null");
        Assertions.assertEquals(testRegisterRequest.username(), testRegisterResult.username(), "Returned wrong username");
        Assertions.assertFalse(testRegisterResult.authToken().isEmpty(), "AuthToken is empty!");
    }

    @Test
    @Order(2)
    @DisplayName("1 Client Register Failure")
    public void registerFailure() {

        RegisterRequest[] badRequests = {
                new RegisterRequest("", "", ""),
                new RegisterRequest("emma", "1234", ""),
                new RegisterRequest("emma", "", "emma@email.com"),
                new RegisterRequest("", "1234", "emma@email.com")
        };

        for (RegisterRequest badRequest : badRequests) {
            var ex = Assertions.assertThrows(RuntimeException.class, () -> facade.register(badRequest));
            Assertions.assertEquals("Client Error", ex.getMessage());
        }
    }

    @Test
    @Order(3)
    @DisplayName("1 Client Register Already Taken")
    public void registerAlreadyTaken() {
        RegisterRequest[] existingUsers = {
                new RegisterRequest("emma", "1234", "emma.email.com"),
                new RegisterRequest("beans", "NOOOO", "bean.bean.com"),
                new RegisterRequest("Mario", "itsAMe", "mario@nintendo.com")
        };

        for (RegisterRequest existingUser : existingUsers) {
            facade.register(existingUser);
        }

        RegisterRequest[] newUsers = {
                new RegisterRequest("emma", "1234", "emma.email.com"),
                new RegisterRequest("beans", "NOOOO", "bean.bean.com"),
                new RegisterRequest("Mario", "itsAMe", "mario@nintendo.com")
        };

        for (RegisterRequest newUser : newUsers) {
            var ex = Assertions.assertThrows(RuntimeException.class, () -> facade.register(newUser));
            Assertions.assertEquals("Client Error", ex.getMessage());
        }
    }


    @Test
    @Order(4)
    @DisplayName("1 Client Login Success")
    public void loginSuccess() {
        RegisterRequest testRegisterRequest = new RegisterRequest("emma", "1234", "emma@email.com");
        facade.register(testRegisterRequest);

        LoginRequest testLoginRequest = new LoginRequest("emma", "1234");
        LoginResult testLoginResult = facade.login(testLoginRequest);

        Assertions.assertNotNull(testLoginResult, "Expected LoginRequest, returned null");
        Assertions.assertEquals(testLoginRequest.username(), testLoginResult.username(), "Returned wrong username");
        Assertions.assertFalse(testLoginResult.authToken().isEmpty(), "AuthToken is empty!");
    }

    @Test
    @Order(5)
    @DisplayName("1 Client Login Failure")
    public void loginFailure() {
        LoginRequest[] badRequests = {
                new LoginRequest("", ""),
                new LoginRequest("emma",""),
                new LoginRequest("", "1234")
        };

        for (LoginRequest badRequest : badRequests) {
            var ex = Assertions.assertThrows(RuntimeException.class, () -> facade.login(badRequest));
            Assertions.assertEquals("Client Error", ex.getMessage());
        }
    }

    @Test
    @Order(6)
    @DisplayName("1 Client Logout Success")
    public void logoutSuccess() {
        RegisterRequest testRegisterRequest = new RegisterRequest("emma", "1234", "emma@email.com");
        facade.register(testRegisterRequest);

        LoginRequest testLoginRequest = new LoginRequest("emma", "1234");
        LoginResult testLoginResult = facade.login(testLoginRequest);

        String authToken = testLoginResult.authToken();

        LogoutRequest testLogoutRequest = new LogoutRequest(authToken);
        LogoutResult testLogoutResult = facade.logout(testLogoutRequest);

        Assertions.assertNotNull(testLogoutResult, "Expected LogoutRequest, returned null");
    }

    @Test
    @Order(7)
    @DisplayName("1 Client Logout Failure")
    public void logoutFailure() {
        LogoutRequest emptyRequest = new LogoutRequest("");

        var ex = Assertions.assertThrows(RuntimeException.class, () -> facade.logout(emptyRequest));
        Assertions.assertEquals("Client Error", ex.getMessage());
    }

    @Test
    @Order(8)
    @DisplayName("1 Client List Games Success")
    public void listGamesSuccess() {
        RegisterRequest testRegisterRequest = new RegisterRequest("emma", "1234", "emma@email.com");
        facade.register(testRegisterRequest);

        LoginRequest testLoginRequest = new LoginRequest("emma", "1234");
        LoginResult testLoginResult = facade.login(testLoginRequest);

        String authToken = testLoginResult.authToken();

        ListGamesRequest testListReq = new ListGamesRequest(authToken);

        ListGamesResult testListResult = facade.listGames(testListReq);

        Assertions.assertNotNull(testListResult, "Expected ListGamesRequest, returned null");
        Assertions.assertNotNull(testListResult.games(), "Expected type:Collection, returned null");
    }

    @Test
    @Order(9)
    @DisplayName("1 Client List Games Failure")
    public void listGamesFailure() {
        ListGamesRequest testListReq = new ListGamesRequest("");

        var ex = Assertions.assertThrows(RuntimeException.class, () -> facade.listGames(testListReq));
        Assertions.assertEquals("Client Error", ex.getMessage());
    }

    @Test
    @Order(10)
    @DisplayName("1 Client Create Game Success")
    public void createGameSuccess() {
        RegisterRequest testRegisterRequest = new RegisterRequest("emma", "1234", "emma@email.com");
        facade.register(testRegisterRequest);

        LoginRequest testLoginRequest = new LoginRequest("emma", "1234");
        LoginResult testLoginResult = facade.login(testLoginRequest);

        String authToken = testLoginResult.authToken();

        CreateGameRequest testReq = new CreateGameRequest(authToken, "beepbeep");

        CreateGameResult testResult = facade.createGame(testReq);

        Assertions.assertNotNull(testResult, "Expected CreateGameRequest, returned null");
    }

    @Test
    @Order(11)
    @DisplayName("1 Client Create Game Unauthorized")
    public void createGameUnauthorized() {
        CreateGameRequest emptyRequest = new CreateGameRequest("", "");

        var ex = Assertions.assertThrows(RuntimeException.class, () -> facade.createGame(emptyRequest));
        Assertions.assertEquals("Client Error", ex.getMessage());
    }

    @Test
    @Order(12)
    @DisplayName("1 Client Create Game Bad Request")
    public void createGameBadRequest() {
        RegisterRequest testRegisterRequest = new RegisterRequest("emma", "1234", "emma@email.com");
        facade.register(testRegisterRequest);

        LoginRequest testLoginRequest = new LoginRequest("emma", "1234");
        LoginResult testLoginResult = facade.login(testLoginRequest);

        String authToken = testLoginResult.authToken();

        CreateGameRequest badRequest = new CreateGameRequest(authToken, null);

        var ex = Assertions.assertThrows(RuntimeException.class, () -> facade.createGame(badRequest));
        Assertions.assertEquals("Client Error", ex.getMessage());
    }

    @Test
    @Order(13)
    @DisplayName("2 Client Join Game Success")
    public void joinGameSuccess() {
        RegisterRequest testRegisterRequest = new RegisterRequest("emma", "1234", "emma@email.com");
        facade.register(testRegisterRequest);

        LoginRequest testLoginRequest = new LoginRequest("emma", "1234");
        LoginResult testLoginResult = facade.login(testLoginRequest);

        String authToken = testLoginResult.authToken();

        // create player 2
        RegisterRequest testRegisterRequest1 = new RegisterRequest("connor", "5678", "connor@email.com");
        facade2.register(testRegisterRequest1);

        LoginRequest testLoginRequest1 = new LoginRequest("connor", "5678");
        LoginResult testLoginResult1 = facade2.login(testLoginRequest1);

        String authToken1 = testLoginResult1.authToken();

        CreateGameRequest testCreateReq = new CreateGameRequest(authToken, "1.0");

        CreateGameResult testCreateResult = facade.createGame(testCreateReq);

        int gameID = testCreateResult.gameID();


        JoinGameRequest request1 = new JoinGameRequest(authToken, "WHITE", gameID);
        JoinGameRequest request2 = new JoinGameRequest(authToken1,"BLACK", gameID);

        JoinGameResult testResult1 = facade.joinGame(request1);
        Assertions.assertNotNull(testResult1, "Expected JoinGameRequest, returned null");
        JoinGameResult testResult2 = facade2.joinGame(request2);
        Assertions.assertNotNull(testResult2, "Expected JoinGameRequest, returned null");
    }

    @Test
    @Order(14)
    @DisplayName("2 Client Join Game Already Taken")
    public void joinAlreadyTaken() {
        RegisterRequest testRegisterRequest = new RegisterRequest("emma", "1234", "emma@email.com");
        facade.register(testRegisterRequest);

        LoginRequest testLoginRequest = new LoginRequest("emma", "1234");
        LoginResult testLoginResult = facade.login(testLoginRequest);

        String authToken = testLoginResult.authToken();

        // create player 2
        RegisterRequest testRegisterRequest1 = new RegisterRequest("connor", "5678", "connor@email.com");
        facade2.register(testRegisterRequest1);

        LoginRequest testLoginRequest1 = new LoginRequest("connor", "5678");
        LoginResult testLoginResult1 = facade2.login(testLoginRequest1);

        String authToken1 = testLoginResult1.authToken();

        CreateGameRequest testCreateReq = new CreateGameRequest(authToken, "beepbeep");

        CreateGameResult testCreateResult = facade.createGame(testCreateReq);

        int gameID = testCreateResult.gameID();

        JoinGameRequest request = new JoinGameRequest(authToken, "WHITE", gameID);

        facade.joinGame(request);

        JoinGameRequest request1 = new JoinGameRequest(authToken1,"WHITE", gameID);

        var ex = Assertions.assertThrows(RuntimeException.class, () -> facade2.joinGame(request1));
        Assertions.assertEquals("Client Error", ex.getMessage());
    }

    @Test
    @Order(15)
    @DisplayName("Clear Success")
    public void testClear() {
        RegisterRequest testRegisterRequest = new RegisterRequest("emma", "1234", "emma@email.com");
        facade.register(testRegisterRequest);

        ClearResult testResult = facade.clear();
        Assertions.assertNotNull(testResult, "Expected testResult, returned null");
    }
}
