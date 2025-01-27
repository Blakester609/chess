package chess.pieces;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static chess.ChessPiece.isStuck;

public class QueenMovesCalculator implements PieceMovesCalculator  {

    ArrayList<ChessMove> validMoves = new ArrayList<>();
    private final ChessGame.TeamColor pieceColor;
    public QueenMovesCalculator(ChessGame.TeamColor pieceColor) {
         this.pieceColor = pieceColor;
    }
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        int rowPos = position.getRow();
        int colPos = position.getColumn();
        validMoves = BishopMovesCalculator.getDiagonalMoves(rowPos, colPos, board, position, this.pieceColor);
        validMoves.addAll(RookMovesCalculator.getHorizontalMoves(rowPos, colPos, board, position, this.pieceColor));
        return validMoves;
    }
}
