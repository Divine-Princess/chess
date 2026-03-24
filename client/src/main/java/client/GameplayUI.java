package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import static ui.EscapeSequences.*;

public class GameplayUI {

    public void render(ChessBoard board, String color) {
        for (int i = 1; i <= 8; i++) {
            System.out.print(SET_BG_COLOR_MAGENTA + SET_TEXT_COLOR_WHITE + " " +
                    i + " " + RESET_BG_COLOR + RESET_TEXT_COLOR);
            for (int j = 1; j <= 8; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(pos);
                String symbol = "";
                String bg_color = "";
                String text_color = "";

                if ((i + j) % 2 == 0) {
                    bg_color = SET_BG_COLOR_YELLOW;
                }
                else {
                    bg_color = SET_BG_COLOR_BLUE;
                }

                if (piece == null) {
                    symbol = EMPTY;
                }
                else {
                    switch (piece.getPieceType()) {
                        case PAWN:
                            symbol = BLACK_PAWN;
                            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                                text_color = SET_TEXT_COLOR_WHITE;
                            } else {
                                text_color = SET_TEXT_COLOR_BLACK;
                            }
                            break;
                        case ROOK:
                            symbol = BLACK_ROOK;
                            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                                text_color = SET_TEXT_COLOR_WHITE;
                            } else {
                                text_color = SET_TEXT_COLOR_BLACK;
                            }
                            break;
                        case KNIGHT:
                            symbol = BLACK_KNIGHT;
                            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                                text_color = SET_TEXT_COLOR_WHITE;
                            } else {
                                text_color = SET_TEXT_COLOR_BLACK;
                            }
                            break;
                        case BISHOP:
                            symbol = BLACK_BISHOP;
                            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                                text_color = SET_TEXT_COLOR_WHITE;
                            } else {

                                text_color = SET_TEXT_COLOR_BLACK;
                            }
                            break;
                        case QUEEN:
                            symbol = BLACK_QUEEN;
                            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                                text_color = SET_TEXT_COLOR_WHITE;
                            } else {

                                text_color = SET_TEXT_COLOR_BLACK;
                            }
                            break;
                        case KING:
                            symbol = BLACK_KING;
                            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                                text_color = SET_TEXT_COLOR_WHITE;
                            } else {
                                text_color = SET_TEXT_COLOR_BLACK;
                            }
                            break;
                        }
                    }
                System.out.print(bg_color + text_color + symbol + RESET_TEXT_COLOR + RESET_BG_COLOR);
            }
            System.out.print(SET_BG_COLOR_MAGENTA + SET_TEXT_COLOR_WHITE + " " +
                    i + " " + RESET_BG_COLOR + RESET_TEXT_COLOR + "\n");
        }
    }
}
