package client.websocket;

import chess.ChessGame;
import websocket.messages.ServerMessage;

public interface GameHandler {

    public void updateGame(ChessGame game);

    public void printMessage(String message, String playerColor);


}
