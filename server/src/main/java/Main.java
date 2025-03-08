import chess.*;
import dataaccess.*;
import server.Server;
import service.UserService;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);
        try {

            Server chessServer = new Server();
            chessServer.run(8080);
//            chessServer.stop();
        } catch (Throwable ex) {
            System.out.printf("Unable to start server %s", ex.getMessage());
        }

    }
}