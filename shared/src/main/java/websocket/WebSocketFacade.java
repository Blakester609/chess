package websocket;

//import org.glassfish.tyrus.core.wsadl.model.Endpoint;

import com.google.gson.Gson;
import exception.DataAccessException;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static websocket.commands.UserGameCommand.CommandType.CONNECT;
import static websocket.messages.ServerMessage.ServerMessageType.*;

public class WebSocketFacade extends Endpoint {

    Session session;
    ServerMessageObserver observer;

    public WebSocketFacade(String url, ServerMessageObserver serverMessageObserver) throws DataAccessException {
        try {
            url = url.replace("http", "ws");
            URI socketUri = new URI(url + "/ws");
            this.observer = serverMessageObserver;
            System.out.println("Creating WebSocketFacade");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketUri);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {

                @Override
                public void onMessage(String message) {
                    try {
                        ServerMessage someMessage = new Gson().fromJson(message, ServerMessage.class);
                        if(someMessage.getServerMessageType() == NOTIFICATION) {
                           NotificationMessage newMessage = new Gson().fromJson(message, NotificationMessage.class);
                           observer.notify(newMessage);
                        } else if(someMessage.getServerMessageType()  == LOAD_GAME) {
                            LoadGameMessage newMessage = new Gson().fromJson(message, LoadGameMessage.class);
                            observer.notify(newMessage);
                        } else if(someMessage.getServerMessageType() == ERROR) {
                            ErrorMessage newMessage = new Gson().fromJson(message, ErrorMessage.class);
                            observer.notify(newMessage);
                        }
                    } catch (Exception e) {
                        observer.notify(new ErrorMessage(ERROR, e.getMessage()));
                    }
                }
            });
        } catch(DeploymentException | IOException | URISyntaxException ex) {
            throw new DataAccessException(ex.getMessage(), 500);
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    // This is for debugging purposes only
    public void send(String msg) throws Exception {
        this.session.getBasicRemote().sendText(msg);
    }

    public void connectToGame(String authToken, Integer gameID) throws DataAccessException {
        try{
            System.out.println("Trying to connect to game: " + authToken);
            var command = new UserGameCommand(CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        }catch(IOException e) {
            throw new DataAccessException(e.getMessage(), 500);
        }
    }
}
