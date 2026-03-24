package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import static ui.EscapeSequences.*;

public class GameplayUI {

    private final String sideBg = SET_TEXT_COLOR_DARK_GREY;
    private final String sideText = SET_TEXT_COLOR_LIGHT_GREY;
    private final String blackText = SET_TEXT_COLOR_BLACK;
    private final String whiteText = SET_TEXT_COLOR_WHITE;

    public void render(ChessBoard board, String color) {
        int step;
        int start;
        int end;

        if (color.equalsIgnoreCase("WHITE")) {
            whiteSide();
            step = -1;
            start = 8;
            end = 1;
        }
        else {
            blackSide();
            step = 1;
            start = 1;
            end = 8;

        }

        for (int i = start; i != end + step; i += step) {
            System.out.print(sideBg + sideText + " " +
                    i + " " + RESET_BG_COLOR + RESET_TEXT_COLOR);
            for (int j = 8; j >= 1; j--) {
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(pos);
                String symbol = "";
                String bgColor = "";
                String textColor = "";

                if ((i + j) % 2 == 0) {
                    bgColor = SET_BG_COLOR_LIGHT_GREY;
                }
                else {
                    bgColor = SET_BG_COLOR_DARK_GREEN;
                }

                if (piece == null) {
                    symbol = EMPTY;
                }
                else {
                    if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                        textColor = whiteText;
                    } else {
                        textColor = blackText;
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
        if (color.equalsIgnoreCase("WHITE")) { whiteSide();} else { blackSide(); }
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
}
