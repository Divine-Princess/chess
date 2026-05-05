package client;

import chess.*;
import client.websocket.GameHandler;
import com.google.gson.Gson;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ui.EscapeSequences.*;

public class GameplayUI implements GameHandler {

    private final String sideBg = SET_BG_COLOR_DARK_GREY;
    private final String sideText = SET_TEXT_COLOR_MAGENTA;
    private String color;
    private ChessGame currentGame;


    private void render(ChessBoard board, Collection<ChessPosition> legalMoves, ChessPosition currPos) {
        int rowStep;
        int rowStart;
        int rowEnd;
        int colStep;
        int colStart;
        int colEnd;

        if (color == null || color.equalsIgnoreCase("WHITE")) {
            whiteSide();
            rowStep = -1;
            rowStart = 8;
            rowEnd = 1;
            colStep = 1;
            colStart = 1;
            colEnd = 8;
        }
        else {
            blackSide();
            rowStep = 1;
            rowStart = 1;
            rowEnd = 8;
            colStep = -1;
            colStart = 8;
            colEnd = 1;

        }

        for (int i = rowStart; i != rowEnd + rowStep; i += rowStep) {
            System.out.print(sideBg + sideText + " " +
                    i + " " + RESET_BG_COLOR + RESET_TEXT_COLOR);
            for (int j = colStart; j != colEnd + colStep; j += colStep) {
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(pos);
                String symbol;
                String bgColor;
                String textColor = "";

                if ((i + j) % 2 == 0) {
                    bgColor = SET_BG_COLOR_BLACK;
                    if (legalMoves.contains(pos)) {
                        bgColor = SET_BG_COLOR_DARK_GREEN;
                    } else if (pos.equals(currPos)) {
                        bgColor = SET_BG_COLOR_WHITE;
                    }
                }
                else {
                    bgColor = SET_BG_COLOR_LIGHT_GREY;
                    if (legalMoves.contains(pos)) {
                        bgColor = SET_BG_COLOR_GREEN;
                    } else if (pos.equals(currPos)) {
                        bgColor = SET_BG_COLOR_WHITE;
                    }
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
                    i + " " + RESET_BG_COLOR + RESET_TEXT_COLOR + "\n");
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
        this.currentGame = game;

        render(game.getBoard(), new ArrayList<>(), null);
    }

    @Override
    public void highlightMoves(ChessPosition position) {

        Collection<ChessMove> legalMoves = currentGame.validMoves(position);

        List<ChessPosition> endPos = new ArrayList<>();

        for (ChessMove move : legalMoves) {
            endPos.add(move.getEndPosition());
        }

        render(currentGame.getBoard(), endPos, position);
    }

    @Override
    public void redraw() {
        render(currentGame.getBoard(), new ArrayList<>(), null);
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
