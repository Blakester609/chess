package chess.pieces;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class QueenMovesCalculator extends BishopMovesCalculator implements PieceMovesCalculator  {

    ArrayList<ChessMove> validMoves = new ArrayList<>();
    public QueenMovesCalculator(ChessGame.TeamColor pieceColor) {
        super(pieceColor);
    }
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        validMoves = (ArrayList<ChessMove>) super.pieceMoves(board, position);
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
}
