package client.websocket;

import chess.ChessGame;
import chess.ChessPosition;
import websocket.messages.ServerMessage;

public interface GameHandler {

    public void updateGame(ChessGame game);

    public void printMessage(String message, String playerColor);

    public void highlightMoves(ChessPosition position);

    public void redraw();


}
