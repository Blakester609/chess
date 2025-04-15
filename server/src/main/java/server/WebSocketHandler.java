package server;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import model.GameData;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import service.UserService;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import exception.DataAccessException;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

import static websocket.commands.UserGameCommand.CommandType.MAKE_MOVE;
import static websocket.messages.ServerMessage.ServerMessageType.ERROR;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String msg) throws Exception {
        try {
            UserGameCommand command = new Gson().fromJson(msg, UserGameCommand.class);
            System.out.println("Message received");
            String username = getUsername(command.getAuthToken());

            saveSession(username, command.getGameID(), session);

            switch(command.getCommandType()) {
                case CONNECT -> connect(session, username, command);
                case MAKE_MOVE -> {
                    MakeMoveCommand moveCommand = new Gson().fromJson(msg, MakeMoveCommand.class);
                    makeMove(session, username, moveCommand, moveCommand.getMoveString());
                }
                case LEAVE -> leaveGame(session, username, command);
                case RESIGN -> resign(session, username, command);
            }
        } catch (DataAccessException e) {
            sendMessage(session.getRemote(), new ErrorMessage(ERROR, "Error: unauthorized"));
        } catch (Exception e) {
            e.printStackTrace();
            sendMessage(session.getRemote(), new ErrorMessage(ERROR, "Error: " + e.getMessage()));
        }
    }

    private void sendMessage(RemoteEndpoint remote, ErrorMessage errorMessage) throws IOException {
        remote.sendString(errorMessage.toString());
    }

    private void resign(Session session, String username, UserGameCommand command) throws DataAccessException, IOException {
        GameData gameData = userService.retrieveGameDataWithIsOver(command.getGameID());
        if(gameData.getIsGameOver()) {
            throw new DataAccessException("Error: cannot resign from game that is already over", 403);
        }
        gameData = userService.updateGameIsOver(command.getGameID(), true);
        String playerColor = "";
        if(gameData.getWhiteUsername().equals(username)) {
            playerColor = "white";
        } else if(gameData.getBlackUsername().equals(username)) {
            playerColor = "black";
        } else {
            throw new DataAccessException("Error: cannot resign as observer", 403);
        }
        var message = String.format("%s has resigned the game. Game over!", playerColor);
        var serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcastLoadGameAll(serverMessage, command.getGameID());
        
    }
    
    private void connect(Session session, String username, UserGameCommand command) throws DataAccessException, IOException {
        GameData gameData = userService.retrieveGameData(command.getGameID());
        String playerColor = "white";
        if(gameData.getBlackUsername().equals(username)) {
            playerColor = "black";
        }
        var message = String.format("%s has joined game %s as %s", username, gameData.gameName(), playerColor);

        var loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData, playerColor);
        connections.broadcast(loadGameMessage, command.getGameID());
        var serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcastNotification(username, serverMessage, command.getGameID());
    }

    private void leaveGame(Session session, String username, UserGameCommand command) throws IOException, DataAccessException {
        GameData gameData = userService.retrieveGameData(command.getGameID());
        userService.leaveGame(command.getGameID(), username);
        var message = String.format("%s has left the game", username);
        var serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcastNotification(username, serverMessage, command.getGameID());
        connections.remove(username);
    }

    private void makeMove(Session session, String username, MakeMoveCommand command, String moveString) throws DataAccessException, IOException {
        GameData gameData = userService.retrieveGameDataWithIsOver(command.getGameID());
        if(gameData.getIsGameOver()) {
            throw new DataAccessException("Error: cannot resign from game that is already over", 403);
        }
        boolean isObserver = gameData.getWhiteUsername().equals(username) || gameData.getBlackUsername().equals(username);
        if(gameData.getWhiteUsername().equals(username)) {
            if(gameData.getGame().getTeamTurn() == ChessGame.TeamColor.BLACK) {
                throw new DataAccessException("Error: Cannot make move for other player", 403);
            }
        }
        if(gameData.getBlackUsername().equals(username)) {
            if(gameData.getGame().getTeamTurn() == ChessGame.TeamColor.WHITE) {
                throw new DataAccessException("Error: Cannot make move for other player", 403);
            }
        }
        if(!isObserver) {
            throw new DataAccessException("Error: cannot make move as observer", 403);
        }
        String message = "";
        gameData = userService.updateGameState(command.getGameID(), command.getMove());
        gameData = userService.retrieveGameDataWithIsOver(command.getGameID());
        if(gameData.getIsGameOver()) {
            if(gameData.getGame().isInCheckmate(ChessGame.TeamColor.WHITE)) {
                message = "White is in checkmate! Game over!";
            } else if(gameData.getGame().isInCheckmate(ChessGame.TeamColor.BLACK)) {
                message = "Black is checkmate! Game Over!";
            } else if(gameData.getGame().isInStalemate(ChessGame.TeamColor.WHITE)
                      || gameData.getGame().isInStalemate(ChessGame.TeamColor.BLACK)) {
                message = "Stalemate! Game is over!";
            }
        } else {
            if(gameData.getGame().isInCheck(ChessGame.TeamColor.WHITE)) {
                message = "White is in check!";
            } else if(gameData.getGame().isInCheck(ChessGame.TeamColor.BLACK)) {
                message = "Black is in check!";
            } else {
                message = String.format("%s made the move %s", username, moveString);
            }
        }

        var loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData, "");
        connections.broadcastLoadGameAll(loadGameMessage, command.getGameID());
        var serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcastNotification(username, serverMessage, command.getGameID());
    }

    private void saveSession(String username, Integer gameID, Session session) throws Exception {
        System.out.println("Trying to save session");
        try {
            connections.add(username, session, gameID, false);
        } catch (Exception e) {
            throw e;
        }

    }

    private String getUsername(String authToken) throws DataAccessException {
        return userService.retrieveAuthData(authToken).username();
    }
}
