package com.gmail.dystopianrescuer.ninjavrfx.utility.db;

import com.gmail.dystopianrescuer.ninjavrfx.models.Usuario;
import com.gmail.dystopianrescuer.ninjavrfx.utility.GestorTransacciones;
import javafx.application.Platform;

import javax.swing.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.*;
import java.util.ArrayList;

public class ConsultasDB {

    public static void main(String[] args) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api-gw.payclip.com/paymentrequest"))
                .header("accept", "application/vnd.com.payclip.v2+json")
                .header("x-api-key", "Basic ODBhMTkxMDQtNzMxNC00NmI5LWFjMTEtMGY0MmVhODNkYzY1OmQyNzhjNjBiLTQ2MjMtNDM1Yi04ZjIzLWYxNjQ3YzI5NjkyNA==")
                .header("content-type", "application/json; charset=UTF-8")
                .method("POST", HttpRequest.BodyPublishers.ofString("{\"amount\":1,\"assigned_user\":\"diegobravis20@gmail.com\",\"reference\":\"1234567\",\"message\":\"pago\"}"))
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.headers() + "\n");
        System.out.println(response.body());

    }

    public synchronized static ArrayList<Usuario> getUsuarios() {

        ArrayList<Usuario> usuarios = new ArrayList<>();

        try {

            Connection connection = DBConnection.getInstance().connect();
            PreparedStatement seleccionar = connection.prepareStatement("SELECT * FROM usuarios");
            ResultSet consulta = seleccionar.executeQuery();

            while (consulta.next()) {
                usuarios.add(new Usuario(consulta.getString(2), consulta.getString(3), consulta.getString(4), consulta.getString(5)));
            }


        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "No se ha podido conectar con la base de datos, consulte con administrador");
            Platform.exit();
        }

        return usuarios;
    }

    public static Usuario userExist(String user, String password) {

        Usuario usuario = null;

        try {
            Connection connection = DBConnection.getInstance().connect();

            PreparedStatement seleccionar = connection.prepareStatement("SELECT * FROM usuarios WHERE BINARY usuario=? AND contraseña=?");
            seleccionar.setString(1, user);
            seleccionar.setString(2, password);

            ResultSet consulta = seleccionar.executeQuery();

            if (consulta.next()) {
                usuario = new Usuario(consulta.getString(2), consulta.getString(3), consulta.getString(4), consulta.getString(5));
            }


        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "No se ha podido conectar con la base de datos, consulte con administrador");
            Platform.exit();
        }

        return usuario;
    }

    public static void insertTransaction(Timestamp fecha, String tipo, String especificacion, GestorTransacciones.TipoPago tipoPago, int pago, String referenciaBancaria, Usuario empleado) throws SQLException {
        Connection connection = DBConnection.getInstance().connect();
        PreparedStatement seleccionar = connection.prepareStatement("INSERT INTO transacciones(fecha, tipo_transaccion, especificacion, tipo_pago, pago, referencia_bancaria, empleado) VALUES (?,?,?,?,?,?,?)");
        seleccionar.setTimestamp(1, fecha);
        seleccionar.setString(2, tipo);
        seleccionar.setString(3, especificacion);
        seleccionar.setString(4, tipoPago.name());
        seleccionar.setInt(5, pago);
        seleccionar.setString(6, referenciaBancaria);
        seleccionar.setString(7, empleado.getNombre());

        seleccionar.execute();

    }

    public static void insertOperation(Timestamp fecha, String operacion, Usuario empleado) throws SQLException {
        // TODO
    }

    public static ResultSet executeQuery(String sql) throws SQLException {
        Connection connection = DBConnection.getInstance().connect();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        return preparedStatement.executeQuery();
    }

    public static int lastTransactionID() throws SQLException {
        ResultSet consulta = DBConnection.getInstance().connect().prepareStatement("SELECT MAX(transaction_id) FROM transacciones").executeQuery();
        consulta.next();
        return consulta.getInt(1);
    }
}
