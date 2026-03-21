package client;

import model.request.LoginRequest;
import model.request.RegisterRequest;
import serverfacade.ServerFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class ChessClient {

    private final ServerFacade server;
    private State state = State.LOGGEDOUT;
    private final Scanner scanner = new Scanner(System.in);

    public ChessClient(String url) {
        server = new ServerFacade(url);
    }

    private enum State {
        LOGGEDOUT,
        LOGGEDIN
    }

    public void run() {
        System.out.println("Welcome to CHESS 240!");
        padding();
        System.out.println(help());

        String command = "";

        while (!command.equals("quit")) {
            prompt();
            String input = scanner.nextLine();

            try {
                command = getCommand(input);
                System.out.print(command);
            } catch (Throwable ex) {
                var message = ex.toString();
                System.out.print(message);
            }
        }
        System.out.println("\nThanks for playing!");
    }

    private void prompt() {
        System.out.print("\n>>> ");
    }

    private void padding() {
        System.out.println("\n* * *\n");
    }

    private String getCommand(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = "help";
            if (tokens.length > 0) {
                cmd = tokens[0];
            }
            String[] parameters = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register();
                case "login" -> login();
                case "create" -> createGame(parameters);
                case "list" -> listGames();
                case "join" -> joinGame(parameters);
                case "observe" -> observeGame(parameters);
                case "logout" -> logout();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    // TODO: IMPLEMENT CLIENT CALLING

    private String register() {
        System.out.println("Please enter new username, password, and email in the following format:");
        System.out.println("[USERNAME] [PASSWORD] [EMAIL]");
        System.out.println("Or 'return' to go back");
        while (true) {
            prompt();
            String[] cred = scanner.nextLine().split(" ");
            if (cred.length == 3) {
                try {
                    RegisterRequest registerRequest = new RegisterRequest(cred[0], cred[1], cred[2]);
                    server.register(registerRequest);
                    state = State.LOGGEDIN;
                    return "Welcome, " + cred[0] + "!";
                } catch (Exception ex) {
                    throw new RuntimeException("Error: " + ex.getMessage());
                }
            }
            else if (cred[0].equals("return")){
                return "";
            }
            else {
                System.out.println("Missing username, password, or email. \nExpected: [USERNAME] [PASSWORD] [EMAIL]");
            }
        }

    }

    private String login() {

        System.out.println("Please enter username and password in the following format:");
        System.out.print("[USERNAME] [PASSWORD]");
        System.out.println("Or 'return' to go back");
        while (true) {
            prompt();
            String[] cred = scanner.nextLine().split(" ");
            if (cred.length == 2) {
                try {
                    LoginRequest loginRequest = new LoginRequest(cred[0], cred[1]);
                    server.login(loginRequest);
                    state = State.LOGGEDIN;
                    return "Welcome, " + cred[0] + "!";
                } catch (Exception ex) {
                    throw new RuntimeException("Error: " + ex.getMessage());
                }
            }
            else if (cred[0].equals("return")){
                return "";
            }
            else {
                System.out.println("Missing username, password, or email. \nExpected: [USERNAME] [PASSWORD] [EMAIL]");
            }
        }
    }

    private String createGame(String[] gameName) {
        return "";
    }

    private String listGames() {
        return "";
    }

    private String joinGame(String[] params) {
        return "";
    }

    private String observeGame(String[] id) {
        return "";
    }

    private String logout() {
        return "";
    }

    private String help() {
        if (state == State.LOGGEDOUT) {
            return """
                    COMMANDS:
                    * register -> Create Account
                    * login [USERNAME] [PASSWORD] -> Login as existing user
                    * help -> List possible commands
                    * quit -> Quit CHESS 240
                    """;
        }
        return """
                COMMANDS:
                * create [NAME] -> Make a new CHESS 240 game with name of choice
                * list -> List all existing CHESS 240 games
                * join [ID] [WHITE/BLACK] -> Join an existing CHESS 240 game
                * observe [ID] -> View an existing CHESS 240 game
                * logout -> Logout current user
                * quit -> Quit CHESS 240
                * help -> List possible commands
                """;
    }

    private void checkLoggedIn() throws RuntimeException {
        if (state == State.LOGGEDOUT) {
            throw new RuntimeException("Must be signed in to perform this action");
        }
    }
}
