package client;

import chess.ChessMove;
import chess.ChessPosition;
import client.websocket.GameHandler;
import client.websocket.WebSocketFacade;
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
    private final String errorColor = SET_TEXT_COLOR_YELLOW;
    private final String mainColor = SET_TEXT_COLOR_MAGENTA;
    private final String inputColor = SET_TEXT_COLOR_BLUE;
    private final WebSocketFacade ws = new WebSocketFacade();
    private final String url;
    private final GameHandler gameUI = new GameplayUI();
    private int currentGameID;
    private String playerColor;

    public ChessClient(String url) {
        server = new ServerFacade(url);
        this.url = url;
    }

    private enum State {
        LOGGEDOUT,
        LOGGEDIN,
        INGAME
    }

    public void run() {
        System.out.println(SET_TEXT_COLOR_WHITE + SET_TEXT_BOLD + "Welcome to CHESS 240!" +
        RESET_TEXT_COLOR + RESET_TEXT_BOLD_FAINT);
        padding();
        System.out.println(help());

        String command = "";

        while (!command.equals("quit")) {
            if (!(state == State.INGAME)) {
                prompt();
            }
            String input = scanner.nextLine();

            try {
                command = getCommand(input);
                if (!command.isEmpty()) { padding(); }
                if (!(command.equals("quit"))) { System.out.print(command); }
            } catch (Throwable ex) {
                var message = ex.toString();
                System.out.print(errorColor + message + RESET_TEXT_COLOR);
            }
        }
        System.out.println(SET_TEXT_COLOR_WHITE + SET_TEXT_BOLD +
                "\nThanks for playing!" + RESET_TEXT_COLOR + RESET_TEXT_BOLD_FAINT);
    }

    private void prompt() {
        System.out.print(inputColor + "\n\n>>> " + RESET_TEXT_COLOR);
    }

    private void padding() {
        System.out.println(SET_TEXT_COLOR_WHITE +
                "\n" + WHITE_QUEEN + WHITE_QUEEN + WHITE_QUEEN + "\n"
                + RESET_TEXT_COLOR);
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
                case "register" -> register() + help();
                case "login" -> login() + help();
                case "create" -> createGame(parameters);
                case "list" -> listGames();
                case "join" -> joinGame(parameters);
                case "observe" -> observeGame(parameters);
                case "logout" -> logout();
                case "move" -> move(parameters);
                case "highlight" -> highlight(parameters);
                case "hl" -> highlight(parameters);
                case "leave" -> leave();
                case "resign" -> resign();
                case "redraw" -> redraw();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private String register() {
        System.out.println(mainColor +
                "\n\uD83D\uDF9B Please enter new username, password, " +
                "and email in the following format: \uD83D\uDF9B");
        System.out.println(inputColor + SET_TEXT_BOLD
                + "USERNAME PASSWORD EMAIL" + RESET_TEXT_BOLD_FAINT + RESET_TEXT_COLOR);
        System.out.println(mainColor + "Or " + inputColor +
                        "return" + mainColor + " to go back" + inputColor);
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
                    return "Welcome, " + username + "!\n";
                } catch (Exception ex) {
                    throw new RuntimeException(errorColor +
                            "Error: " + ex.getMessage() + RESET_TEXT_COLOR);
                }
            }
            else if (cred[0].equals("return")){
                return "";
            }
            else if (cred.length < 3){
                System.out.println(SET_TEXT_BOLD + errorColor + "Missing username, password, or email."
                                + mainColor + RESET_TEXT_BOLD_FAINT + "\nExpected:" + SET_TEXT_BOLD
                                + inputColor + " USERNAME PASSWORD EMAIL" + RESET_TEXT_BOLD_FAINT
                                + RESET_TEXT_COLOR);
                System.out.println(mainColor + "Enter " +  inputColor +
                        "return" + mainColor + " to go back" + RESET_TEXT_COLOR);
            }
            else {
                System.out.println(SET_TEXT_BOLD + errorColor + "Incorrect format." + RESET_TEXT_BOLD_FAINT
                                 + mainColor + "\nExpected:" + SET_TEXT_BOLD
                                 + inputColor + " USERNAME PASSWORD EMAIL"
                                 + RESET_TEXT_COLOR + RESET_TEXT_BOLD_FAINT);
                System.out.println(mainColor + "Enter " +  inputColor +
                        "return" + mainColor + " to go back" + RESET_TEXT_COLOR);
            }
        }

    }

    private String login() {
        if (state == State.LOGGEDIN) {
            return errorColor + "\nAlready logged in" + RESET_TEXT_COLOR;
        }
        System.out.println(mainColor +
                "\n\uD83D\uDF9B Please enter username and password in the following format: \uD83D\uDF9B");
        System.out.println(inputColor + SET_TEXT_BOLD + "USERNAME PASSWORD"
                            + RESET_TEXT_BOLD_FAINT + RESET_TEXT_COLOR);
        System.out.println(mainColor + "Or " + inputColor +
                "return" + mainColor + " to go back" + RESET_TEXT_COLOR);
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
                    return "Welcome back, " + username + "!\n";
                } catch (Exception ex) {
                    throw new RuntimeException(errorColor +
                            "Error: Incorrect Username or Password" + RESET_TEXT_COLOR);
                }
            }
            else if (cred[0].equals("return")){
                return "";
            }
            else if (cred.length < 2){
                System.out.println(SET_TEXT_BOLD + errorColor + "Missing username or password."
                        + mainColor + RESET_TEXT_BOLD_FAINT + "\nExpected:" + SET_TEXT_BOLD
                        + inputColor + " USERNAME PASSWORD EMAIL" + RESET_TEXT_BOLD_FAINT
                        + RESET_TEXT_COLOR);
                System.out.println(mainColor + "Enter " +  inputColor +
                        "return" + mainColor + " to go back" + RESET_TEXT_COLOR);
            }
            else {
                System.out.println(SET_TEXT_BOLD + errorColor + "Incorrect format." + RESET_TEXT_BOLD_FAINT
                        + mainColor + "\nExpected:" + SET_TEXT_BOLD
                        + inputColor + " USERNAME PASSWORD"
                        + RESET_TEXT_COLOR + RESET_TEXT_BOLD_FAINT);
                System.out.println(mainColor + "Enter " +  inputColor +
                        "return" + mainColor + " to go back" + RESET_TEXT_COLOR);
            }
        }
    }

    private String createGame(String[] gameName) {
        checkLoggedIn();
        if (gameName.length < 1) {
            throw new RuntimeException(errorColor + "Error: No name given" + RESET_TEXT_COLOR);
        }
        String name = gameName[0];
        if (gameName.length > 1) {
            name = String.join(" ", gameName);
        }

        checkLoggedIn();
        try {
            CreateGameRequest request = new CreateGameRequest(authToken, name);
            server.createGame(request);
            return mainColor + "'" + name + "'" + " successfully created." + RESET_TEXT_COLOR;
        } catch (Exception ex) {
            throw new RuntimeException(errorColor + "Error: " + ex.getMessage() + RESET_TEXT_COLOR);
        }


    }

    private String listGames() {
        checkLoggedIn();
        try {
            ListGamesRequest request = new ListGamesRequest(authToken);
            ListGamesResult result = server.listGames(request);

            Collection<GameData> gamesList = result.games();
            this.games = new HashMap<>();

            int gameNum = 1;

            StringBuilder stringBuilder = new StringBuilder();
            for (GameData game : gamesList) {
                String whiteUser = game.whiteUsername();
                String blackUser = game.blackUsername();
                if (whiteUser == null) {
                    whiteUser = "None";
                }
                if (blackUser == null) {
                    blackUser = "None";
                }
                games.put(gameNum, game.gameID());
                stringBuilder.append(inputColor)
                        .append(SET_TEXT_BOLD)
                        .append(gameNum)
                        .append(RESET_TEXT_BOLD_FAINT)
                        .append(": ")
                        .append(mainColor)
                        .append("'")
                        .append(game.gameName())
                        .append("' - ")
                        .append(" Players: ")
                        .append(whiteUser)
                        .append(" (white), ")
                        .append(blackUser)
                        .append(" (black)\n")
                        .append(RESET_TEXT_COLOR);
                gameNum += 1;
            }
            if (games.isEmpty()) {
                return errorColor +
                        "Error: No games to list! Please create a new game to play."
                        + RESET_TEXT_COLOR;
            }

            return stringBuilder.toString();
        }
        catch (Exception ex) {
            throw new RuntimeException(errorColor + "Error: " + ex.getMessage() + RESET_TEXT_COLOR);
        }
    }

    private String joinGame(String[] params) {
        checkLoggedIn();
        if (!(params.length == 2)) {
            throw new RuntimeException(errorColor +
                    "Error: Incorrect format. Expected: join [NUMBER] [white/black]" + RESET_TEXT_COLOR);
        }

        playerColor = params[1].toUpperCase();

        if (!(playerColor.equals("WHITE") || playerColor.equals("BLACK"))) {
            throw new RuntimeException(errorColor + "Error: Color must be white or black" + RESET_TEXT_COLOR);
        }

        if (games == null || games.isEmpty()) {
            throw new RuntimeException(errorColor +
                    "Error: Please list games before attempting to join." + RESET_TEXT_COLOR);
        }
        int gameNum;

        try {
            gameNum = Integer.parseInt(params[0]);

        } catch (Exception ex) {
            throw new RuntimeException(errorColor + "Error: '" + params[0] + "' " + "not a number" + RESET_TEXT_COLOR);
        }

        if (!(games.containsKey(gameNum))) {
            throw new RuntimeException(errorColor + "Error: Game does not exist" + RESET_TEXT_COLOR);
        }

        currentGameID = games.get(gameNum);

        try {
            JoinGameRequest joinGameRequest = new JoinGameRequest(authToken, playerColor, currentGameID);
            server.joinGame(joinGameRequest);

            ws.connect(url, gameUI, authToken, currentGameID, playerColor);
            state = State.INGAME;

            return "";
        }
        catch (Exception ex) {
            throw new RuntimeException(errorColor + "Error: " + ex.getMessage() + RESET_TEXT_COLOR);
        }

    }

    private String observeGame(String[] num) {
        checkLoggedIn();
        if (!(num.length == 1)) {
            throw new RuntimeException(errorColor +
                    "Error: Incorrect format. Expected: observe [NUMBER]" + RESET_TEXT_COLOR);
        }

        if (games == null || games.isEmpty()) {
            throw new RuntimeException(errorColor +
                    "Error: Please list games before attempting to observe." + RESET_TEXT_COLOR);
        }
        try {
            int gameNum = Integer.parseInt(num[0]);

            if (!(games.containsKey(gameNum))) {
                throw new RuntimeException(errorColor + "Error: Game does not exist" + RESET_TEXT_COLOR);
            }

            int gameID = games.get(gameNum);

            System.out.print("\n");
            ws.connect(url, gameUI, authToken, gameID, playerColor);
            state = State.INGAME;

            return "";

        } catch (Exception ex) {
            throw new RuntimeException(errorColor + "Error: '" + num[0] + "' " + "not a number" + RESET_TEXT_COLOR);
        }
    }

    private String logout() {
        if (state == State.LOGGEDOUT) {
            return errorColor + "Already logged out" + RESET_TEXT_COLOR;
        }
        System.out.println(mainColor + "Log out as " + username + "? Y/N" + RESET_TEXT_COLOR);
        String input = scanner.nextLine();
        if (input.equalsIgnoreCase("Y")) {
            try {
                LogoutRequest request = new LogoutRequest(authToken);
                server.logout(request);
                state = State.LOGGEDOUT;
                this.username = null;
                return mainColor + "Logged out successfully" + RESET_TEXT_COLOR;

            } catch (Exception ex) {
                throw new RuntimeException(errorColor + "Error: " + ex.getMessage() + RESET_TEXT_COLOR);
            }

        }
        else if (input.equalsIgnoreCase("N")) {
            return " ";
        }
        else {
            throw new RuntimeException(errorColor + "Error: Incorrect format. Expected: Y/N" + RESET_TEXT_COLOR);
        }

    }

    private String move(String[] moves) {
        checkInGame();
        if (!(moves.length == 2)) {
            throw new RuntimeException(errorColor +
                    "Error: Incorrect format. Expected: move [START SPACE] [END SPACE] ex. move e3 e5"
                    + RESET_TEXT_COLOR);
        }

        String start = moves[0];
        String end = moves[1];

        ChessPosition startPos = parsePosition(start);
        ChessPosition endPos = parsePosition(end);

        ChessMove move = new ChessMove(startPos, endPos, null);

        ws.makeMove(move, authToken, currentGameID);

        return "";
    }

    private ChessPosition parsePosition(String move) {
        int col = move.charAt(0) - 'a' + 1;
        int row = move.charAt(1) - '0';
        return new ChessPosition(row, col);
    }

    private String highlight(String[] space) {
        checkInGame();
        if (!(space.length == 1)) {
            throw new RuntimeException(errorColor +
                    "Error: Incorrect format. Expected: highlight [COL/ROW] ex. highlight e3" + RESET_TEXT_COLOR);
        }

        gameUI.highlightMoves(parsePosition(space[0]));

        return "";
    }

    private String leave() {
        checkInGame();
        ws.leave(authToken, currentGameID);
        currentGameID = 0;
        state = State.LOGGEDIN;
        System.out.println(help());
        return "";
    }

    private String resign() {
        checkInGame();
        ws.resign(authToken, currentGameID);
        return "";
    }

    private String redraw() {
        checkInGame();
        gameUI.redraw();

        return "";
    }

    private String help() {
        if (state == State.LOGGEDOUT) {
            return SET_TEXT_BOLD + mainColor + " \uD83D\uDF9B COMMANDS: \uD83D\uDF9B\n"
                    + inputColor + RESET_TEXT_BOLD_FAINT +
                    "♢ register " + mainColor + "🡒 Create Account\n" + inputColor +
                    "♢ login " + mainColor + "🡒 Login as existing user\n"
                    + inputColor +
                    "♢ help " + mainColor + "🡒 List possible commands\n"
                    + inputColor +
                    "♢ quit " + mainColor + "🡒 Quit CHESS 240" + RESET_TEXT_COLOR;
        } else if (state == State.INGAME) {
            return SET_TEXT_BOLD + mainColor + "  \uD83D\uDF9B COMMANDS: \uD83D\uDF9B\n"
                    + inputColor + RESET_TEXT_BOLD_FAINT +
                    "♢ move [COL/ROW] [COL/ROW] " + mainColor + "🡒 Move chess piece using valid moves. " +
                    "Ex. move b3 f7 \n"
                    + inputColor +
                    "♢ highlight (or hl) [COL/ROW] " + mainColor + "🡒 Highlight legal moves of single chess piece\n"
                    + inputColor +
                    "♢ leave " + mainColor + "🡒 Leave current game\n"
                    + inputColor +
                    "♢ resign " + mainColor + "🡒 Forfeit current game\n"
                    + inputColor +
                    "♢ redraw " + mainColor + "🡒 Redraw chessboard\n"
                    + inputColor +
                    "♢ help " + mainColor + "🡒 List possible commands\n";
        }
        return SET_TEXT_BOLD + mainColor + "  \uD83D\uDF9B COMMANDS: \uD83D\uDF9B\n"
                + inputColor + RESET_TEXT_BOLD_FAINT +
                "♢ create [NAME] " + mainColor + "🡒 Make a new CHESS 240 game with name of choice\n"
                + inputColor +
                "♢ list " + mainColor + "🡒 List all existing CHESS 240 games\n"
                + inputColor +
                "♢ join [GAME #] [white/black] " + mainColor + "🡒 Join an existing CHESS 240 game\n"
                + inputColor +
                "♢ observe [GAME #] " + mainColor + "🡒 View an existing CHESS 240 game\n"
                + inputColor +
                "♢ logout " + mainColor + "🡒 Log out current user\n"
                + inputColor +
                "♢ help " + mainColor + "🡒 List possible commands\n"
                + inputColor +
                "♢ quit " + mainColor + "🡒 Quit CHESS 240" + RESET_TEXT_COLOR;
    }

    private String clear() {
        try {
            server.clear();
            this.authToken = null;
            this.username = null;
            this.state = State.LOGGEDOUT;
            this.currentGameID = 0;
            return "Server Cleared";
        }
        catch (Exception ex) {
            throw new RuntimeException("Unable to clear");
        }
    }


    private void checkLoggedIn() throws RuntimeException {
        if (state == State.LOGGEDOUT) {
            throw new RuntimeException(errorColor + "Error: Must be signed in to perform this action"
            + RESET_TEXT_COLOR);
        } else if (state == State.INGAME) {
            throw new RuntimeException(errorColor + "Error: Must be out of game to perform this action"
            + RESET_TEXT_COLOR);
        }
    }

    private void checkInGame() throws RuntimeException {
        if (state == State.LOGGEDOUT || state == State.LOGGEDIN) {
            throw new RuntimeException(errorColor + "Error: Must be in game to perform this action"
                    + RESET_TEXT_COLOR);
        }
    }
}
