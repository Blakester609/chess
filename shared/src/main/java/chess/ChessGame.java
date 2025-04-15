package chess;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collection;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessPiece.PieceType.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn = TeamColor.WHITE;
    private ChessBoard gameBoard;
    public ChessGame() {
        ChessBoard newBoard = new ChessBoard();
        newBoard.resetBoard();
        setBoard(newBoard);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece pieceToMove = getBoard().getPiece(startPosition);
        TeamColor chessTeam;
        if (pieceToMove == null) {
            return null;
        }
        ChessPosition kingPos = null;
        if(pieceToMove.getTeamColor() == WHITE) {
            kingPos = getKingPosition(WHITE);
            chessTeam = WHITE;
        } else {
            kingPos = getKingPosition(BLACK);
            chessTeam = BLACK;
        }

        ArrayList<ChessMove> validMoves = new ArrayList<>();
        for(ChessMove pieceMove : pieceToMove.pieceMoves(getBoard(), startPosition)) {
            if(isValidMove(pieceToMove, kingPos, startPosition, pieceMove, chessTeam)) {
                validMoves.add(pieceMove);
            }
        }
        return validMoves;
    }


    private boolean isValidMove(ChessPiece piece, ChessPosition kingPosition, ChessPosition
            startPosition, ChessMove possibleMove, TeamColor chessTeam) {
        ChessPosition tempPosition = null;
        ChessPiece tempPiece = null;

        if(getBoard().getPiece(possibleMove.getEndPosition()) != null ) {
            tempPosition = possibleMove.getEndPosition();
            tempPiece = getBoard().getPiece(possibleMove.getEndPosition());
        }
        getBoard().addPiece(possibleMove.getEndPosition(), piece);
        getBoard().removePiece(startPosition);
        boolean isCheck = isInCheck(chessTeam);
        if(isCheck) {
            getBoard().addPiece(startPosition, piece);
            getBoard().removePiece(possibleMove.getEndPosition());
            if(tempPosition != null && tempPiece != null) {
                getBoard().addPiece(tempPosition, tempPiece);
            }
            return false;
        }
        getBoard().addPiece(startPosition, piece);
        getBoard().removePiece(possibleMove.getEndPosition());
        if(tempPosition != null && tempPiece != null) {
            getBoard().addPiece(tempPosition, tempPiece);
        }

        return true;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = getBoard().getPiece(move.getStartPosition());
        if((piece != null) && validMoves(move.getStartPosition()).contains(move) && (getTeamTurn() == piece.getTeamColor())) {
            if(move.getPromotionPiece() != null) {
                ChessPiece newPiece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
                getBoard().addPiece(move.getEndPosition(), newPiece);
                getBoard().removePiece(move.getStartPosition());
            } else {
                getBoard().addPiece(move.getEndPosition(), piece);
                getBoard().removePiece(move.getStartPosition());
            }
        } else {
            throw new InvalidMoveException();
        }
        if(getTeamTurn() == WHITE) {
            setTeamTurn(BLACK);
        } else if (getTeamTurn() == BLACK) {
            setTeamTurn(WHITE);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        if(teamColor == TeamColor.WHITE) {
            ChessPosition whiteKingPos = getKingPosition(WHITE);
            return isWhiteInCheck(whiteKingPos);
        }
        if(teamColor == TeamColor.BLACK) {
            ChessPosition blackKingPos = getKingPosition(BLACK);
            return isBlackInCheck(blackKingPos);
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if(isInCheck(teamColor)) {
            ChessPosition kingPos = getKingPosition(teamColor);
            ArrayList<ChessMove> kingMoves = (ArrayList<ChessMove>) validMoves(kingPos);
            if(kingMoves.isEmpty()) {
                return !checkmateLogic(teamColor);
            }
        }
        return false;
    }

    private boolean checkmateLogic(TeamColor teamColor) {
        for(int i = 1; i <= 8; i++ ) {
            for(int j = 1; j <= 8; j++) {
                if (checkPieceCanBlockCheck(teamColor, i, j)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkPieceCanBlockCheck(TeamColor teamColor, int i, int j) {
        ChessPosition pos = new ChessPosition(i, j);
        ChessPiece newPiece = getBoard().getPiece(pos);
        if(newPiece != null) {
            return checkPieceCanBlockCheckLogic(teamColor, newPiece, pos);
        }
        return false;
    }

    private boolean checkPieceCanBlockCheckLogic(TeamColor teamColor, ChessPiece newPiece, ChessPosition pos) {
        if(newPiece.getTeamColor() == teamColor) {
            return pieceCanBlockCheckLogicInner(teamColor, newPiece, pos);
        }
        return false;
    }

    private boolean pieceCanBlockCheckLogicInner(TeamColor teamColor, ChessPiece newPiece, ChessPosition pos) {
        ArrayList<ChessMove> thisPieceMoves = (ArrayList<ChessMove>) validMoves(pos);
        for(ChessMove move: thisPieceMoves) {
            if (checkEachPieceCanBlock(teamColor, newPiece, move)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkEachPieceCanBlock(TeamColor teamColor, ChessPiece newPiece, ChessMove move) {
        ChessPosition tempPosition = null;
        ChessPiece tempPiece = null;
        if(getBoard().getPiece(move.getEndPosition()) != null ) {
            tempPosition = move.getEndPosition();
            tempPiece = getBoard().getPiece(move.getEndPosition());
        }
        getBoard().addPiece(move.getEndPosition(), newPiece);
        getBoard().removePiece(move.getStartPosition());
        if(!isInCheck(teamColor)) {
            getBoard().addPiece(move.getStartPosition(), newPiece);
            getBoard().removePiece(move.getEndPosition());
            if(tempPosition != null && tempPiece != null) {
                getBoard().addPiece(tempPosition, tempPiece);
            }
            return true;
        }
        getBoard().addPiece(move.getStartPosition(), newPiece);
        getBoard().removePiece(move.getEndPosition());
        if(tempPosition != null && tempPiece != null) {
            getBoard().addPiece(tempPosition, tempPiece);
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        ChessPosition kingPos = getKingPosition(teamColor);
        ArrayList<ChessMove> kingMoves = (ArrayList<ChessMove>) validMoves(kingPos);
        ArrayList<Boolean> canMovePieces = new ArrayList<>();
        if(isInCheckmate(teamColor)) {
            return false;
        }
        if(!isInCheck(teamColor)) {
            if(kingMoves.isEmpty()) {
                checkCanMoveAllPieces(teamColor, canMovePieces);
            }
        }
        if(teamColor != getTeamTurn()) {
            return false;
        }
        return canMovePieces.isEmpty() || !canMovePieces.contains(false);
    }

    private void checkCanMoveAllPieces(TeamColor teamColor, ArrayList<Boolean> canMovePieces) {
        for(int i = 1; i <= 8; i++) {
            for(int j = 1; j <= 8; j++) {
                checkCanMovePiece(teamColor, i, j, canMovePieces);
            }
        }
    }

    private void checkCanMovePiece(TeamColor teamColor, int i, int j, ArrayList<Boolean> canMovePieces) {
        ChessPosition pos = new ChessPosition(i, j);
        ChessPiece newPiece = getBoard().getPiece(pos);
        if(newPiece != null) {
            if(newPiece.getTeamColor() == teamColor) {
                addCanMovePiece(pos, canMovePieces);
            }
        }
    }

    private void addCanMovePiece(ChessPosition pos, ArrayList<Boolean> canMovePieces) {
        ArrayList<ChessMove> thisPieceMoves = (ArrayList<ChessMove>) validMoves(pos);
        if(thisPieceMoves.isEmpty()) {
            canMovePieces.add(true);
        } else {
            canMovePieces.add(false);
        }
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.gameBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.gameBoard;
    }

    private ChessPosition getKingPosition(ChessGame.TeamColor color ) {
        for(int i = 1; i < 9; i++) {
            for(int j = 1; j < 9; j++) {
                ChessPosition pos = returnKingPosition(color, i, j);
                if (pos != null) {
                    return pos;
                }
            }
        }
        return new ChessPosition(1, 1);
    }

    private ChessPosition returnKingPosition(TeamColor color, int i, int j) {
        ChessPosition pos = new ChessPosition(i, j);
        if(getBoard().getPiece(pos) != null) {
            ChessPiece piece = getBoard().getPiece(pos);
            if(piece.getPieceType() == KING && piece.getTeamColor() == color) {
                return pos;
            }
        }
        return null;
    }

    private boolean isWhiteInCheck(ChessPosition kingPosition) {
        return isInCheck(kingPosition, BLACK);
    }

    private boolean isBlackInCheck(ChessPosition kingPosition) {
        return isInCheck(kingPosition, WHITE);
    }

    private boolean isInCheck(ChessPosition kingPosition, TeamColor opposingColor) {
        for(int i = 1; i <= 8; i++) {
            for(int j = 1; j <= 8; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                if (checkAllPiecesCheck(kingPosition, opposingColor, pos)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkAllPiecesCheck(ChessPosition kingPosition, TeamColor opposingColor, ChessPosition pos) {
        if(getBoard().getPiece(pos) != null) {
            ChessPiece piece = getBoard().getPiece(pos);
            if(piece.getTeamColor() == opposingColor) {
                ArrayList<ChessMove> thisPieceMoves = (ArrayList<ChessMove>) piece.pieceMoves(getBoard(), pos);
                if (checkIfPawnCanCheck(kingPosition, opposingColor, piece, pos)) {
                    return true;
                }
                return checkPieceCanTakeKing(kingPosition, thisPieceMoves);

            }

        }
        return false;
    }

    private boolean checkPieceCanTakeKing(ChessPosition kingPosition, ArrayList<ChessMove> thisPieceMoves) {
        for(ChessMove move: thisPieceMoves) {
            if(kingPosition.equals(move.getEndPosition())) {
                return true;
            }
        }
        return false;
    }

    private boolean checkIfPawnCanCheck(ChessPosition kingPosition, TeamColor opposingColor, ChessPiece piece, ChessPosition pos) {
        if(piece.getPieceType() == PAWN) {
            boolean couldCheck = isCouldCheck(kingPosition, opposingColor, pos);
            return couldCheck;
        }
        return false;
    }

    private static boolean isCouldCheck(ChessPosition kingPosition, TeamColor opposingColor, ChessPosition pos) {
        boolean couldCheck = false;
        if(opposingColor == BLACK) {
            couldCheck = kingPosition.equals(new ChessPosition(pos.getRow() - 1, pos.getColumn() + 1)) ||
                    kingPosition.equals(new ChessPosition(pos.getRow() - 1, pos.getColumn() - 1));
        } else if(opposingColor == WHITE) {
            couldCheck = kingPosition.equals(new ChessPosition(pos.getRow() + 1, pos.getColumn() + 1)) ||
                    kingPosition.equals(new ChessPosition(pos.getRow() + 1, pos.getColumn() - 1));
        }
        return couldCheck;
    }

}
