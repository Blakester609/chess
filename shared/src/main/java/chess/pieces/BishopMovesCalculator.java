package chess.pieces;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BishopMovesCalculator implements PieceMovesCalculator {
    ArrayList<ChessMove> validMoves = new ArrayList<>();
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        int rowPos = position.getRow();
        int colPos = position.getColumn();
        while ((rowPos < 8) &&  (colPos < 8)){
            ChessPosition newPos = new ChessPosition(rowPos += 1, colPos += 1);
            ChessMove newMove = new ChessMove(position, newPos, null);
            validMoves.add(newMove);
        }
        rowPos = position.getRow();
        colPos = position.getColumn();
        while ((rowPos > 1) &&  (colPos < 8)){
           System.out.println("Row: " + rowPos + " Column: " + colPos);
            ChessPosition newPos = new ChessPosition(rowPos -= 1, colPos += 1);
            ChessMove newMove = new ChessMove(position, newPos, null);
            validMoves.add(newMove);
        }
        rowPos = position.getRow();
        colPos = position.getColumn();
        while ((rowPos > 1) &&  (colPos > 1)){
//            System.out.println("Row: " + rowPos + " Column: " + colPos);
            ChessPosition newPos = new ChessPosition(rowPos -= 1, colPos -= 1);
            ChessMove newMove = new ChessMove(position, newPos, null);
            validMoves.add(newMove);
        }
        rowPos = position.getRow();
        colPos = position.getColumn();
        while ((rowPos < 8) &&  (colPos > 1)){
//            System.out.println("Row: " + rowPos + " Column: " + colPos);
            ChessPosition newPos = new ChessPosition(rowPos += 1, colPos -= 1);
            ChessMove newMove = new ChessMove(position, newPos, null);
            validMoves.add(newMove);
        }
//        ChessPosition newPos = new ChessPosition(position.getRow() + 1, position.getColumn() + 1);
//        ChessMove newMove = new ChessMove(position, newPos, null);
//        validMoves.add(newMove);
//        System.out.println(validMoves);
        return validMoves;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "BishopMovesCalculator{" +
                "validMoves=" + validMoves +
                '}';
    }
}
