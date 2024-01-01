package com.gmail.dystopianrescuer.ninjavrfx.controllers.modulesControllers;

import eu.hansolo.medusa.Gauge;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import se.vidstige.jadb.JadbConnection;
import se.vidstige.jadb.JadbDevice;
import se.vidstige.jadb.JadbException;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PrincipalController implements Initializable {

    public static PrincipalController principalController;

    @FXML
    private Circle indicadorADB, indicadorTunnel, indicadorWebhook;

    @FXML
    private Label versionADB, problemaWebhook;

    @FXML
    private VBox dispositivosBox, pantallasBox;

    @FXML
    private Label reiniciandoADB;

    @FXML
    private Gauge clientes;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        principalController = this;

    }

    JadbConnection connection = new JadbConnection();

    public void updateInfo(boolean adbConnected, List<GraphicsDevice> screens, boolean tunnel, boolean webhook) {

        Platform.runLater(() -> {
            try {
                dispositivosBox.getChildren().clear();
                pantallasBox.getChildren().clear();

                if (adbConnected) {
                    List<JadbDevice> jadbDevices = connection.getDevices();

                    versionADB.setText(connection.getHostVersion());
                    reiniciandoADB.setVisible(false);
                    indicadorADB.setFill(Paint.valueOf("#1fff00"));

                    for (JadbDevice jadbDevice : jadbDevices) {
                        Label dispositivo = new Label();
                        dispositivo.setFont(new Font("System", 15));
                        dispositivo.setTextFill(Paint.valueOf("#baaeae"));
                        dispositivo.setText(" - " + jadbDevice.getSerial());
                        VBox.setMargin(dispositivo, new Insets(0, 0, 10, 0));
                        dispositivosBox.getChildren().add(dispositivo);
                    }
                } else {
                    indicadorADB.setFill(Paint.valueOf("#ff0000"));
                    reiniciandoADB.setVisible(true);
                    versionADB.setText("???");
                }

                for (GraphicsDevice graphicsDevice : screens) {
                    Label dispositivo = new Label();
                    dispositivo.setFont(new Font("System", 15));
                    dispositivo.setTextFill(Paint.valueOf("#baaeae"));
                    dispositivo.setText(" - " + graphicsDevice.getIDstring());
                    VBox.setMargin(dispositivo, new Insets(0, 0, 10, 0));
                    pantallasBox.getChildren().add(dispositivo);
                }

                problemaWebhook.setVisible(!tunnel || !webhook);
                indicadorTunnel.setFill(Paint.valueOf(tunnel ? "#1fff00" : "#baaeae"));
                indicadorWebhook.setFill(Paint.valueOf(webhook ? "#1fff00" : "#baaeae"));

            } catch (IOException | JadbException | NullPointerException e) {
                System.err.println(e.getMessage());
            }
        });
    }

    public void setClientes(int value) {
        clientes.setValue(value);
    }

    public void addCliente() {
        setClientes((int) (clientes.getValue() + 1));
    }
}
