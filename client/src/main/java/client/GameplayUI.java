package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import client.websocket.GameHandler;
import com.google.gson.Gson;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import static ui.EscapeSequences.*;

public class GameplayUI implements GameHandler {

    private final String sideBg = SET_BG_COLOR_DARK_GREY;
    private final String sideText = SET_TEXT_COLOR_MAGENTA;
    private String color;


    public void render(ChessBoard board) {
        int step;
        int start;
        int end;
//        String whiteSquare;
//        String blackSquare;

        if (color == null || color.equalsIgnoreCase("WHITE")) {
            whiteSide();
            step = 1;
            start = 1;
            end = 8;
        }
        else {
            blackSide();
            step = -1;
            start = 8;
            end = 1;

        }

        for (int i = start; i != end + step; i += step) {
            System.out.print(sideBg + sideText + " " +
                    (9-i) + " " + RESET_BG_COLOR + RESET_TEXT_COLOR);
            for (int j = start; j != end + step; j += step) {
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(pos);
                String symbol;
                String bgColor;
                String textColor = "";

                if ((i + j) % 2 == 0) {
                    bgColor = SET_BG_COLOR_LIGHT_GREY;
                }
                else {
                    bgColor = SET_BG_COLOR_BLACK;
                }

                if (piece == null) {
                    symbol = EMPTY;
                }
                else {
                    if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                        textColor = SET_TEXT_COLOR_BLUE;
                    } else {
                        textColor = SET_TEXT_COLOR_YELLOW;
                    }
                    symbol = switch (piece.getPieceType()) {
                        case PAWN -> BLACK_PAWN;
                        case ROOK -> BLACK_ROOK;
                        case KNIGHT -> BLACK_KNIGHT;
                        case BISHOP -> BLACK_BISHOP;
                        case QUEEN -> BLACK_QUEEN;
                        case KING -> BLACK_KING;
                    };
                }
                System.out.print(bgColor + textColor + symbol + RESET_TEXT_COLOR + RESET_BG_COLOR);
            }
            System.out.print(sideBg + sideText + " " +
                    (9-i) + " " + RESET_BG_COLOR + RESET_TEXT_COLOR + "\n");
        }
        if (color == null || color.equalsIgnoreCase("WHITE")) { whiteSide();} else { blackSide(); }
    }


    private void whiteSide() {
        System.out.print(sideBg + sideText + "   " +
                " \u2009a\u2009 " + " \u2009b\u2009 " + " \u2009c\u2009 " + " \u2009d\u2009 " +
                " \u2009e\u2009 " + " \u2009f\u2009 " + " \u2009g\u2009 " + " \u2009h\u2009 " + "   " +
                RESET_BG_COLOR + RESET_TEXT_COLOR + "\n");
    }

    private void blackSide() {
        System.out.print(sideBg + sideText + "   " +
                " \u2009h\u2009 " + " \u2009g\u2009 " + " \u2009f\u2009 " + " \u2009e\u2009 " +
                " \u2009d\u2009 " + " \u2009c\u2009 " + " \u2009b\u2009 " + " \u2009a\u2009 " + "   " +
                RESET_BG_COLOR + RESET_TEXT_COLOR + "\n");
    }

    @Override
    public void updateGame(ChessGame game) {
        render(game.getBoard());
    }

    @Override
    public void printMessage(String message, String playerColor) {
        ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
        this.color = playerColor;
        if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
           LoadGameMessage toPrint = new Gson().fromJson(message, LoadGameMessage.class);
           updateGame(toPrint.getGame());
        } else if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
            NotificationMessage toPrint = new Gson().fromJson(message, NotificationMessage.class);
            System.out.println(toPrint.getMessage());
        } else {
            ErrorMessage toPrint = new Gson().fromJson(message, ErrorMessage.class);
            System.out.println(toPrint.getMessage());
        }

    }
}
