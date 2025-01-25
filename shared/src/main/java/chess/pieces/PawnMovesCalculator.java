package chess.pieces;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class PawnMovesCalculator implements PieceMovesCalculator {
    ArrayList<ChessMove> validMoves = new ArrayList<>();
    private final ChessGame.TeamColor pieceColor;

    public PawnMovesCalculator(ChessGame.TeamColor pieceColor) {
        this.pieceColor = pieceColor;
    }

    public ChessGame.TeamColor getPieceColor() {
        return this.pieceColor;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        int newCol = 0;
        int newRow = 0;
        if (getPieceColor() == ChessGame.TeamColor.WHITE) {
            newCol = position.getRow();
            newRow = position.getColumn() + 1;
        } else if (getPieceColor() == ChessGame.TeamColor.BLACK) {
            newCol = position.getRow();
            newRow = position.getColumn() - 1;
        }
        if (((newRow <= 8) && (newRow >= 1)) && ((newCol <= 8) && (newCol >= 1))) {
            ChessPosition newPos = new ChessPosition(newRow, newCol);
            ChessMove newMove = new ChessMove(position, newPos, null);
            validMoves.add(newMove);
        }

        return validMoves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PawnMovesCalculator that = (PawnMovesCalculator) o;
        return Objects.equals(validMoves, that.validMoves) && pieceColor == that.pieceColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(validMoves, pieceColor);
    }
}
