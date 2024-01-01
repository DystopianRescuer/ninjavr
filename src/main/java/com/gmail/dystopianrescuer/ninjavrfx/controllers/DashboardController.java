package com.gmail.dystopianrescuer.ninjavrfx.controllers;

import com.gmail.dystopianrescuer.ninjavrfx.NinjaVRApplication;
import com.gmail.dystopianrescuer.ninjavrfx.models.Usuario;
import com.gmail.dystopianrescuer.ninjavrfx.utility.DaemonThread;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class DashboardController implements Initializable {

    private static Usuario logged;

    @FXML
    private Label empleadoLabel;

    @FXML
    private Pane principal, ninjaCard, renta, ventas, gestion;
    @FXML
    private Button principalButton, ninjaCardButton, rentaButton, ventasButton, gestionButton;

    @FXML
    void handleClick(ActionEvent event) {
        Object source = event.getSource();

        if (source.equals(principalButton)) {
            principal.toFront();
        } else if (source.equals(ninjaCardButton)) {
            ninjaCard.toFront();
        } else if (source.equals(rentaButton)) {
            renta.toFront();
        } else if (source.equals(ventasButton)) {
            ventas.toFront();
        } else if (source.equals(gestionButton)) {
            gestion.toFront();
        } else {
            Platform.exit();
        }

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        loadNodes();

    }

    private void loadNodes() {

        try {

            // Renta node
            FXMLLoader rentaLoader = new FXMLLoader(NinjaVRApplication.class.getResource("modulos/renta.fxml"));
            AnchorPane rentaNode = rentaLoader.load();
            renta.getChildren().add(rentaNode);

            // Principal node
            FXMLLoader principalLoader = new FXMLLoader(NinjaVRApplication.class.getResource("modulos/principal.fxml"));
            AnchorPane principalNode = principalLoader.load();
            principal.getChildren().add(principalNode);

            // Ninja card node
            FXMLLoader ninjaLoader = new FXMLLoader(NinjaVRApplication.class.getResource("modulos/ninjacard.fxml"));
            AnchorPane ninjaNode = ninjaLoader.load();
            ninjaCard.getChildren().add(ninjaNode);

            // Gestion node
            FXMLLoader gestionLoader = new FXMLLoader(NinjaVRApplication.class.getResource("modulos/gestion.fxml"));
            AnchorPane gestionNode = gestionLoader.load();
            gestion.getChildren().add(gestionNode);

//            // Ventas node
//            FXMLLoader ventasLoader = new FXMLLoader(NinjaVRApplication.class.getResource("modulos/ventas.fxml"));
//            AnchorPane ventasNode = ventasLoader.load();
//            principal.getChildren().add(ventasNode);

            principal.toFront();

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            DaemonThread.startMainThread();
        }

    }

    public void setLogged(Usuario logged) {

        DashboardController.logged = logged;

        empleadoLabel.setText(DashboardController.logged.getNombre());

        if (!logged.isAdmin()) {
            ((VBox) ventasButton.getParent()).getChildren().remove(ventasButton);
            ((VBox) gestionButton.getParent()).getChildren().remove(gestionButton);
        }
    }

    public static Usuario getLogged() {
        return logged;
    }
}