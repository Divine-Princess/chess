package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BishopMove {

    public BishopMove() {

    }

    public static Collection<ChessMove> getBishopMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> possibleMoves = new ArrayList<ChessMove>();
        int currRow = myPosition.getRow();
        int currCol = myPosition.getColumn();

        List<List<Integer>> vectors = List.of(List.of(1,1), List.of(1,-1), List.of(-1,1), List.of(-1,-1));

        for (List<Integer> vector : vectors) {
            int movRow = vector.getFirst();
            int movCol = vector.get(1);
            int nextRow = currRow + movRow;
            int nextCol = currCol + movCol;
            while (0 < nextRow && nextRow <= 8 && 0 < nextCol && nextCol <= 8) {
                possibleMoves.add(new ChessMove(new ChessPosition(currRow, currCol), new ChessPosition(nextRow, nextCol), null));
                nextRow += movRow;
                nextCol += movCol;
            }
        }
        return possibleMoves;
        // return List.of(new ChessMove(new ChessPosition(5,4), new ChessPosition(1,8), null));
    }


}
