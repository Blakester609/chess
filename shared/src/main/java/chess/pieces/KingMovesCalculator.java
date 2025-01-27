package chess.pieces;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.ChessPiece;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class KingMovesCalculator implements PieceMovesCalculator {
    ArrayList<ChessMove> validMoves = new ArrayList<>();
    private final ChessGame.TeamColor pieceColor;
    private final int[][] possibleMoves = {{1,0}, {1,1}, {0,1}, {-1, 1}, {-1,0}, {-1,-1}, {0, -1}, {1, -1} };

    public KingMovesCalculator(ChessGame.TeamColor pieceColor) {
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
        KingMovesCalculator that = (KingMovesCalculator) o;
        return Objects.equals(validMoves, that.validMoves);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(validMoves);
    }
}
