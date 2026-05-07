package client;

import chess.ChessMove;
import chess.ChessPosition;

import static ui.EscapeSequences.RESET_TEXT_COLOR;

public class ChessGameClient extends ChessClient {

    public ChessGameClient(String url) {
        super(url);
    }

    protected String move(String[] moves) {
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

    protected ChessPosition parsePosition(String move) {
        int col = move.charAt(0) - 'a' + 1;
        int row = move.charAt(1) - '0';
        return new ChessPosition(row, col);
    }

    protected String highlight(String[] space) {
        checkInGame();
        if (!(space.length == 1)) {
            throw new RuntimeException(errorColor +
                    "Error: Incorrect format. Expected: highlight [COL/ROW] ex. highlight e3" + RESET_TEXT_COLOR);
        }

        gameUI.highlightMoves(parsePosition(space[0]));

        return "";
    }

    protected String leave() {
        checkInGame();
        ws.leave(authToken, currentGameID);
        currentGameID = 0;
        state = State.LOGGEDIN;
        System.out.println(help());
        return "";
    }
    protected String resign() {
        checkInGame();
        ws.resign(authToken, currentGameID);
        return "";
    }
    protected String redraw() {
        checkInGame();
        gameUI.redraw();

        return "";
    }
}
