package chess;

import java.util.List;

public class KnightMove {

    public static void getKnightMoves(List<ChessMove> possibleMoves, ChessBoard board, ChessGame.TeamColor myColor, int currRow, int currCol) {

        List<List<Integer>> jumps = List.of(List.of(-1,2), List.of(1,2), List.of(-2,1), List.of(2,1), List.of(-2,-1), List.of(2,-1), List.of(-1,-2), List.of(1,-2));

        for (List<Integer> jump : jumps) {
            int movRow = jump.getFirst();
            int movCol = jump.get(1);
            int nextRow = currRow + movRow;
            int nextCol = currCol + movCol;

            if (0 < nextRow && nextRow <= 8 && 0 < nextCol && nextCol <= 8) {
                System.out.println(nextRow + " " + nextCol);
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
            }

        }
    }
    private static void addMove(List<ChessMove> possibleMoves, int currRow, int currCol, int nextRow, int nextCol) {
        possibleMoves.add(new ChessMove(new ChessPosition(currRow, currCol), new ChessPosition(nextRow, nextCol), null));
    }
}