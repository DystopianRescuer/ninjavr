package com.gmail.dystopianrescuer.ninjavrfx.controllers.modulesControllers.renta;

import com.gmail.dystopianrescuer.ninjavrfx.controllers.modulesControllers.PrincipalController;
import com.gmail.dystopianrescuer.ninjavrfx.utility.*;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import eu.hansolo.medusa.Gauge;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import se.vidstige.jadb.JadbConnection;
import se.vidstige.jadb.JadbDevice;
import se.vidstige.jadb.JadbException;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class DeviceController implements Initializable {

    private JadbDevice device;
    private GraphicsDevice screen;
    private WinDef.HWND transmision;
    private Tab tab;
    private ScheduledFuture<?> daemonTask, rentaTask;
    private boolean transmissionState, desbloqueado, temporizadorPausado;
    private String serialNumber, currentApp, batteryInfo;
    private final String defaultApp = "com.oculus.shellenv";
    private final JadbConnection jadbConnection = new JadbConnection();
    private final String[] startCommands = {"setprop debug.oculus.refreshRate 72",
            "setprop debug.oculus.textureWidth 2048", "setprop debug.oculus.textureHeight 2253",
            "setprop debug.oculus.cpuLevel 4",
            "setprop debug.oculus.gpuLevel 4"};
    private final String[] availableGames = {"Beat Saber:com.beatsaber", "Resident Evil 4:com.residentevil4"};
    private Map<String, String> juegos;
    private static final ScheduledExecutorService thread = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = Executors.defaultThreadFactory().newThread(r);
        t.setDaemon(true);
        return t;
    });

    @FXML
    private AnchorPane iniciarRenta, gestorRenta, gestorPagoTarjeta;

    @FXML
    private Gauge batteryProgress, temporizador;

    @FXML
    private ListView<String> gameList;

    @FXML
    private ButtonBase closeApp, freeMode, restart, turnOff, transmissionButton;

    @FXML
    public Button confirmTransaction;

    @FXML
    private Label screenLabel, currentAppLabel, transmissionWorking, last;

    @FXML
    private ImageView acIndicator, usbIndicator;

    // Transaction part
    @FXML
    private ToggleGroup membresia, pago;

    @FXML
    private RadioButton ninjaCardYes, ninjaCardNo, pagoEfectivo, pagoNinja, pagoTarjeta;

    @FXML
    private Label ofrecerNinjaCard, total, tieneNinjaCard;

    @FXML
    private ChoiceBox<Integer> minutes;

    @FXML
    void onTransaction() {
        if ((minutes.getValue() != null) && !Objects.isNull(pago.getSelectedToggle())) {
            GestorTransacciones.nuevaRenta(this, GestorTransacciones.TipoPago.parseTipoPago(((RadioButton) pago.getSelectedToggle()).getText()), minutes.getValue());
            confirmTransaction.setDisable(true);
        } else {
            JOptionPane.showMessageDialog(null, "Llena el formulario correctamente", "Renta de equipo", JOptionPane.WARNING_MESSAGE);
        }
    }

    @FXML
    void tipoPagoAction() {
        Stream.<Node>of(ninjaCardYes, ninjaCardNo, ofrecerNinjaCard, tieneNinjaCard).forEach(n -> n.setVisible(!pagoNinja.isSelected()));
        updateTotal();
    }

    @FXML
    void updateTotal() {
        if (minutes.getValue() != null) {
            total.setText(pagoNinja.isSelected() ? minutes.getValue() + " MINUTOS" : "$" + GestorTransacciones.getPrecioPesos(minutes.getValue()) + ".00MXN");
        }
    }


    @FXML
    private void onButtonAction(ActionEvent event) throws IOException, JadbException {
        Object source = event.getSource();
        if (source.equals(turnOff)) {
            device.executeShell("reboot -p");
            destroyController();
        } else if (source.equals(restart)) {
            device.execute("reboot");
            destroyController();
        } else if (source.equals(closeApp)) {
            device.executeShell("am force-stop " + currentApp);
        } else if (source.equals(freeMode)) {
            if (((ToggleButton) freeMode).selectedProperty().get()) {
                JOptionPane.showMessageDialog(null, "Gafas desbloqueadas, esta operación ha sido registrada");
                setDesbloqueado(true);
            } else {
                setDesbloqueado(false);
            }
        } else if (source.equals(transmissionButton)) {
            if (transmision == null) {
                startTransmission();
            } else {
                setTransmissionState(!transmissionState);
            }
        }
    }

    @FXML
    private void onRentaAction(ActionEvent e) {
        String buttonString = ((Button) e.getSource()).getText();

        if (buttonString.equals("Reanudar")) {
            temporizadorPausado = false;
            temporizador.setBarColor(Color.valueOf("#008f11"));
        } else if (buttonString.equals("Pausar")) {
            temporizadorPausado = true;
            temporizador.setBarColor(Color.valueOf("#ff1c1c"));
        } else {
            setDesbloqueado(false);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        minutes.getItems().add(20);
        minutes.getItems().add(40);
        minutes.getItems().add(60);

        juegos = new HashMap<>();
        Stream.of(availableGames).forEach(this::addJuego);

        gameList.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                if (mouseEvent.getClickCount() == 2) {
                    System.out.println("Double clicked");
                }
            }
        });

        startDaemon();
    }

    private void startTransmission() throws IOException, JadbException {
        GestorConexion.initScrCpy(device, screen, true);
        transmissionState = true;

        serialNumber = new String(device.executeShell("getprop ro.serialno").readAllBytes()).replace("\n", "");

        new Thread(() -> {
            int i = 0;
            do {
                transmision = User32.INSTANCE.FindWindow(null, serialNumber);
                i++;
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    transmissionState = false;
                    throw new RuntimeException(e);
                }
                if (i > 15) {
                    JOptionPane.showMessageDialog(null, "La transmisión ha tardado mucho en responder, inténtalo de nuevo");
                    break;
                }
            } while (transmision == null);

        }).start();
    }

    private void setTransmissionState(boolean showing) throws IOException, JadbException {
        if (!((transmision = User32.INSTANCE.FindWindow(null, serialNumber)) == null)) {
            if (showing) {
                User32.INSTANCE.ShowWindow(transmision, 9);
            } else {
                User32.INSTANCE.CloseWindow(transmision);
            }
        } else {
            transmissionState = true;
            startTransmission();
            return;
        }

        transmissionState = !transmissionState;
    }


    private void startDaemon() {

        daemonTask = thread.scheduleWithFixedDelay(() -> Platform.runLater(() -> {
            try {
                if (!ScreenHelper.getAllScreens().contains(screen) || !new JadbConnection().getDevices().contains(device)) {
                    destroyController();
                } else {
                    batteryInfo = new String(device.executeShell("dumpsys battery").readAllBytes());

                    int index = batteryInfo.indexOf("level:") + 7;
                    batteryProgress.setValue(Double.parseDouble(batteryInfo.substring(index, index + 3)));

                    usbIndicator.setOpacity(batteryInfo.contains("USB powered: true") ? 1.00 : 0.15);
                    acIndicator.setOpacity(batteryInfo.contains("AC powered: true") ? 1.00 : 0.15);

                    currentApp = new String(device.executeShell("dumpsys activity recents | grep 'Recent #0' | cut -d= -f2 | sed 's| .*||' | cut -d '/' -f1").readAllBytes()).replace("\n", "");
                    currentAppLabel.setText(currentApp);

                    transmissionWorking.setText(transmision != null ? "Activa" : "Inactiva");

                    if (!desbloqueado && !currentApp.equals(defaultApp)) {
                        NotificationHelper.displayNotification("Se ha cerrado la aplicación actual porque las gafas no están en renta");
                        device.executeShell("am force-stop " + currentApp);
                    }
                }
            } catch (IOException | JadbException e) {
                System.err.println(e.getMessage());
                destroyController();
            }
        }), 2, 4, TimeUnit.SECONDS);
    }

    int destroyed = 0;

    private synchronized void destroyController() {
        if (destroyed == 0) {
            try {
                daemonTask.cancel(true);
                setDesbloqueado(false);
                RentaController.rentaController.getTabs().remove(this.tab);
                ScreenHelper.addAvailableScreen(this.screen);
                DaemonThread.completados.remove(device);
                jadbConnection.disconnectFromTcpDevice(new InetSocketAddress(device.getSerial().split(":")[0], Integer.parseInt(device.getSerial().split(":")[1])));
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
            System.out.println("Controller destroyed");
        }
        destroyed++;
    }

    private void setDesbloqueado(boolean desbloqueado) {

        Platform.runLater(() -> {
            this.desbloqueado = desbloqueado;

            if (desbloqueado) {
                gestorRenta.toFront();
            } else {
                iniciarRenta.toFront();
                freeMode.setDisable(false);
                ((ToggleButton) freeMode).selectedProperty().setValue(false);
                if (rentaTask != null) {
                    rentaTask.cancel(true);
                }
            }
        });
    }

    public void iniciarRenta(long minutes) {

        setDesbloqueado(true);
        freeMode.setDisable(true);
        temporizador.setMaxValue(minutes);
        temporizador.setValue(minutes);
        Platform.runLater(() -> last.setText(DateTimeFormatter.ofPattern("d'/'MMMM'/'yyyy hh:mm").format(LocalDateTime.now())));

        rentaTask = thread.scheduleAtFixedRate(() -> {
            if (!temporizadorPausado) {

                temporizador.setValue(temporizador.getCurrentValue() - 1);

                if (temporizador.getCurrentValue() == 0) {
                    setDesbloqueado(false);
                }
            }

            PrincipalController.principalController.addCliente();
        }, 1, 1, TimeUnit.MINUTES);
    }

    public void setDevice(JadbDevice device) {
        this.device = device;
        Stream.of(startCommands).forEach(c -> {
            try {
                device.execute(c);
            } catch (IOException | JadbException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void setScreen(GraphicsDevice screen) {
        this.screen = screen;
        screenLabel.setText(screen.getIDstring());
    }

    public void setTab(Tab tab) {
        this.tab = tab;
    }

    private void addJuego(String formato) {

        String[] divided = formato.split(":");
        String nombre = divided[0];
        String nombreCalificado = divided[1];

        juegos.put(nombre, nombreCalificado);
        gameList.getItems().add(nombre);
    }
}
