package com.gmail.dystopianrescuer.ninjavrfx.utility.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // Datos de acceso por defecto
    private static final String URL = "jdbc:mysql://sql5.freemysqlhosting.net/sql5471995";
    private static final String USER = "sql5471995";
    private static final String PASSWORD = "nqMb1L8Hu3";
    private static Connection conexion;
    private static DBConnection instancia;

    private DBConnection() {
    }

    public static DBConnection getInstance() {
        if (instancia == null) {
            instancia = new DBConnection();
        }
        return instancia;
    }

    public Connection connect() throws SQLException {

        conexion = DriverManager.getConnection(URL, USER, PASSWORD);

        return conexion;
    }

    public void closeConnection() throws SQLException {

        conexion.close();

    }
}
