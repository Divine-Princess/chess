package client;

import serverfacade.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

public class ChessClient {

    private final ServerFacade server;
    private State state = State.LOGGEDOUT;

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

        Scanner scanner = new Scanner(System.in);
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
        System.out.println("Thanks for playing!");
    }

    private void prompt() {
        System.out.println("\n>>>");
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
                case "register" -> register(parameters);
                case "login" -> login(parameters);
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

    private String register(String[] params) {
        if (params.length >= 1) {
            state = State.LOGGEDIN;

        }
        return "";
    }

    private String login(String[] params) {
        return "";
    }

    private String createGame(String[] params) {
        return "";
    }

    private String listGames() {
        return "";
    }

    private String joinGame(String[] params) {
        return "";
    }

    private String observeGame(String[] params) {
        return "";
    }

    private String logout() {
        return "";
    }

    private String help() {
        if (state == State.LOGGEDOUT) {
            return """
                    * help -> List possible commands
                    * register [USERNAME] [PASSWORD] [EMAIL] -> Create Account
                    * login [USERNAME] [PASSWORD] -> Login as existing user
                    * quit -> Quit CHESS 240
                    """;
        }
        return """
                * create [NAME] -> Make a new CHESS 240 game with name of choice
                * list -> List all existing CHESS 240 games
                * join [ID] [WHITE/BLACK] -> Join an existing CHESS 240 game
                * observe [ID] -> View an existing CHESS 240 game
                * logout -> Logout current user
                * quit -> Quit CHESS 240
                * help -> List possible commands
                """;
    }
}
