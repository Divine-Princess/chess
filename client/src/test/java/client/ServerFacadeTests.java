package client;

import org.junit.jupiter.api.*;
import server.Server;
import serverfacade.ServerFacade;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

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

    }


}
