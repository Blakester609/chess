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

import static chess.ChessPiece.isStuck;
import static chess.ChessPiece.validateCanCapture;

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
        validMoves = getDiagonalMoves(rowPos, colPos, board, position, this.pieceColor);
        return validMoves;
    }

    public static ArrayList<ChessMove> getDiagonalMoves(int rowPos, int colPos, ChessBoard board,
                                                        ChessPosition position, ChessGame.TeamColor pieceColor) {
        ArrayList<ChessMove> diagonalMoves = new ArrayList<>();
        while ((rowPos < 8) &&  (colPos < 8)){
            ChessPosition newPos = new ChessPosition(rowPos += 1, colPos += 1);
            if(isStuck(board, rowPos, colPos, pieceColor)) {
                break;
            }
            ChessMove newMove = new ChessMove(position, newPos, null);
            diagonalMoves.add(newMove);
            if(validateCanCapture(board, rowPos, colPos, pieceColor)) {
                break;
            }
        }
        rowPos = position.getRow();
        colPos = position.getColumn();
        while ((rowPos > 1) &&  (colPos < 8)){
            ChessPosition newPos = new ChessPosition(rowPos -= 1, colPos += 1);
            if(isStuck(board, rowPos, colPos, pieceColor)) {
                break;
            }
            ChessMove newMove = new ChessMove(position, newPos, null);
            diagonalMoves.add(newMove);
            if(validateCanCapture(board, rowPos, colPos, pieceColor)) {
                break;
            }
        }
        rowPos = position.getRow();
        colPos = position.getColumn();
        while ((rowPos > 1) &&  (colPos > 1)){
            ChessPosition newPos = new ChessPosition(rowPos -= 1, colPos -= 1);
            if(isStuck(board, rowPos, colPos, pieceColor)) {
                break;
            }
            ChessMove newMove = new ChessMove(position, newPos, null);
            diagonalMoves.add(newMove);
            if(validateCanCapture(board, rowPos, colPos, pieceColor)) {
                break;
            }
        }
        rowPos = position.getRow();
        colPos = position.getColumn();
        while ((rowPos < 8) &&  (colPos > 1)){
            ChessPosition newPos = new ChessPosition(rowPos += 1, colPos -= 1);
            if(isStuck(board, rowPos, colPos, pieceColor)) {
                break;
            }
            ChessMove newMove = new ChessMove(position, newPos, null);
            diagonalMoves.add(newMove);
            if(validateCanCapture(board, rowPos, colPos, pieceColor)) {
                break;
            }
        }

        return diagonalMoves;
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
