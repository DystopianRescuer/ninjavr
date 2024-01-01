package com.gmail.dystopianrescuer.ninjavrfx.controllers.modulesControllers.renta;

import com.gmail.dystopianrescuer.ninjavrfx.NinjaVRApplication;
import com.gmail.dystopianrescuer.ninjavrfx.utility.DaemonThread;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import se.vidstige.jadb.JadbDevice;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class RentaController implements Initializable {

    public static RentaController rentaController;

    @FXML
    private TabPane devicesTab;

    public void addController(JadbDevice device, GraphicsDevice screen) {


        Platform.runLater(() -> {
            try {
                String modelo = new String(device.executeShell("getprop", "ro.product.model").readAllBytes());
                System.out.println("modelo = " + modelo);

                // if modelo es igual quest 2 ejecutar...
                Tab tab = new Tab();
                tab.setText(modelo);

                FXMLLoader loader = new FXMLLoader(NinjaVRApplication.class.getResource("others/device-controller.fxml"));
                Node node = loader.load();
                DeviceController controller = loader.getController();
                controller.setDevice(device);
                controller.setScreen(screen);
                controller.setTab(tab);
                tab.setContent(node);

                devicesTab.getTabs().add(tab);

                DaemonThread.completados.add(device);

            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        });
    }

    public ObservableList<Tab> getTabs() {
        return devicesTab.getTabs();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        rentaController = this;

    }
}
