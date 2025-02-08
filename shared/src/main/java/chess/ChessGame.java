package chess;

import java.util.ArrayList;
import java.util.Collection;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessPiece.PieceType.KING;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn = TeamColor.WHITE;
    private ChessBoard gameBoard;
    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
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
        if (getBoard().getPiece(startPosition) == null) {
            return null;
        }
        return null;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        if(teamColor == TeamColor.WHITE) {
            return isWhiteInCheck();
        }
        if(teamColor == TeamColor.BLACK) {
            return isBlackInCheck();
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
     * no valid moves
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
        this.gameBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.gameBoard;
    }

    private ChessPosition getKingPosition(ChessGame.TeamColor color ) {
        for(int i = 1; i < 9; i++) {
            for(int j = 1; j < 9; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                if(getBoard().getPiece(pos) != null) {
                    ChessPiece piece = getBoard().getPiece(pos);
                    if(piece.getPieceType() == KING && piece.getTeamColor() == color) {
                        return pos;
                    }
                }
            }
        }
        return new ChessPosition(1, 1);
    }

    private boolean isWhiteInCheck() {
        ChessPosition whiteKingPos = getKingPosition(WHITE);
        return isInCheck(whiteKingPos, BLACK);
    }

    private boolean isBlackInCheck() {
        ChessPosition blackKingPos = getKingPosition(BLACK);
        return isInCheck(blackKingPos, WHITE);
    }

    private boolean isInCheck(ChessPosition kingPosition, TeamColor opposingColor) {
        for(int i = 1; i <= 8; i++) {
            for(int j = 1; j <= 8; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                if(getBoard().getPiece(pos) != null) {
                    ChessPiece piece = getBoard().getPiece(pos);
                    if(piece.getTeamColor() == opposingColor) {
                        ArrayList<ChessMove> thisPieceMoves = (ArrayList<ChessMove>) piece.pieceMoves(getBoard(), pos);
                        for(ChessMove move: thisPieceMoves) {
                            if(kingPosition.equals(move.getEndPosition())) {
                                return true;
                            }
                        }
                    }

                }
            }
        }
        return false;
    }

}
