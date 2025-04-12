package websocket;

//import org.glassfish.tyrus.core.wsadl.model.Endpoint;

import com.google.gson.Gson;
import exception.DataAccessException;
import websocket.messages.ErrorMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static websocket.messages.ServerMessage.ServerMessageType.ERROR;

public class WebSocketFacade extends Endpoint {

    Session session;
    ServerMessageObserver observer;

    public WebSocketFacade(String url, ServerMessageObserver serverMessageObserver) throws DataAccessException {
        try {
            url = url.replace("http", "ws");
            URI socketUri = new URI(url + "/ws");
            this.observer = serverMessageObserver;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketUri);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {

                @Override
                public void onMessage(String message) {
                    try {
                        ServerMessage someMessage = new Gson().fromJson(message, NotificationMessage.class);
                        observer.notify(someMessage);
                    } catch (Exception e) {
                        observer.notify(new ErrorMessage(ERROR, e.getMessage()));
                    }

                }
            });
        } catch(DeploymentException | IOException | URISyntaxException ex) {

        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}
}
