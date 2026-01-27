package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PieceMoveCalculator {

    protected final List<ChessMove> possibleMoves = new ArrayList<>();
    protected ChessBoard board;
    protected ChessGame.TeamColor myColor;
    protected ChessPosition myPosition;

    protected int currRow;
    protected int currCol;
    protected int movRow;
    protected int movCol;
    protected int nextRow;
    protected int nextCol;

    protected ChessPiece otherPiece;
    protected ChessPosition nextPosition;


    public Collection<ChessMove> getPieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        this.board = board;
        this.myPosition = myPosition;
        myColor = piece.getTeamColor();
        currRow = myPosition.getRow();
        currCol = myPosition.getColumn();

        switch (piece.getPieceType()) {
            case PAWN:
                PawnMoveCalculator pawnMoveCalculator = new PawnMoveCalculator();
                pawnMoveCalculator.getPawnMoves(this);
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

    protected void addMove() {
        possibleMoves.add(new ChessMove(myPosition, nextPosition, null));
    }

    protected void getNextPosition() {
        nextRow = currRow + movRow;
        nextCol = currCol + movCol;
    }

    protected void getOtherPiece() {
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
                    if (!(myColor == otherPiece.getTeamColor())) {
                        addMove();
                    }
                    break;
                }
                nextRow += movRow;
                nextCol += movCol;
            }
        }
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

            if (!(0 < nextRow && nextRow <= 8 && 0 < nextCol && nextCol <= 8)) {
                break;
            }
            getOtherPiece();

            if (otherPiece == null) {
                addMove();
            } else if (!(myColor == otherPiece.getTeamColor())) {
                addMove();
            }
        }
    }

    private void getKnightMoves() {
        List<List<Integer>> moves = List.of(List.of(2,-1), List.of(2,1), List.of(1,2), List.of(-1,2), List.of(-2,1), List.of(-2,-1), List.of(-1,-2), List.of(1,-2));

        for (List<Integer> move : moves) {
            movRow = move.getFirst();
            movCol = move.get(1);
            getNextPosition();

            if (!(0 < nextRow && nextRow <= 8 && 0 < nextCol && nextCol <= 8)) {
                continue;
            }
            getOtherPiece();
            if (otherPiece == null) {
                addMove();
            } else if (!(myColor == otherPiece.getTeamColor())) {
                addMove();
            }
        }
    }

}
