package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BishopMove {

    public static Collection<ChessMove> getBishopMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> possibleMoves = new ArrayList<>();
        ChessPiece piece = board.getPiece(myPosition);
        ChessGame.TeamColor myColor = piece.getTeamColor();
        int currRow = myPosition.getRow();
        int currCol = myPosition.getColumn();

        getLegalMoves(board, myColor, possibleMoves, currRow, currCol);

        return possibleMoves;
    }

    private static void addMove(List<ChessMove> possibleMoves, int currRow, int currCol, int nextRow, int nextCol) {
        possibleMoves.add(new ChessMove(new ChessPosition(currRow, currCol), new ChessPosition(nextRow, nextCol), null));
    }

    private static void getLegalMoves(ChessBoard board, ChessGame.TeamColor myColor, List<ChessMove> possibleMoves, int currRow, int currCol) {
        List<List<Integer>> vectors = List.of(List.of(1,1), List.of(1,-1), List.of(-1,1), List.of(-1,-1));

        for (List<Integer> vector : vectors) {
            int movRow = vector.getFirst();
            int movCol = vector.get(1);
            int nextRow = currRow + movRow;
            int nextCol = currCol + movCol;
            while (0 < nextRow && nextRow <= 8 && 0 < nextCol && nextCol <= 8) {
                ChessPosition nextPosition = new ChessPosition(nextRow, nextCol);
                ChessPiece otherPiece = board.getPiece(nextPosition);

                if (otherPiece == null) {
                    addMove(possibleMoves, currRow, currCol, nextRow, nextCol);
                }
                else {
                    ChessGame.TeamColor otherPieceColor = otherPiece.getTeamColor();
                    if (!myColor.equals(otherPieceColor)) {
                        addMove(possibleMoves, currRow, currCol, nextRow, nextCol);
                    }
                    break;

                }
                nextRow += movRow;
                nextCol += movCol;
            }
        }
    }

}
