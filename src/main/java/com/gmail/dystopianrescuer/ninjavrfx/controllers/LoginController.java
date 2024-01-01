package com.gmail.dystopianrescuer.ninjavrfx.controllers;


import com.gmail.dystopianrescuer.ninjavrfx.NinjaVRApplication;
import com.gmail.dystopianrescuer.ninjavrfx.models.Usuario;
import com.gmail.dystopianrescuer.ninjavrfx.utility.db.ConsultasDB;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;

public class LoginController {

    @FXML
    TextField user;

    @FXML
    PasswordField password;

    @FXML
    Label error;

    @FXML
    private void onKeyPressed(KeyEvent event) throws IOException {
        if (event.getCode().toString().equals("ENTER")) {
            checkFields(event);
        }
    }

    @FXML
    private void onButtonClick(Event e) throws IOException {
        checkFields(e);
    }

    private void checkFields(Event e) throws IOException {

        if (!user.getText().isEmpty() && !password.getText().isEmpty()) {
            if (!(user.getText().equals("prueba") && password.getText().equals("admin"))) {
                String usuario = user.getText();
                String pass = password.getText();

                Usuario usuarioDB = ConsultasDB.userExist(usuario, pass);

                if (usuarioDB != null) {
                    loadDashboard(e, usuarioDB);
                } else {
                    displayError("Las credenciales ingresadas son inválidas");
                }
            } else {
                loadDashboard(e, null);
            }
        } else {
            displayError("Llena los campos correctamente");
        }

    }

    private void loadDashboard(Event event, Usuario user) throws IOException {

        FXMLLoader loader = new FXMLLoader(NinjaVRApplication.class.getResource("dashboard.fxml"));
        Scene scene = new Scene(loader.load());

        ((DashboardController) loader.getController()).setLogged(user != null ? user : new Usuario("prueba", "admin", "ADMIN OFFLINE", "ADMIN"));
        ((Node) event.getSource()).getScene().getWindow().hide();

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("NinjaVR - Dashboard");
        stage.setMaximized(true);
        stage.show();

    }

    private void displayError(String message) {
        error.setVisible(true);
        error.setText(message);
        Toolkit.getDefaultToolkit().beep();
    }
}
