package ui;

import websocket.ServerMessageObserver;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Scanner;

import static ui.EscapeSequences.*;
import static websocket.messages.ServerMessage.ServerMessageType.LOAD_GAME;
import static websocket.messages.ServerMessage.ServerMessageType.NOTIFICATION;

public class Repl implements ServerMessageObserver {
    private final ChessClient client;

    public Repl(String serverUrl) {
        client = new ChessClient(serverUrl, this);
    }

    public void run() {
        System.out.println(SET_TEXT_COLOR_BLUE + "Welcome to CS 240 Chess. Type Help to get started");
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while(!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = client.eval(line);
                System.out.println(SET_TEXT_COLOR_YELLOW + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.println(msg);
            }

        }
    }

    private void printPrompt() {
        System.out.print("\n" + SET_TEXT_COLOR_WHITE + ">>> ");
    }

    @Override
    public void notify(ServerMessage message) {
        if(message.getServerMessageType() == NOTIFICATION) {
            NotificationMessage someMessage = (NotificationMessage) message;
            System.out.println(SET_TEXT_COLOR_YELLOW + someMessage.getMessage());
        }
        if(message.getServerMessageType() == LOAD_GAME) {
            LoadGameMessage someMessage = (LoadGameMessage) message;
            System.out.println();
            System.out.println(client.drawBoard(someMessage.getGameData().getGame().getBoard(), someMessage.getPlayerColor()));
        }

        printPrompt();
    }
}
