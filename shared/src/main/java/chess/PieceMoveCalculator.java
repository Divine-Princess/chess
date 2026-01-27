package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PieceMoveCalculator {

    private List<ChessMove> possibleMoves = new ArrayList<>();
    private ChessBoard board;
    private ChessGame.TeamColor myColor;
    private ChessPosition myPosition;

    private int currRow;
    private int currCol;
    private int movRow;
    private int movCol;
    private int nextRow;
    private int nextCol;

    private ChessPiece otherPiece;
    private ChessGame.TeamColor otherPieceColor;
    private ChessPosition nextPosition;


    public Collection<ChessMove> getPieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        this.board = board;
        this.myPosition = myPosition;
        myColor = piece.getTeamColor();
        currRow = myPosition.getRow();
        currCol = myPosition.getColumn();

        switch (piece.getPieceType()) {
            case PAWN:
                getPawnMoves();
                break;
            case BISHOP:
                getBishopMoves();
                break;
            case ROOK:
                getRookMoves();
                break;
            case QUEEN:
                getRookMoves();
                getBishopMoves();
                break;
            case KING:
                getKingMoves();
                break;
            case KNIGHT:
                getKnightMoves();
                break;
        }
        return possibleMoves;
    }

    private void addMove() {
        possibleMoves.add(new ChessMove(myPosition, nextPosition, null));
    }

    private void getNextPosition() {
        nextRow = currRow + movRow;
        nextCol = currCol + movCol;
    }

    private void getOtherPiece() {
        nextPosition = new ChessPosition(nextRow,nextCol);
        otherPiece = board.getPiece(nextPosition);
    }

    private void getLongSlidingMoves(List<List<Integer>> vectors) {
        for (List<Integer> vector : vectors) {
            movRow = vector.getFirst();
            movCol = vector.get(1);
            getNextPosition();

            while (0 < nextRow && nextRow <= 8 && 0 < nextCol && nextCol <= 8) {
                getOtherPiece();

                if (otherPiece == null) {
                    addMove();
                } else {
                    otherPieceColor = otherPiece.getTeamColor();
                    if (!(myColor == otherPieceColor)) {
                        addMove();
                    }
                    break;

                }
                nextRow += movRow;
                nextCol += movCol;
            }

        }
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
        List<List<Integer>> moves;
        List<List<Integer>> attackMoves = List.of(List.of(1,-1), List.of(1,1));
        if (currRow == 2) {
            moves = List.of(List.of(1,0), List.of(2,0));
        } else {
            moves = List.of(List.of(1,0));
        }
        getPawnMovesCalc(attackMoves);
        getPawnMovesCalc(moves);
    }

    private void getBlackPawnMoves() {
        List<List<Integer>> moves;
        List<List<Integer>> attackMoves = List.of(List.of(-1,-1), List.of(-1,1));
        if (currRow == 7) {
            moves = List.of(List.of(-1,0), List.of(-2,0));
        } else {
            moves = List.of(List.of(-1,0));
        }
        getPawnMovesCalc(attackMoves);
        getPawnMovesCalc(moves);
    }

    private void getPawnMovesCalc(List<List<Integer>> moves) {
        for (List<Integer> move : moves) {
            movRow = move.getFirst();
            movCol = move.get(1);
            getNextPosition();

            if (0 < nextRow && nextRow <= 8 && 0 < nextCol && nextCol <= 8) {
                getOtherPiece();
                if (movCol == 0) {
                    if (otherPiece == null) {
                        if (nextRow == 8 || nextRow == 1) {
                            promotePawn();
                        } else {
                            addMove();
                        }
                    } else {
                        break;
                    }
                } else {
                    if (otherPiece != null) {
                        otherPieceColor = otherPiece.getTeamColor();
                        if (!(myColor == otherPieceColor)) {
                            if (nextRow == 8 || nextRow == 1) {
                                promotePawn();
                            } else {
                                addMove();
                            }
                        }
                    }
                }
            } else {
                break;
            }
        }
    }

    private void promotePawn() {
        possibleMoves.add(new ChessMove(myPosition, nextPosition, ChessPiece.PieceType.QUEEN));
        possibleMoves.add(new ChessMove(myPosition, nextPosition, ChessPiece.PieceType.KNIGHT));
        possibleMoves.add(new ChessMove(myPosition, nextPosition, ChessPiece.PieceType.BISHOP));
        possibleMoves.add(new ChessMove(myPosition, nextPosition, ChessPiece.PieceType.ROOK));
    }


    private void getBishopMoves() {
        List<List<Integer>> vectors = List.of(List.of(-1,-1), List.of(1,1), List.of(1,-1), List.of(-1,1));

        getLongSlidingMoves(vectors);
    }

    private void getRookMoves() {
        List<List<Integer>> vectors = List.of(List.of(0,-1), List.of(0,1), List.of(1,0), List.of(-1,0));

        getLongSlidingMoves(vectors);
    }

    private void getKingMoves() {
        List<List<Integer>> moves = List.of(List.of(0,-1), List.of(0,1), List.of(1,0), List.of(-1,0), List.of(-1,-1), List.of(1,1), List.of(1,-1), List.of(-1,1));

        for (List<Integer> move : moves) {
            movRow = move.getFirst();
            movCol = move.get(1);
            getNextPosition();

            if (0 < nextRow && nextRow <= 8 && 0 < nextCol && nextCol <= 8) {
                getOtherPiece();

                if (otherPiece == null) {
                    addMove();
                } else {
                    otherPieceColor = otherPiece.getTeamColor();
                    if (!(myColor == otherPieceColor)) {
                        addMove();
                    }
                }
            } else {
                break;
            }

        }
    }

    private void getKnightMoves() {
        List<List<Integer>> moves = List.of(List.of(2,-1), List.of(2,1), List.of(1,2), List.of(-1,2), List.of(-2,1), List.of(-2,-1), List.of(-1,-2), List.of(1,-2));

        for (List<Integer> move : moves) {
            movRow = move.getFirst();
            movCol = move.get(1);
            getNextPosition();

            if (0 < nextRow && nextRow <= 8 && 0 < nextCol && nextCol <= 8) {
                getOtherPiece();

                if (otherPiece == null) {
                    addMove();
                } else {
                    otherPieceColor = otherPiece.getTeamColor();
                    if (!(myColor == otherPieceColor)) {
                        addMove();
                    }
                }
            }
        }
    }

}
