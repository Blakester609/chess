package chess;

import chess.pieces.*;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        PieceMovesCalculator pmc = null;
        switch (getPieceType()) {
            case KING:
                pmc = new KingMovesCalculator();
                break;
            case QUEEN:
                pmc = new QueenMovesCalculator();
                break;
            case BISHOP:
                pmc = new BishopMovesCalculator();
                break;
            case KNIGHT:
                pmc = new KnightMovesCalculator();
                break;
            case ROOK:
                pmc = new RookMovesCalculator();
                break;
            case PAWN:
                pmc = new PawnMovesCalculator(getTeamColor());
                break;
        }
        return pmc.pieceMoves(board, myPosition);
    }
}
