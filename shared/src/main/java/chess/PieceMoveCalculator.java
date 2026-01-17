package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PieceMoveCalculator {

    private List<ChessMove> possibleMoves;
    private ChessGame.TeamColor myColor;
    private ChessBoard board;

    private int currRow;
    private int currCol;
    private int movRow;
    private int movCol;
    private int nextRow;
    private int nextCol;

    private ChessPosition nextPosition;
    private ChessPiece otherPiece;
    private ChessGame.TeamColor otherPieceColor;

    public Collection<ChessMove> getPieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        possibleMoves = new ArrayList<>();
        myColor = piece.getTeamColor();
        currRow = myPosition.getRow();
        currCol = myPosition.getColumn();
        this.board = board;

        switch (piece.getPieceType()) {
            case PAWN:
                break;
            case BISHOP:
                getBishopMoves();
                break;
            case KNIGHT:
                getKnightMoves();
                break;
            case ROOK:
                getRookMoves();
                break;
            case QUEEN:
                getQueenMoves();
                break;
            case KING:
                getKingMoves();
                break;
        }

        return possibleMoves;
    }

    private void addMove(int nextRow, int nextCol) {
        possibleMoves.add(new ChessMove(new ChessPosition(currRow, currCol), new ChessPosition(nextRow, nextCol), null));
    }


    private void getPawnMoves() {
        if (myColor == ChessGame.TeamColor.WHITE) {
            // currRow must be 2 in order to move 2, col++


        }
        else if (myColor == ChessGame.TeamColor.BLACK) {
            // currRow must be 7 in order to move 2, col--
        }
    }

    private void getBishopMoves() {

        List<List<Integer>> vectors = List.of(List.of(1, 1), List.of(1, -1), List.of(-1, 1), List.of(-1, -1));

        getLongSlideMove(vectors);
    }

    private void getKnightMoves() {

        List<List<Integer>> jumps = List.of(List.of(-1, 2), List.of(1, 2), List.of(-2, 1), List.of(2, 1), List.of(-2, -1), List.of(2, -1), List.of(-1, -2), List.of(1, -2));

        for (List<Integer> jump : jumps) {
            movRow = jump.getFirst();
            movCol = jump.get(1);
            nextRow = currRow + movRow;
            nextCol = currCol + movCol;

            if (0 < nextRow && nextRow <= 8 && 0 < nextCol && nextCol <= 8) {
                nextPosition = new ChessPosition(nextRow, nextCol);
                otherPiece = board.getPiece(nextPosition);
                if (otherPiece == null) {
                    addMove(nextRow, nextCol);
                } else {
                    otherPieceColor = otherPiece.getTeamColor();
                    if (!myColor.equals(otherPieceColor)) {
                        addMove(nextRow, nextCol);
                    }
                }
            }

        }
    }

    private void getRookMoves() {
        List<List<Integer>> vectors = List.of(List.of(0,1), List.of(1,0), List.of(0,-1), List.of(-1,0));

        getLongSlideMove(vectors);
    }

    private void getQueenMoves() {
        List<List<Integer>> vectors = List.of(List.of(0,1), List.of(1,0), List.of(0,-1), List.of(-1,0),List.of(1, 1), List.of(1, -1), List.of(-1, 1), List.of(-1, -1));

        getLongSlideMove(vectors);
    }

    private void getKingMoves() {

        List<List<Integer>> moves = List.of(List.of(-1, 1), List.of(0, 1), List.of(1, 1), List.of(-1, 0), List.of(1, 0), List.of(-1, -1), List.of(0, -1), List.of(1, -1));

        for (List<Integer> move : moves) {
            movRow = move.getFirst();
            movCol = move.get(1);
            nextRow = currRow + movRow;
            nextCol = currCol + movCol;

            if (0 < nextRow && nextRow <= 8 && 0 < nextCol && nextCol <= 8) {
                nextPosition = new ChessPosition(nextRow, nextCol);
                otherPiece = board.getPiece(nextPosition);
                if (otherPiece == null) {
                    addMove(nextRow, nextCol);
                } else {
                    otherPieceColor = otherPiece.getTeamColor();
                    if (!myColor.equals(otherPieceColor)) {
                        addMove(nextRow, nextCol);
                    }
                }
            } else {
                break;
            }

        }
    }

    private void getLongSlideMove(List<List<Integer>> vectors) {
        for (List<Integer> vector : vectors) {
            movRow = vector.getFirst();
            movCol = vector.get(1);
            nextRow = currRow + movRow;
            nextCol = currCol + movCol;

            while (0 < nextRow && nextRow <= 8 && 0 < nextCol && nextCol <= 8) {
                nextPosition = new ChessPosition(nextRow, nextCol);
                otherPiece = board.getPiece(nextPosition);

                if (otherPiece == null) {
                    addMove(nextRow, nextCol);
                } else {
                    otherPieceColor = otherPiece.getTeamColor();
                    if (!myColor.equals(otherPieceColor)) {
                        addMove(nextRow, nextCol);
                    }
                    break;

                }
                nextRow += movRow;
                nextCol += movCol;
            }
        }
    }
}
