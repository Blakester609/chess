package chess.pieces;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class RookMovesCalculator implements PieceMovesCalculator {
    private final ChessGame.TeamColor pieceColor;
    ArrayList<ChessMove> validMoves = new ArrayList<>();
    public RookMovesCalculator(ChessGame.TeamColor pieceColor) {
        this.pieceColor = pieceColor;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        int rowPos = position.getRow();
        int colPos = position.getColumn();
        while(colPos < 8) {
            ChessPosition newPos = new ChessPosition(rowPos, colPos += 1);
            if(isStuck(board, rowPos, colPos)) {
                break;
            }
            ChessMove newMove = new ChessMove(position, newPos, null);
            validMoves.add(newMove);
            if(validateCanCapture(board, rowPos, colPos)) {
                break;
            }
        }
        rowPos = position.getRow();
        colPos = position.getColumn();
        while(colPos > 1) {
            ChessPosition newPos = new ChessPosition(rowPos, colPos -= 1);
            if(isStuck(board, rowPos, colPos)) {
                break;
            }
            ChessMove newMove = new ChessMove(position, newPos, null);
            validMoves.add(newMove);
            if(validateCanCapture(board, rowPos, colPos)) {
                break;
            }
        }
        rowPos = position.getRow();
        colPos = position.getColumn();
        while(rowPos < 8) {
            ChessPosition newPos = new ChessPosition(rowPos +=1, colPos);
            if(isStuck(board, rowPos, colPos)) {
                break;
            }
            ChessMove newMove = new ChessMove(position, newPos, null);
            validMoves.add(newMove);
            if(validateCanCapture(board, rowPos, colPos)) {
                break;
            }
        }
        rowPos = position.getRow();
        colPos = position.getColumn();
        while(rowPos > 1) {
            ChessPosition newPos = new ChessPosition(rowPos -=1, colPos);
            if(isStuck(board, rowPos, colPos)) {
                break;
            }
            ChessMove newMove = new ChessMove(position, newPos, null);
            validMoves.add(newMove);
            if(validateCanCapture(board, rowPos, colPos)) {
                break;
            }
        }
        return validMoves;
    }

    public boolean isStuck(ChessBoard board, int newRow, int newCol) {
        if ((newCol <= 8 && newCol >= 1) && board.getPiece(new ChessPosition(newRow, newCol)) != null) {
            return board.getPiece(new ChessPosition(newRow, newCol)).getTeamColor() == this.pieceColor;
        }
        return false;
    }

    public boolean validateCanCapture(ChessBoard board, int newRow, int newCol) {
        if ((newCol <= 8 && newCol >= 1) && board.getPiece(new ChessPosition(newRow, newCol)) != null) {
            return board.getPiece(new ChessPosition(newRow, newCol)).getTeamColor() != this.pieceColor;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RookMovesCalculator that = (RookMovesCalculator) o;
        return pieceColor == that.pieceColor && Objects.equals(validMoves, that.validMoves);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, validMoves);
    }
}
