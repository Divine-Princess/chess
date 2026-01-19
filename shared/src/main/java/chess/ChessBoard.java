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
        new ChessBoard();

        ChessPiece rookBlack = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        List<ChessPosition> rookBlackPositions = List.of(new ChessPosition(8,1), new ChessPosition(8,8));
        resetMultiplePieces(rookBlackPositions, rookBlack);

        ChessPiece knightBlack = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        List<ChessPosition> knightBlackPositions = List.of(new ChessPosition(8,2), new ChessPosition(8,7));
        resetMultiplePieces(knightBlackPositions, knightBlack);

        ChessPiece bishopBlack = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        List<ChessPosition> bishopBlackPositions = List.of(new ChessPosition(8,3), new ChessPosition(8,6));
        resetMultiplePieces(bishopBlackPositions, bishopBlack);

        addPiece(new ChessPosition(8,4), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(8,5), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));

        ChessPiece pawnBlack = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        List<ChessPosition> pawnBlackPositions = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            pawnBlackPositions.add(new ChessPosition(7,i));
        }
        resetMultiplePieces(pawnBlackPositions,pawnBlack);

        ChessPiece rookWhite = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        List<ChessPosition> rookWhitePositions = List.of(new ChessPosition(1,1), new ChessPosition(1,8));
        resetMultiplePieces(rookWhitePositions, rookWhite);

        ChessPiece knightWhite = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        List<ChessPosition> knightWhitePositions = List.of(new ChessPosition(1,2), new ChessPosition(1,7));
        resetMultiplePieces(knightWhitePositions, knightWhite);

        ChessPiece bishopWhite = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        List<ChessPosition> bishopWhitePositions = List.of(new ChessPosition(1,3), new ChessPosition(1,6));
        resetMultiplePieces(bishopWhitePositions, bishopWhite);

        addPiece(new ChessPosition(1,4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(1,5), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));

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
