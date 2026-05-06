package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Thay đổi thông tin này cho phù hợp với máy của bạn
    private static final String URL = "jdbc:mysql://localhost:3306/kho_hang";
    private static final String USER = "root";
    private static final String PASSWORD = "123456"; 

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}