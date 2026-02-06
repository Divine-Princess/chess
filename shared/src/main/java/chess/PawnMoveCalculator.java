package chess;

import java.util.List;

public class PawnMoveCalculator {

    public void getPawnMoves(PieceMoveCalculator calc) {
        if (calc.myColor == ChessGame.TeamColor.WHITE) {
            getWhitePawnMoves(calc);
        }
        else if (calc.myColor == ChessGame.TeamColor.BLACK) {
            getBlackPawnMoves(calc);
        }
    }

    private void getWhitePawnMoves(PieceMoveCalculator calc) {
        List<List<Integer>> moves;
        List<List<Integer>> attackMoves = List.of(List.of(1,-1), List.of(1,1));
        if (calc.currRow == 2) {
            moves = List.of(List.of(1,0), List.of(2,0));
        } else {
            moves = List.of(List.of(1,0));
        }
        getAttackMoves(attackMoves, calc);
        getMoves(moves, calc);
    }

    private void getBlackPawnMoves(PieceMoveCalculator calc) {
        List<List<Integer>> moves;
        List<List<Integer>> attackMoves = List.of(List.of(-1,-1), List.of(-1,1));
        if (calc.currRow == 7) {
            moves = List.of(List.of(-1,0), List.of(-2,0));
        } else {
            moves = List.of(List.of(-1,0));
        }
        getAttackMoves(attackMoves, calc);
        getMoves(moves, calc);
    }

    private void getMoves(List<List<Integer>> moves, PieceMoveCalculator calc) {
        for (List<Integer> move : moves) {
            calc.movRow = move.getFirst();
            calc.movCol = move.get(1);
            calc.getNextPosition();

            if (!(0 < calc.nextRow && calc.nextRow <= 8 && 0 < calc.nextCol && calc.nextCol <= 8)) {
                break;
            }
            calc.getOtherPiece();
            if (calc.otherPiece != null) {
                break;
            }
            if (calc.nextRow == 8 || calc.nextRow == 1) {
                promotePawn(calc);
            } else {
                calc.addMove();
            }
        }
    }

    private void getAttackMoves(List<List<Integer>> moves, PieceMoveCalculator calc) {
        for (List<Integer> move : moves) {
            calc.movRow = move.getFirst();
            calc.movCol = move.get(1);
            calc.getNextPosition();

            if (!(0 < calc.nextRow && calc.nextRow <= 8 && 0 < calc.nextCol && calc.nextCol <= 8)) {
                break;
            }
            calc.getOtherPiece();
            if (calc.otherPiece == null || calc.myColor == calc.otherPiece.getTeamColor()) {
                continue;
            }
            if (calc.nextRow == 8 || calc.nextRow == 1) {
                promotePawn(calc);
            } else {
                calc.addMove();
            }
        }
    }

    private void promotePawn(PieceMoveCalculator calc) {
        ChessPiece.PieceType[] types = {ChessPiece.PieceType.QUEEN,ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.ROOK};
        for (ChessPiece.PieceType type : types) {
            calc.possibleMoves.add(new ChessMove(calc.myPosition, calc.nextPosition, type));
        }
    }

}
