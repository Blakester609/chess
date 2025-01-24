package chess.pieces;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class KingMovesCalculator implements PieceMovesCalculator {
    ArrayList<ChessMove> validMoves = new ArrayList<>();
    private final int[][] possibleMoves = {{1,0}, {1,1}, {0,1}, {-1, 1}, {-1,0}, {-1,-1}, {0, -1}, {1, -1} };
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        for (int[] possibleMove : possibleMoves) {
            int newRow = position.getRow() + possibleMove[0];
            int newCol = position.getColumn() + possibleMove[1];
            if (((newRow < 8) && (newRow >= 0)) && ((newCol < 8) && (newCol >= 0))) {
                ChessPosition newPos = new ChessPosition(newRow, newCol);
                ChessMove newMove = new ChessMove(position, newPos, null);
                validMoves.add(newMove);
            }

        }
        return validMoves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KingMovesCalculator that = (KingMovesCalculator) o;
        return Objects.equals(validMoves, that.validMoves);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(validMoves);
    }
}
