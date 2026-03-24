package client;

import chess.ChessBoard;
import model.data.GameData;
import model.request.*;
import model.result.ListGamesResult;
import model.result.LoginResult;
import model.result.RegisterResult;
import serverfacade.ServerFacade;

import java.util.*;

import static ui.EscapeSequences.*;

public class ChessClient {

    private final ServerFacade server;
    private State state = State.LOGGEDOUT;
    private final Scanner scanner = new Scanner(System.in);
    private String authToken;
    private String username;
    private HashMap<Integer, Integer> games;

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
        System.out.println("\n" + WHITE_QUEEN + WHITE_QUEEN + WHITE_QUEEN + "\n");
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
                // TODO! REMOVE AFTER TESTING!!!
                case "clear" -> clear();
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private String register() {
        System.out.println("Please enter new username, password, and email in the following format:");
        System.out.println("USERNAME PASSWORD EMAIL");
        System.out.println("Or 'return' to go back");
        while (true) {
            prompt();
            String[] cred = scanner.nextLine().split(" ");
            if (cred.length == 3) {
                try {
                    RegisterRequest registerRequest = new RegisterRequest(cred[0], cred[1], cred[2]);
                    RegisterResult result = server.register(registerRequest);
                    authToken = result.authToken();
                    state = State.LOGGEDIN;
                    username = result.username();
                    return "Welcome, " + username + "!";
                } catch (Exception ex) {
                    throw new RuntimeException("Error: " + ex.getMessage());
                }
            }
            else if (cred[0].equals("return")){
                return "";
            }
            else if (cred.length < 3){
                System.out.println(SET_TEXT_BOLD + SET_TEXT_COLOR_RED + "Missing username, password, or email."
                                + SET_TEXT_COLOR_YELLOW +
                                "\nExpected:" + SET_TEXT_COLOR_LIGHT_GREY + " USERNAME PASSWORD EMAIL");
            }
            else {
                System.out.println(SET_TEXT_BOLD + SET_TEXT_COLOR_RED + "Incorrect format." +
                        SET_TEXT_COLOR_YELLOW + "\nExpected:" + " USERNAME PASSWORD EMAIL");
            }
        }

    }

    private String login() {
        if (state == State.LOGGEDIN) {
            return "Already logged in";
        }
        System.out.println("Please enter username and password in the following format:");
        System.out.println("USERNAME PASSWORD");
        System.out.println("Or 'return' to go back");
        while (true) {
            prompt();
            String[] cred = scanner.nextLine().split(" ");
            if (cred.length == 2) {
                try {
                    LoginRequest loginRequest = new LoginRequest(cred[0], cred[1]);
                    LoginResult result = server.login(loginRequest);
                    state = State.LOGGEDIN;
                    username = result.username();
                    authToken = result.authToken();
                    return "Welcome back, " + username + "!";
                } catch (Exception ex) {
                    throw new RuntimeException("Error: Incorrect Username or Password");
                }
            }
            else if (cred[0].equals("return")){
                return "";
            }
            else if (cred.length < 2){
                System.out.println("Missing username or password. \nExpected: USERNAME PASSWORD");
            }
            else {
                System.out.println("Incorrect format. \nExpected: USERNAME PASSWORD");
            }
        }
    }

    private String createGame(String[] gameName) {
        System.out.println(Arrays.toString(gameName));
        String name = gameName[0];
        if (gameName.length > 1) {
            name = String.join(" ", gameName);
        }
        checkLoggedIn();
        try {
            CreateGameRequest request = new CreateGameRequest(authToken, name);
            server.createGame(request);
            return "'" + name + "'" + " successfully created.";
        } catch (Exception ex) {
            throw new RuntimeException("Error: " + ex.getMessage());
        }

        //throw new RuntimeException("Error: No name given");
    }

    private String listGames() {
        checkLoggedIn();
        try {
            ListGamesRequest request = new ListGamesRequest(authToken);
            ListGamesResult result = server.listGames(request);

            Collection<GameData> gamesList = result.games();
            this.games = new HashMap<>();

            int gameNum = 1;
            for (GameData game : gamesList) {
                games.put(gameNum, game.gameID());
                gameNum += 1;
                System.out.println(gameNum + ": " + game.gameName() +
                        " Players: " + game.whiteUsername() + " (white), " + game.blackUsername() + " (black)");
            }
            return "";
        }
        catch (Exception ex) {
            throw new RuntimeException("Error: " + ex.getMessage());
        }
    }

    private String joinGame(String[] params) {
        if (!(params.length == 2)) {
            throw new RuntimeException("Incorrect format. Expected: [NUMBER] [white/black]");
        }
        GameplayUI ui = new GameplayUI();
        int gameNum = Integer.parseInt(params[0]);
        String color = params[1].toUpperCase();
        if (!(color.equals("WHITE") || color.equals("BLACK"))) {
            throw new RuntimeException("Color must be white or black");
        }
        int gameID = games.get(gameNum);

        ChessBoard board = new ChessBoard();
        board.resetBoard();
        ui.render(board, color);

        return "";
    }

    private String observeGame(String[] num) {
        return "";
    }

    private String logout() {
        if (state == State.LOGGEDOUT) {
            return "Already logged out";
        }
        System.out.println("Log out as " + username + "? Y/N");
        String input = scanner.nextLine();
        if (input.equalsIgnoreCase("Y")) {
            try {
                LogoutRequest request = new LogoutRequest(authToken);
                server.logout(request);
                state = State.LOGGEDOUT;
                this.username = null;
                return "Logged out successfully";

            } catch (Exception ex) {
                throw new RuntimeException("Error: " + ex.getMessage());
            }

        }
        else if (input.equalsIgnoreCase("N")) {
            return "";
        }
        else {
            throw new RuntimeException("Incorrect format. Expected: Y/N");
        }

    }

    private String help() {
        if (state == State.LOGGEDOUT) {
            return SET_TEXT_BOLD + SET_TEXT_COLOR_MAGENTA + " \uD83D\uDF9B COMMANDS: \uD83D\uDF9B\n"
                    + SET_TEXT_COLOR_BLUE + RESET_TEXT_BOLD_FAINT +
                    "♢ register " + SET_TEXT_COLOR_MAGENTA + "🡒 Create Account\n" + SET_TEXT_COLOR_BLUE +
                    "♢ login [USERNAME] [PASSWORD] " + SET_TEXT_COLOR_MAGENTA + "🡒 Login as existing user\n"
                    + SET_TEXT_COLOR_BLUE +
                    "♢ help " + SET_TEXT_COLOR_MAGENTA + "🡒 List possible commands\n"
                    + SET_TEXT_COLOR_BLUE +
                    "♢ quit " + SET_TEXT_COLOR_MAGENTA + "🡒 Quit CHESS 240" + RESET_TEXT_COLOR;
        }
        return """
                COMMANDS:
                * create [NAME] 🡒 Make a new CHESS 240 game with name of choice
                * list 🡒 List all existing CHESS 240 games
                * join [GAME #] [white/black] 🡒 Join an existing CHESS 240 game
                * observe [GAME #] 🡒 View an existing CHESS 240 game
                * logout 🡒 Logout current user
                * quit 🡒 Quit CHESS 240
                * help 🡒 List possible commands
                """;
    }

    // TODO: REMOVE AFTER TESTING!!!

    private String clear() {
        try {
            server.clear();
            this.authToken = null;
            this.username = null;
            this.state = State.LOGGEDOUT;
            return "Server Cleared";
        }
        catch (Exception ex) {
            throw new RuntimeException("Unable to clear");
        }
    }


    private void checkLoggedIn() throws RuntimeException {
        if (state == State.LOGGEDOUT) {
            throw new RuntimeException("Must be signed in to perform this action");
        }
    }
}
