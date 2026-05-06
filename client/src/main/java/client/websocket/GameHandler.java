package client.websocket;

import chess.ChessGame;
import chess.ChessPosition;

public interface GameHandler {

    void updateGame(ChessGame game);

    void printMessage(String message, String playerColor);

    void highlightMoves(ChessPosition position);

    void redraw();


}
