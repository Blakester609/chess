package websocket.commands;

import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand {


    private ChessMove move;
    private String moveString;

    public String getMoveString() {
        return moveString;
    }

    public ChessMove getMove() {
        return move;
    }

    public MakeMoveCommand(CommandType commandType, String authToken, Integer gameID, ChessMove move, String moveString) {
        super(commandType, authToken, gameID);
        this.move = move;
        this.moveString = moveString;
    }
}
