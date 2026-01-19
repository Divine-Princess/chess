package chess;

import java.util.*;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    ChessPiece[][] squares = new ChessPiece[8][8];
    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        // Place piece on the board
        squares[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        // What piece is at position [row,col]?
        return squares[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        List<ChessGame.TeamColor> colors = List.of(ChessGame.TeamColor.BLACK, ChessGame.TeamColor.WHITE);
        List<Integer> rows = List.of(8,1);

        for (int i = 0; i < 2; i++) {

            ChessPiece rook = new ChessPiece(colors.get(i), ChessPiece.PieceType.ROOK);
            List<ChessPosition> rookPositions = List.of(new ChessPosition(rows.get(i), 1), new ChessPosition(rows.get(i), 8));
            resetMultiplePieces(rookPositions, rook);

            ChessPiece knight = new ChessPiece(colors.get(i), ChessPiece.PieceType.KNIGHT);
            List<ChessPosition> knightPositions = List.of(new ChessPosition(rows.get(i), 2), new ChessPosition(rows.get(i), 7));
            resetMultiplePieces(knightPositions, knight);

            ChessPiece bishopBlack = new ChessPiece(colors.get(i), ChessPiece.PieceType.BISHOP);
            List<ChessPosition> bishopBlackPositions = List.of(new ChessPosition(rows.get(i), 3), new ChessPosition(rows.get(i), 6));
            resetMultiplePieces(bishopBlackPositions, bishopBlack);

            addPiece(new ChessPosition(rows.get(i), 4), new ChessPiece(colors.get(i), ChessPiece.PieceType.QUEEN));
            addPiece(new ChessPosition(rows.get(i), 5), new ChessPiece(colors.get(i), ChessPiece.PieceType.KING));

        }

        ChessPiece pawnBlack = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        List<ChessPosition> pawnBlackPositions = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            pawnBlackPositions.add(new ChessPosition(7,i));
        }
        resetMultiplePieces(pawnBlackPositions,pawnBlack);

        ChessPiece pawnWhite = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        List<ChessPosition> pawnWhitePositions = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            pawnWhitePositions.add(new ChessPosition(2,i));
        }
        resetMultiplePieces(pawnWhitePositions,pawnWhite);

    }

    public void resetMultiplePieces(List<ChessPosition> positions, ChessPiece piece) {
        for (ChessPosition position : positions) {
            addPiece(position, piece);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }
}
