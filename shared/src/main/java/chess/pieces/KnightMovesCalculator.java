package chess.pieces;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class KnightMovesCalculator implements PieceMovesCalculator {
    ArrayList<ChessMove> validMoves = new ArrayList<>();
    private final ChessGame.TeamColor pieceColor;
    private final int[][] possibleMoves = {{2,1}, {1,2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}, {1, -2}, {2, -1}};

    public KnightMovesCalculator(ChessGame.TeamColor pieceColor) {
        this.pieceColor = pieceColor;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        validMoves = ChessPiece.addMovesFromList(board, position, this.pieceColor, possibleMoves);
        return validMoves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KnightMovesCalculator that = (KnightMovesCalculator) o;
        return Objects.equals(validMoves, that.validMoves);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(validMoves);
    }
}
