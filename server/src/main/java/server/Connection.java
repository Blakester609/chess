package server;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
    public String username;
    public Session session;
    public boolean onFirstConnect;

    public Integer getGameID() {
        return gameID;
    }

    public Integer gameID;

    public Connection(String username, Session session, Integer gameID, boolean onFirstConnect) {
        this.username = username;
        this.session = session;
        this.gameID = gameID;
        this.onFirstConnect = onFirstConnect;
    }

    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }
}
