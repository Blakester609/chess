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
            if (newRow == 8 && validateForwardMove(board, position, newRow)) {
                addPromotionMoves(position, newRow, position.getColumn());
                whiteCaptureAndPromote(board, position, newRow);
            } else {
                if ((position.getRow() == 2)
                        && validateForwardMove(board, position, position.getRow() + 2)
                        && validateForwardMove(board, position, position.getRow() + 1)
                ) {
                    newRow = position.getRow() + 2;
                    ChessPosition newPos = new ChessPosition(newRow, position.getColumn());
                    ChessMove newMove = new ChessMove(position, newPos, null);
                    validMoves.add(newMove);
                }

                if ((newRow <= 8)) {
                    newRow = position.getRow() + 1;
                    if (validateForwardMove(board, position, newRow)) {
                        ChessPosition newPos = new ChessPosition(newRow, position.getColumn());
                        ChessMove newMove = new ChessMove(position, newPos, null);
                        validMoves.add(newMove);
                    }
                    whiteCapture(board, position, newRow);

                }

            }


        } else if (getPieceColor() == ChessGame.TeamColor.BLACK) {
            int newRow = position.getRow() - 1;
            if (newRow == 1 && validateForwardMove(board, position, newRow)) {
                addPromotionMoves(position, newRow, position.getColumn());
                blackCaptureAndPromote(board, position, newRow);
            } else {
                if (position.getRow() == 7
                        && validateForwardMove(board, position, position.getRow() - 2)
                        && validateForwardMove(board, position, position.getRow() - 1)
                ) {
                    newRow = position.getRow() - 2;
                    ChessPosition newPos = new ChessPosition(newRow, position.getColumn());
                    ChessMove newMove = new ChessMove(position, newPos, null);
                    validMoves.add(newMove);
                }
                if ((newRow >= 1)) {
                    newRow = position.getRow() - 1;
                    if(validateForwardMove(board, position, newRow)) {
                        ChessPosition newPos = new ChessPosition(newRow, position.getColumn());
                        ChessMove newMove = new ChessMove(position, newPos, null);
                        validMoves.add(newMove);
                    }
                    blackCapture(board, position, newRow);
                }
            }
        }
        return validMoves;
    }

    private void whiteCaptureAndPromote(ChessBoard board, ChessPosition position, int newRow) {
        if (validateCanCapture(board, ChessGame.TeamColor.BLACK, newRow, position.getColumn() + 1)) {
            addPromotionMoves(position, newRow, position.getColumn() + 1);
        }
        if (validateCanCapture(board, ChessGame.TeamColor.BLACK, newRow, position.getColumn() - 1)) {
            addPromotionMoves(position, newRow, position.getColumn() - 1);
        }
    }

    private void blackCaptureAndPromote(ChessBoard board, ChessPosition position, int newRow) {
        if (validateCanCapture(board, ChessGame.TeamColor.WHITE, newRow, position.getColumn() + 1)) {
            addPromotionMoves(position, newRow, position.getColumn() + 1);
        }
        if (validateCanCapture(board, ChessGame.TeamColor.WHITE, newRow, position.getColumn() - 1)) {
            addPromotionMoves(position, newRow, position.getColumn() - 1);
        }
    }

    private void whiteCapture(ChessBoard board, ChessPosition position, int newRow) {
        if (validateCanCapture(board, ChessGame.TeamColor.BLACK, newRow, position.getColumn() + 1)) {
            ChessPosition newPos = new ChessPosition(newRow, position.getColumn() + 1);
            ChessMove newMove = new ChessMove(position, newPos, null);
            validMoves.add(newMove);
        }
        if (validateCanCapture(board, ChessGame.TeamColor.BLACK, newRow, position.getColumn() - 1)) {
            ChessPosition newPos = new ChessPosition(newRow, position.getColumn() - 1);
            ChessMove newMove = new ChessMove(position, newPos, null);
            validMoves.add(newMove);
        }
    }

    private void blackCapture(ChessBoard board, ChessPosition position, int newRow) {
        if (validateCanCapture(board, ChessGame.TeamColor.WHITE, newRow, position.getColumn() + 1)) {
            ChessPosition newPos = new ChessPosition(newRow, position.getColumn() + 1);
            ChessMove newMove = new ChessMove(position, newPos, null);
            validMoves.add(newMove);
        }
        if (validateCanCapture(board, ChessGame.TeamColor.WHITE, newRow, position.getColumn() - 1)) {
            ChessPosition newPos = new ChessPosition(newRow, position.getColumn() - 1);
            ChessMove newMove = new ChessMove(position, newPos, null);
            validMoves.add(newMove);
        }
    }

    private void addPromotionMoves(ChessPosition position, int newRow, int newCol) {
        ChessPosition newPos = new ChessPosition(newRow, newCol);
        validMoves.add(new ChessMove(position, newPos, ChessPiece.PieceType.QUEEN));
        validMoves.add(new ChessMove(position, newPos, ChessPiece.PieceType.BISHOP));
        validMoves.add(new ChessMove(position, newPos, ChessPiece.PieceType.ROOK));
        validMoves.add(new ChessMove(position, newPos, ChessPiece.PieceType.KNIGHT));
    }

    private boolean validateForwardMove(ChessBoard board, ChessPosition position, int newRow) {
        return (board.getPiece(new ChessPosition(newRow, position.getColumn())) == null);
    }

    private boolean validateCanCapture(ChessBoard board, ChessGame.TeamColor pieceColor, int newRow, int newCol) {
        if ((newCol <= 8 && newCol >= 1) && board.getPiece(new ChessPosition(newRow, newCol)) != null) {
            return board.getPiece(new ChessPosition(newRow, newCol)).getTeamColor() == pieceColor;
        }
        return false;
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
