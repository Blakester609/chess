package chess.pieces;

import chess.*;

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
        if (getPieceColor() == ChessGame.TeamColor.WHITE) {
            int newRow = position.getRow() + 1;
            if (newRow == 8) {
                ChessPosition newPos = new ChessPosition(newRow, position.getColumn());
                validMoves.add(new ChessMove(position, newPos, ChessPiece.PieceType.QUEEN));
                validMoves.add(new ChessMove(position, newPos, ChessPiece.PieceType.BISHOP));
                validMoves.add(new ChessMove(position, newPos, ChessPiece.PieceType.ROOK));
                validMoves.add(new ChessMove(position, newPos, ChessPiece.PieceType.KNIGHT));
            } else {
                ChessPosition newPos = new ChessPosition(newRow, position.getColumn());
                ChessMove newMove = new ChessMove(position, newPos, null);
                validMoves.add(newMove);
            }
            if (position.getRow() == 2) {
                newRow = position.getRow() + 2;
                ChessPosition newPos = new ChessPosition(newRow, position.getColumn());
                ChessMove newMove = new ChessMove(position, newPos, null);
                validMoves.add(newMove);
            }

        } else if (getPieceColor() == ChessGame.TeamColor.BLACK) {
            int newRow = position.getRow() - 1;
            if (newRow == 1) {
                ChessPosition newPos = new ChessPosition(newRow, position.getColumn());
                validMoves.add(new ChessMove(position, newPos, ChessPiece.PieceType.QUEEN));
                validMoves.add(new ChessMove(position, newPos, ChessPiece.PieceType.BISHOP));
                validMoves.add(new ChessMove(position, newPos, ChessPiece.PieceType.ROOK));
                validMoves.add(new ChessMove(position, newPos, ChessPiece.PieceType.KNIGHT));
            } else {
                ChessPosition newPos = new ChessPosition(newRow, position.getColumn());
                ChessMove newMove = new ChessMove(position, newPos, null);
                validMoves.add(newMove);
            }
            if (position.getRow() == 7) {
                newRow = position.getRow() - 2;
                ChessPosition newPos = new ChessPosition(newRow, position.getColumn());
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
        PawnMovesCalculator that = (PawnMovesCalculator) o;
        return Objects.equals(validMoves, that.validMoves) && pieceColor == that.pieceColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(validMoves, pieceColor);
    }
}
