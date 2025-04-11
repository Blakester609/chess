package websocket;

//import org.glassfish.tyrus.core.wsadl.model.Endpoint;

import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;

public class WebSocketFacade extends Endpoint {

    Session session;
    ServerMessageObserver serverMessageObserver;

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}
}
