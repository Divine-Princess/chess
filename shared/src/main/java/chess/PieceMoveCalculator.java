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
    private int movRow = 0;
    private int movCol = 0;
    private int nextRow;
    private int nextCol;

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
                getPawnMoves();
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

    private void addMove() {
        possibleMoves.add(new ChessMove(new ChessPosition(currRow, currCol), new ChessPosition(nextRow, nextCol), null));
    }

    private void getOtherPiece() {
        ChessPosition nextPosition = new ChessPosition(nextRow, nextCol);
        otherPiece = board.getPiece(nextPosition);
    }

    private void setNextPosition() {
        nextRow = currRow + movRow;
        nextCol = currCol + movCol;
    }

    private void getPawnMoves() {
        if (myColor == ChessGame.TeamColor.WHITE) {
            getWhitePawnMoves();
        }
        else if (myColor == ChessGame.TeamColor.BLACK) {
            getBlackPawnMoves();
        }
    }

    private void getWhitePawnMoves() {
        // currRow must be 2 in order to move 2, row++
        List<List<Integer>> moves;
        List<List<Integer>> attackMoves = List.of(List.of(1,-1), List.of(1,1));
        if (currRow == 2) {
            moves = List.of(List.of(1, 0), List.of(2, 0));
        } else {
            moves = List.of(List.of(1, 0));
        }
        getShortSlideMoves(moves);
        getShortSlideMoves(attackMoves);
    }

    private void getBlackPawnMoves() {
        // currRow must be 7 in order to move 2, col--
        List<List<Integer>> moves;
        List<List<Integer>> attackMoves = List.of(List.of(-1,1), List.of(-1,-1));
        if (currRow == 7) {
            moves = List.of(List.of(-1, 0), List.of(-2, 0));
        } else {
            moves = List.of(List.of(-1, 0));
        }
        getShortSlideMoves(moves);
        getShortSlideMoves(attackMoves);
    }

    private void getShortSlideMoves(List<List<Integer>> moves) {
        for (List<Integer> move : moves) {
            movRow = move.getFirst();
            movCol = move.get(1);
            setNextPosition();
            System.out.println(nextRow + " " + nextCol);

            // check if pawn can move forward
            if (0 < nextRow && nextRow <= 8 && 0 < nextCol && nextCol <= 8) {
                getOtherPiece();
                if (movCol == 0) {
                    if (otherPiece == null) {
                        if (nextRow == 1 || nextRow == 8) {
                            promotePawn();
                        }
                        else {
                            addMove();
                        }
                    } else {
                        break;
                    }
                } else {
                    if (otherPiece != null) {
                        otherPieceColor = otherPiece.getTeamColor();
                        if (!myColor.equals(otherPieceColor)) {
                            if (nextRow == 1 || nextRow == 8) {
                                promotePawn();
                            }
                            else {
                                addMove();
                            }
                        }
                    }
                }
            }
        }
    }

    private void promotePawn() {
        possibleMoves.add(new ChessMove(new ChessPosition(currRow, currCol), new ChessPosition(nextRow, nextCol), ChessPiece.PieceType.QUEEN));
        possibleMoves.add(new ChessMove(new ChessPosition(currRow, currCol), new ChessPosition(nextRow, nextCol), ChessPiece.PieceType.KNIGHT));
        possibleMoves.add(new ChessMove(new ChessPosition(currRow, currCol), new ChessPosition(nextRow, nextCol), ChessPiece.PieceType.BISHOP));
        possibleMoves.add(new ChessMove(new ChessPosition(currRow, currCol), new ChessPosition(nextRow, nextCol), ChessPiece.PieceType.ROOK));
    }

    private void getBishopMoves() {

        List<List<Integer>> vectors = List.of(List.of(1, 1), List.of(1, -1), List.of(-1, 1), List.of(-1, -1));

        getLongSlideMoves(vectors);
    }

    private void getKnightMoves() {

        List<List<Integer>> jumps = List.of(List.of(-1, 2), List.of(1, 2), List.of(-2, 1), List.of(2, 1), List.of(-2, -1), List.of(2, -1), List.of(-1, -2), List.of(1, -2));

        for (List<Integer> jump : jumps) {
            movRow = jump.getFirst();
            movCol = jump.get(1);
            setNextPosition();

            if (0 < nextRow && nextRow <= 8 && 0 < nextCol && nextCol <= 8) {
                getOtherPiece();
                if (otherPiece == null) {
                    addMove();
                } else {
                    otherPieceColor = otherPiece.getTeamColor();
                    if (!myColor.equals(otherPieceColor)) {
                        addMove();
                    }
                }
            }

        }
    }

    private void getRookMoves() {
        List<List<Integer>> vectors = List.of(List.of(0,1), List.of(1,0), List.of(0,-1), List.of(-1,0));

        getLongSlideMoves(vectors);
    }

    private void getQueenMoves() {
        List<List<Integer>> vectors = List.of(List.of(0,1), List.of(1,0), List.of(0,-1), List.of(-1,0),List.of(1, 1), List.of(1, -1), List.of(-1, 1), List.of(-1, -1));

        getLongSlideMoves(vectors);
    }

    private void getKingMoves() {

        List<List<Integer>> moves = List.of(List.of(-1, 1), List.of(0, 1), List.of(1, 1), List.of(-1, 0), List.of(1, 0), List.of(-1, -1), List.of(0, -1), List.of(1, -1));

        for (List<Integer> move : moves) {
            movRow = move.getFirst();
            movCol = move.get(1);
            setNextPosition();

            if (0 < nextRow && nextRow <= 8 && 0 < nextCol && nextCol <= 8) {
                getOtherPiece();
                if (otherPiece == null) {
                    addMove();
                } else {
                    otherPieceColor = otherPiece.getTeamColor();
                    if (!myColor.equals(otherPieceColor)) {
                        addMove();
                    }
                }
            } else {
                break;
            }

        }
        //getShortSlideMoves(moves);
    }

    private void getLongSlideMoves(List<List<Integer>> vectors) {
        for (List<Integer> vector : vectors) {
            movRow = vector.getFirst();
            movCol = vector.get(1);
            setNextPosition();

            while (0 < nextRow && nextRow <= 8 && 0 < nextCol && nextCol <= 8) {
                getOtherPiece();
                if (otherPiece == null) {
                    addMove();
                } else {
                    otherPieceColor = otherPiece.getTeamColor();
                    if (!myColor.equals(otherPieceColor)) {
                        addMove();
                    }
                    break;

                }
                nextRow += movRow;
                nextCol += movCol;
            }
        }
    }
}
