package client;

import chess.*;

public class ClientMain {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        String url = "http://localhost:8080";

        try {
            new ChessClient(url).run();

        } catch (Throwable ex) {
            throw new RuntimeException();
        }
    }
}
