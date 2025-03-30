package ui;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import dataaccess.DataAccessException;
import model.UserData;

public class ServerFacade {
    private final String serverUrl;


    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public AuthData login(UserData userData) throws DataAccessException {
        var path = "/session";
        return this.makeRequest("POST", path, userData, AuthData.class);
    }

    public AuthData register(UserData userData) throws DataAccessException {
        var path = "/user";
        return this.makeRequest("POST", path, userData, AuthData.class);
    }

    public void logout(String authToken) throws DataAccessException {
        var path = "/session";
        this.makeRequest("DELETE", path, authToken, null);
    }

    public GameData createGame(GameData gameData, String authToken) {
        var path = "/game";
        return this.makeRequest("POST", path, );
    }

    public void joinGame() {

    }

    public ArrayList<GameData> listGames() {
        return null;
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            if(request.getClass() == String.class) {
                http.addRequestProperty("authorization", (String) request);
            } else {
                String reqData = new Gson().toJson(request);
                System.out.println(reqData);
                try (OutputStream reqBody = http.getOutputStream()) {
                    reqBody.write(reqData.getBytes());
                }
            }
        }
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws DataAccessException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (DataAccessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage(), 500);
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, DataAccessException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw DataAccessException.fromJson(respErr);
                }
            }

            throw new DataAccessException("other failure: " + status, status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
