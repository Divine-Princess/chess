package client;

import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import model.request.LoginRequest;
import model.result.LoginResult;

import java.util.Scanner;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

public class ChessGameClient{
    private ChessClient chessClient;
    final String errorColor = SET_TEXT_COLOR_YELLOW;
    private final String mainColor = SET_TEXT_COLOR_MAGENTA;
    private final String inputColor = SET_TEXT_COLOR_BLUE;

    public ChessGameClient(ChessClient chessClient) {
        this.chessClient = chessClient;
    }

    protected String move(String[] moves) {
        chessClient.checkInGame();
        if ((moves.length < 2) || (moves.length > 3)) {
            throw new RuntimeException(errorColor +
                    "Error: Incorrect format. Expected: move [START SPACE] [END SPACE] ex. move e3 e5\n" +
                    "OR move [START SPACE] [END SPACE] [PROMOTION PIECE] ex. move f7 f8 knight"
                    + RESET_TEXT_COLOR);
        }

        char c1 = moves[0].charAt(0);
        char c2 = moves[1].charAt(0);
        if (Character.isDigit(c1) || Character.isDigit(c2)) {
            throw new RuntimeException(errorColor +
                    "Error: Incorrect format. Expected: move [START SPACE] [END SPACE] ex. move e3 e5\n" +
                    "OR move [START SPACE] [END SPACE] [PROMOTION PIECE] ex. move f7 f8 knight" + RESET_TEXT_COLOR);
        }

        ChessPiece.PieceType promotionPiece = null;
        if (moves.length == 3) {
            switch (moves[2].toLowerCase()) {
                case "knight" -> promotionPiece = ChessPiece.PieceType.KNIGHT;
                case "queen" -> promotionPiece = ChessPiece.PieceType.QUEEN;
                case "rook" -> promotionPiece = ChessPiece.PieceType.ROOK;
                case "bishop" -> promotionPiece = ChessPiece.PieceType.BISHOP;
                default -> promotionPiece = null;
            }
        }

        String start = moves[0];
        String end = moves[1];

        ChessPosition startPos = parsePosition(start);
        ChessPosition endPos = parsePosition(end);

        ChessMove move = new ChessMove(startPos, endPos, promotionPiece);

        chessClient.getWs().makeMove(move, chessClient.getAuth(), chessClient.getCurrentGameID());

        return "";
    }

    protected ChessPosition parsePosition(String move) {
        int col = move.charAt(0) - 'a' + 1;
        int row = move.charAt(1) - '0';
        return new ChessPosition(row, col);
    }

    protected String highlight(String[] space) {
        chessClient.checkInGame();
        if (!(space.length == 1)) {
            throw new RuntimeException(errorColor +
                    "Error: Incorrect format. Expected: highlight [COL/ROW] ex. highlight e3\n" + RESET_TEXT_COLOR);
        }
        char c = space[0].charAt(0);
        if (Character.isDigit(c)) {
            throw new RuntimeException(errorColor +
                    "Error: Incorrect format. Expected: highlight [COL/ROW] ex. highlight e3\n" + RESET_TEXT_COLOR);
        }

        chessClient.getUi().highlightMoves(parsePosition(space[0]));

        return "";
    }

    protected String leave() {
        chessClient.checkInGame();
        chessClient.getWs().leave(chessClient.getAuth(), chessClient.getCurrentGameID());
        chessClient.setState(ChessClient.State.LOGGEDIN);
        System.out.println(chessClient.help());
        return "";
    }
    protected String resign() {
        System.out.println(SET_TEXT_BOLD + mainColor + "Are you sure you want to resign? (y/n)"
                + RESET_TEXT_BOLD_FAINT + RESET_TEXT_COLOR);
        while (true) {
            Scanner scanner = chessClient.getScanner();
            String response = scanner.nextLine();
            if (response.equals("y")) {
                try {
                    chessClient.checkInGame();
                    chessClient.getWs().resign(chessClient.getAuth(), chessClient.getCurrentGameID());
                    return "";
                } catch (Exception ex) {
                    throw new RuntimeException(SET_TEXT_BOLD + errorColor +"Could not resign"+ RESET_TEXT_BOLD_FAINT
                            + RESET_TEXT_COLOR);
                }
            }
            else if (response.equals("n")){
                return "";
            }
            else {
                System.out.println(SET_TEXT_BOLD + mainColor + "Are you sure you want to resign? (y/n)"
                        + RESET_TEXT_BOLD_FAINT + RESET_TEXT_COLOR);
            }
        }
    }
    protected String redraw() {
        chessClient.checkInGame();
        chessClient.getUi().redraw();

        return "";
    }
}
