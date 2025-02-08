package chess;

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

        System.out.println(kingPos);
        System.out.println(getTeamTurn());
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        System.out.println(getBoard());
        for(ChessMove pieceMove : pieceToMove.pieceMoves(getBoard(), startPosition)) {
            if(isValidMove(pieceToMove, kingPos, startPosition, pieceMove, chessTeam)) {
                validMoves.add(pieceMove);
            }
        }
        return validMoves;
    }


    private boolean isValidMove(ChessPiece piece, ChessPosition kingPosition, ChessPosition startPosition, ChessMove possibleMove, TeamColor chessTeam) {
        ChessPosition tempPosition = null;
        ChessPiece tempPiece = null;

        if(getBoard().getPiece(possibleMove.getEndPosition()) != null ) {
            tempPosition = possibleMove.getEndPosition();
            tempPiece = getBoard().getPiece(possibleMove.getEndPosition());
        }
        getBoard().addPiece(possibleMove.getEndPosition(), piece);
        getBoard().removePiece(startPosition);
        boolean isCheck = isInCheck(chessTeam);
        System.out.println("Is check: " + isCheck);
        if(isCheck) {
            getBoard().addPiece(startPosition, piece);
            getBoard().removePiece(possibleMove.getEndPosition());
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
        boolean isInCheckmate = true;
        TeamColor opposingColor;
        if(teamColor == WHITE) {
            opposingColor = BLACK;
        } else {
            opposingColor = WHITE;
        }
        if(isInCheck(teamColor)) {
            ChessPosition kingPos = getKingPosition(teamColor);
            System.out.println(kingPos);
            ChessPiece king = getBoard().getPiece(kingPos);
            ArrayList<ChessMove> kingMoves = (ArrayList<ChessMove>) king.pieceMoves(getBoard(), kingPos);
            for(ChessMove move : kingMoves) {
                if(!isInCheck(move.getEndPosition(), opposingColor)) {
                    System.out.print("Not in check: ");
                    System.out.println(move);
                    isInCheckmate = false;
                }
            }
        }
        if(teamColor != getTeamTurn()) {
            isInCheckmate = false;
        }
        return isInCheckmate;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return false;
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
                ChessPosition pos = new ChessPosition(i, j);
                if(getBoard().getPiece(pos) != null) {
                    ChessPiece piece = getBoard().getPiece(pos);
                    if(piece.getPieceType() == KING && piece.getTeamColor() == color) {
                        return pos;
                    }
                }
            }
        }
        return new ChessPosition(1, 1);
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
                if(getBoard().getPiece(pos) != null) {
                    ChessPiece piece = getBoard().getPiece(pos);
                    if(piece.getTeamColor() == opposingColor) {
                        ArrayList<ChessMove> thisPieceMoves = (ArrayList<ChessMove>) piece.pieceMoves(getBoard(), pos);
                        if(piece.getPieceType() == PAWN) {
                            boolean couldCheck = isCouldCheck(kingPosition, opposingColor, pos);
                            System.out.print("Pawn could check: ");
                            System.out.println(couldCheck);
                            if(couldCheck) {
                                return couldCheck;
                            }
                        }
                        for(ChessMove move: thisPieceMoves) {
                            if(kingPosition.equals(move.getEndPosition())) {
                                System.out.println(piece);
                                System.out.println(move);
                                return true;
                            }
                        }

                    }

                }
            }
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
