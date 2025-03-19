package dataaccess;

import chess.ChessGame;

import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlDataAccess {

    public MySqlDataAccess() throws DataAccessException {
        configureDatabase();
    }

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()), 500);
        }
    }

    protected int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param instanceof ChessGame p) ps.setString(i + 1, p.toString());
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()), 500);
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  User (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`username`)
            )""",

            """
            CREATE TABLE IF NOT EXISTS Game (
                `id` int NOT NULL AUTO_INCREMENT,
                `whiteUsername` varchar(256) DEFAULT NULL,
                `blackUsername` varchar(256) DEFAULT NULL,
                `gameName` varchar(256) NOT NULL,
                `json` TEXT DEFAULT NULL,
                 PRIMARY KEY (`id`)
            )""",

            """
            CREATE TABLE IF NOT EXISTS Auth (
                `authToken` varchar(256) NOT NULL,
                `username` varchar(256) NOT NULL,
                PRIMARY KEY (`username`)
            )
            """
    };
}
