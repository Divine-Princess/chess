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
        List<ChessGame.TeamColor> color = List.of(ChessGame.TeamColor.WHITE, ChessGame.TeamColor.BLACK);
        List<Integer> row = List.of(1,8);

        for (int i = 0; i < 2; i++) {

            ChessPiece king = new ChessPiece(color.get(i), ChessPiece.PieceType.KING);
            ChessPosition kingPositions = new ChessPosition(row.get(i), 5);
            addPiece(kingPositions, king);

            ChessPiece queen = new ChessPiece(color.get(i), ChessPiece.PieceType.QUEEN);
            ChessPosition queenPositions = new ChessPosition(row.get(i), 4);
            addPiece(queenPositions, queen);

            ChessPiece knight = new ChessPiece(color.get(i), ChessPiece.PieceType.KNIGHT);
            List<ChessPosition> knightPositions = List.of(new ChessPosition(row.get(i), 2),new ChessPosition(row.get(i), 7));
            resetMultiplePieces(knight, knightPositions);

            ChessPiece bishop = new ChessPiece(color.get(i), ChessPiece.PieceType.BISHOP);
            List<ChessPosition> bishopPositions = List.of(new ChessPosition(row.get(i), 3),new ChessPosition(row.get(i), 6));
            resetMultiplePieces(bishop, bishopPositions);

            ChessPiece rook = new ChessPiece(color.get(i), ChessPiece.PieceType.ROOK);
            List<ChessPosition> rookPositions = List.of(new ChessPosition(row.get(i), 1),new ChessPosition(row.get(i), 8));
            resetMultiplePieces(rook, rookPositions);
        }

        ChessPiece blackPawn = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);

        for (int i = 1; i < 9; i++) {
            addPiece(new ChessPosition(7, i), blackPawn);
        }

        ChessPiece whitePawn = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);

        for (int i = 1; i < 9; i++) {
            addPiece(new ChessPosition(2, i), whitePawn);
        }

    }

    public void resetMultiplePieces(ChessPiece piece, List<ChessPosition> positions) {
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

    @Override
    public String toString() {
        return "ChessBoard{" +
                "squares=" + Arrays.toString(squares) +
                '}';
    }
}




