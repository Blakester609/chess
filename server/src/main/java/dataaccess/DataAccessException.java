package dataaccess;

import com.google.gson.Gson;

import java.util.Map;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends Exception{
    final private int statusCode;
    public DataAccessException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public String toJson() {
        return new Gson().toJson(Map.of("message", getMessage()));
    }

    public int statusCode() {
        return statusCode;
    }
}
