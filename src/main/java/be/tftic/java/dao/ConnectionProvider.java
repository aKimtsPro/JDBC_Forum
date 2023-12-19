package be.tftic.java.dao;

import java.sql.*;
import java.util.Map;
import java.util.function.Function;

public class ConnectionProvider {

    private static final String URL = "jdbc:postgresql://localhost:5432/forum_db";
    private static final String USER = "user";
    private static final String PASSWORD = "pass";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

}
