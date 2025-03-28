package ui;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;

public class ServerFacade {
    private final String serverUrl;


    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public AuthData login() {
        return null;
    }

    public AuthData register() {
        return null;
    }

    public void logout() {

    }

    public GameData createGame() {
        return null;
    }

    public void joinGame() {

    }

    public ArrayList<GameData> listGames() {
        return null;
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }


}
