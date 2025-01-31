package chess;

import chess.pieces.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
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
                pmc = new KingMovesCalculator(getTeamColor());
                break;
            case QUEEN:
                pmc = new QueenMovesCalculator(getTeamColor());
                break;
            case BISHOP:
                pmc = new BishopMovesCalculator(getTeamColor());
                break;
            case KNIGHT:
                pmc = new KnightMovesCalculator(getTeamColor());
                break;
            case ROOK:
                pmc = new RookMovesCalculator(getTeamColor());
                break;
            case PAWN:
                pmc = new PawnMovesCalculator(getTeamColor());
                break;
        }
        return pmc.pieceMoves(board, myPosition);
    }

    public static boolean isStuck(ChessBoard board, int newRow, int newCol, ChessGame.TeamColor pieceColor) {
        if ((newCol <= 8 && newCol >= 1) && (newRow <= 8 && newRow >= 1) && board.getPiece(new ChessPosition(newRow, newCol)) != null) {
            return board.getPiece(new ChessPosition(newRow, newCol)).getTeamColor() == pieceColor;
        }
        return false;
    }

    public static boolean validateCanCapture(ChessBoard board, int newRow, int newCol, ChessGame.TeamColor pieceColor) {
        if ((newCol <= 8 && newCol >= 1) && board.getPiece(new ChessPosition(newRow, newCol)) != null) {
            return board.getPiece(new ChessPosition(newRow, newCol)).getTeamColor() != pieceColor;
        }
        return false;
    }

    public static ArrayList<ChessMove> addMovesFromList(ChessBoard board, ChessPosition position, ChessGame.TeamColor pieceColor, int[][] possibleMoves) {
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        for (int[] possibleMove : possibleMoves) {
            int newRow = position.getRow() + possibleMove[0];
            int newCol = position.getColumn() + possibleMove[1];
            if(isStuckEdge(board, newRow, newCol, pieceColor)) {
                continue;
            }
            if (((newRow <= 8) && (newRow >= 1)) && ((newCol <= 8) && (newCol >= 1))) {
                ChessPosition newPos = new ChessPosition(newRow, newCol);
                ChessMove newMove = new ChessMove(position, newPos, null);
                validMoves.add(newMove);
            }

        }
        return validMoves;
    }

    public static boolean isStuckEdge(ChessBoard board, int newRow, int newCol, ChessGame.TeamColor pieceColor) {
        if ((newCol <= 8 && newCol >= 1) && (newRow <= 8 && newRow >= 1) && board.getPiece(new ChessPosition(newRow, newCol)) != null) {
            return board.getPiece(new ChessPosition(newRow, newCol)).getTeamColor() == pieceColor;
        }
        return false;
    }
}
