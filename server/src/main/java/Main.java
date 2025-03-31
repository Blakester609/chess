import chess.*;
import dataaccess.*;
import server.Server;
import service.UserService;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);
        try {
            var port = 8080;
            if (args.length >= 1) {
                port = Integer.parseInt(args[0]);
            }
            Server chessServer = new Server();
            chessServer.run(port);
//            chessServer.stop();
        } catch (Throwable ex) {
            System.out.printf("Unable to start server %s", ex.getMessage());
        }

    }
}