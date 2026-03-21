package client;

import serverfacade.ServerFacade;

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
        String userInput = "";

        while (!userInput.equals("quit")) {
            prompt();
            String input = scanner.nextLine();


        }

    }

    private void prompt() {
        System.out.println("\n>>>");
    }

    private void padding() {
        System.out.println("\n* * *\n");
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
