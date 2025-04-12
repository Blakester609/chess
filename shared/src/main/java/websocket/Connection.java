package websocket;

import chess.ChessGame;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
    public String username;
    public Session session;
    public ChessGame gameState;

    public Connection(String username, Session session, ChessGame gameState) {
        this.username = username;
        this.session = session;
        this.gameState = gameState;
    }

    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }
}
