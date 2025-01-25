package chess.pieces;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class BishopMovesCalculator implements PieceMovesCalculator {
    ArrayList<ChessMove> validMoves = new ArrayList<>();
    private final ChessGame.TeamColor pieceColor;
    public BishopMovesCalculator(ChessGame.TeamColor pieceColor) {
        this.pieceColor = pieceColor;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        int rowPos = position.getRow();
        int colPos = position.getColumn();
        while ((rowPos < 8) &&  (colPos < 8)){
            ChessPosition newPos = new ChessPosition(rowPos += 1, colPos += 1);
            System.out.println(newPos);
            if(isStuck(board, rowPos, colPos)) {
                break;
            }
            ChessMove newMove = new ChessMove(position, newPos, null);
            validMoves.add(newMove);
            System.out.println(validMoves);
            if(validateCanCapture(board, rowPos, colPos)) {
                break;
            }
        }
        rowPos = position.getRow();
        colPos = position.getColumn();
        while ((rowPos > 1) &&  (colPos < 8)){
           System.out.println("Row: " + rowPos + " Column: " + colPos);
            ChessPosition newPos = new ChessPosition(rowPos -= 1, colPos += 1);
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
        while ((rowPos > 1) &&  (colPos > 1)){
//            System.out.println("Row: " + rowPos + " Column: " + colPos);
            ChessPosition newPos = new ChessPosition(rowPos -= 1, colPos -= 1);
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
        while ((rowPos < 8) &&  (colPos > 1)){
//            System.out.println("Row: " + rowPos + " Column: " + colPos);
            ChessPosition newPos = new ChessPosition(rowPos += 1, colPos -= 1);
            if(isStuck(board, rowPos, colPos)) {
                break;
            }
            ChessMove newMove = new ChessMove(position, newPos, null);
            validMoves.add(newMove);
            if(validateCanCapture(board, rowPos, colPos)) {
                break;
            }
        }
//        ChessPosition newPos = new ChessPosition(position.getRow() + 1, position.getColumn() + 1);
//        ChessMove newMove = new ChessMove(position, newPos, null);
//        validMoves.add(newMove);
//        System.out.println(validMoves);
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
    public String toString() {
        return "BishopMovesCalculator{" +
                "validMoves=" + validMoves +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BishopMovesCalculator that = (BishopMovesCalculator) o;
        return Objects.equals(validMoves, that.validMoves) && pieceColor == that.pieceColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(validMoves, pieceColor);
    }
}
