import chess.*;
import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import server.Server;
import service.UserService;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);
        try {

            UserDAO userDao = new MemoryUserDAO();
            AuthDAO authDao = new MemoryAuthDAO();
            UserService userService = new UserService(userDao, authDao);
            Server chessServer = new Server();
            chessServer.setService(userService);
            chessServer.run(8080);
//            chessServer.stop();
        } catch (Throwable ex) {
            System.out.printf("Unable to start server %s", ex.getMessage());
        }

    }
}