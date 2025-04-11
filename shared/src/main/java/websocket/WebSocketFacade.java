package websocket;

//import org.glassfish.tyrus.core.wsadl.model.Endpoint;

import com.google.gson.Gson;
import exception.DataAccessException;
import websocket.messages.ServerMessage;

import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    Session session;
    ServerMessageObserver serverMessageObserver;

    public WebSocketFacade(String url, ServerMessageObserver serverMessageObserver) throws DataAccessException {
        try {
            url = url.replace("http", "ws");
            URI socketUri = new URI(url + "/ws");
            this.serverMessageObserver = serverMessageObserver;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketUri);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {

                @Override
                public void onMessage(String message) {
                    try {
                        ServerMessage someMessage = new Gson().fromJson(message, ServerMessage.class);
                        observer.notify(message);
                    } catch (Exception e) {
                        observer.notify(new DataAccessException(e.getMessage(), 500));
                    }

                }
            });
        } catch(DeploymentException | IOException | URISyntaxException ex) {

        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}
}
