package chess.pieces;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static chess.ChessPiece.isStuck;
import static chess.ChessPiece.validateCanCapture;

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
        validMoves = getHorizontalMoves(rowPos, colPos, board, position, this.pieceColor);
        return validMoves;
    }

    public static ArrayList<ChessMove> getHorizontalMoves(int rowPos, int colPos, ChessBoard board,
                                                          ChessPosition position, ChessGame.TeamColor pieceColor) {
        ArrayList<ChessMove> horizontalMoves = new ArrayList<>();
        while(colPos < 8) {
            ChessPosition newPos = new ChessPosition(rowPos, colPos += 1);
            if(isStuck(board, rowPos, colPos, pieceColor)) {
                break;
            }
            ChessMove newMove = new ChessMove(position, newPos, null);
            horizontalMoves.add(newMove);
            if(validateCanCapture(board, rowPos, colPos, pieceColor)) {
                break;
            }
        }
        rowPos = position.getRow();
        colPos = position.getColumn();
        while(colPos > 1) {
            ChessPosition newPos = new ChessPosition(rowPos, colPos -= 1);
            if(isStuck(board, rowPos, colPos, pieceColor)) {
                break;
            }
            ChessMove newMove = new ChessMove(position, newPos, null);
            horizontalMoves.add(newMove);
            if(validateCanCapture(board, rowPos, colPos, pieceColor)) {
                break;
            }
        }
        rowPos = position.getRow();
        colPos = position.getColumn();
        while(rowPos < 8) {
            ChessPosition newPos = new ChessPosition(rowPos +=1, colPos);
            if(isStuck(board, rowPos, colPos, pieceColor)) {
                break;
            }
            ChessMove newMove = new ChessMove(position, newPos, null);
            horizontalMoves.add(newMove);
            if(validateCanCapture(board, rowPos, colPos, pieceColor)) {
                break;
            }
        }
        rowPos = position.getRow();
        colPos = position.getColumn();
        while(rowPos > 1) {
            ChessPosition newPos = new ChessPosition(rowPos -=1, colPos);
            if(isStuck(board, rowPos, colPos, pieceColor)) {
                break;
            }
            ChessMove newMove = new ChessMove(position, newPos, null);
            horizontalMoves.add(newMove);
            if(validateCanCapture(board, rowPos, colPos, pieceColor)) {
                break;
            }
        }
        return horizontalMoves;
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
