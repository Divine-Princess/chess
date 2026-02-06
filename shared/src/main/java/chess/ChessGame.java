package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    private ChessBoard tempBoard = new ChessBoard();
    private TeamColor currentTeam;

    public ChessGame() {
        this.board = new ChessBoard();
        board.resetBoard();
        this.currentTeam = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTeam;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTeam = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {

        ChessPiece piece = tempBoard.getPiece(startPosition);
        if (piece == null) {
            return Collections.emptyList();
        }

        TeamColor teamColor = piece.getTeamColor();
        Collection<ChessMove> allMoves = piece.pieceMoves(tempBoard, startPosition);
        Collection<ChessMove> possibleMoves = new ArrayList<>();

        for (ChessMove move : allMoves) {
            testPossibleMove(move);
            if (!isInCheck(teamColor)) {
                possibleMoves.add(move);
            }
            setBoard(board);
        }
        System.out.println("Possible moves: " + possibleMoves);
        return possibleMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        System.out.println(board);
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece piece = board.getPiece(startPosition);
        Collection<ChessMove> validMoves = validMoves(startPosition);

        if (validMoves == null) {
            return;
        }
        if (validMoves.contains(move)) {
            board.addPiece(startPosition, null);
            System.out.println(board);
            if (board.getPiece(endPosition) != null) {
                board.addPiece(endPosition, null);
                System.out.println(board);
            }
            board.addPiece(endPosition, piece);
            if (currentTeam == TeamColor.WHITE) {
                setTeamTurn(TeamColor.BLACK);
            } else {
                setTeamTurn(TeamColor.WHITE);
            }

        } else {
            throw new InvalidMoveException("Not a valid move");
        }

    }

    private void testPossibleMove(ChessMove move) {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece piece = tempBoard.getPiece(startPosition);
        tempBoard.addPiece(startPosition, null);
        tempBoard.addPiece(endPosition, piece);
    }

    private ChessPosition getKingPosition(TeamColor teamColor) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition currPosition = new ChessPosition(i, j);
                ChessPiece piece = tempBoard.getPiece(currPosition);
                if (piece == null) {
                    continue;
                }
                if (piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    return currPosition;
                }
            }
        }
        return null;
    }

    Collection<ChessMove> getAllOpposingMoves(TeamColor teamColor) {
        ArrayList<ChessMove> opposingMoves = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition currPosition = new ChessPosition(i, j);
                ChessPiece piece = tempBoard.getPiece(currPosition);
                if (piece == null || piece.getTeamColor() != teamColor) {
                    continue;
                }
                opposingMoves.addAll(piece.pieceMoves(tempBoard, currPosition));
            }
        }
        return opposingMoves;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        Collection<ChessMove> opposingMoves;
        if (teamColor == TeamColor.WHITE) {
             opposingMoves = getAllOpposingMoves(TeamColor.BLACK);
        } else if (teamColor == TeamColor.BLACK) {
            opposingMoves = getAllOpposingMoves(TeamColor.WHITE);
        } else {
            return false;
        }
        ChessPosition kingPosition = getKingPosition(teamColor);

        for (ChessMove move : opposingMoves) {
            if (move.getEndPosition().equals(kingPosition)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
        this.tempBoard = new ChessBoard();

        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if (piece != null) {
                    ChessPiece newPiece = new ChessPiece(piece.getTeamColor(), piece.getPieceType());
                    tempBoard.addPiece(position, newPiece);
                }
            }
        }


    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(getBoard(), chessGame.getBoard()) && Objects.equals(tempBoard, chessGame.tempBoard) && currentTeam == chessGame.currentTeam;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBoard(), tempBoard, currentTeam);
    }
}
