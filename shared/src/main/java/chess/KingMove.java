package chess;

import java.util.List;

public class KingMove {

    public static void getKingMoves(List<ChessMove> possibleMoves, ChessBoard board, ChessGame.TeamColor myColor, int currRow, int currCol) {

        List<List<Integer>> moves = List.of(List.of(-1,1), List.of(0,1), List.of(1,1), List.of(-1,0), List.of(1,0), List.of(-1,-1), List.of(0,-1), List.of(1,-1));

        for (List<Integer> move : moves) {
            int movRow = move.getFirst();
            int movCol = move.get(1);
            int nextRow = currRow + movRow;
            int nextCol = currCol + movCol;

            if (0 < nextRow && nextRow <= 8 && 0 < nextCol && nextCol <= 8) {
                ChessPosition nextPosition = new ChessPosition(nextRow, nextCol);
                ChessPiece otherPiece = board.getPiece(nextPosition);
                if (otherPiece == null) {
                    addMove(possibleMoves, currRow, currCol, nextRow, nextCol);
                } else {
                    ChessGame.TeamColor otherPieceColor = otherPiece.getTeamColor();
                    if (!myColor.equals(otherPieceColor)) {
                        addMove(possibleMoves, currRow, currCol, nextRow, nextCol);
                    }
                }
            } else {
                break;
            }
        }
    }
    private static void addMove(List<ChessMove> possibleMoves, int currRow, int currCol, int nextRow, int nextCol) {
        possibleMoves.add(new ChessMove(new ChessPosition(currRow, currCol), new ChessPosition(nextRow, nextCol), null));
    }
}
