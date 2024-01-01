package com.gmail.dystopianrescuer.ninjavrfx.utility;

import com.gmail.dystopianrescuer.ninjavrfx.NinjaVRApplication;
import com.gmail.dystopianrescuer.ninjavrfx.controllers.modulesControllers.PrincipalController;
import com.gmail.dystopianrescuer.ninjavrfx.controllers.modulesControllers.renta.RentaController;
import com.gmail.dystopianrescuer.ninjavrfx.exceptions.NoSuchScreenException;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.xdevapi.JsonParser;
import se.vidstige.jadb.JadbConnection;
import se.vidstige.jadb.JadbDevice;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class DaemonThread {

    public static JadbConnection connection;
    public static List<JadbDevice> dispositivos, completados, faltantes;
    public static AtomicBoolean adbConnection = new AtomicBoolean(GestorConexion.checkADBconnection());

    private static final ScheduledExecutorService thread = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = Executors.defaultThreadFactory().newThread(r);
        t.setDaemon(true);
        return t;
    });
    private static List<GraphicsDevice> screens;
    private static String os_process;

    public static void startMainThread() {

        startWebhook();

        completados = new ArrayList<>();
        connection = new JadbConnection();

        thread.scheduleAtFixedRate(() -> {
            adbConnection.set(GestorConexion.checkADBconnection());
            screens = ScreenHelper.getAllScreens();
            try {
                os_process = new String(Runtime.getRuntime()
                        .exec(new String[]{"tasklist"}).getInputStream().readAllBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (adbConnection.get()) {
                try {
                    dispositivos = connection.getDevices();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                GestorConexion.purgeUSBandOfflineConnections();

                if (!dispositivos.stream().sorted(GestorConexion.DEVICE_COMPARATOR).toList().equals(completados.stream().sorted(GestorConexion.DEVICE_COMPARATOR).toList())
                        && dispositivos.size() > completados.size()) {

                    faltantes = dispositivos.stream().filter(f -> !completados.contains(f)
                            && f.getSerial().matches(GestorConexion.IP_REGEX)).toList();

                    if (ScreenHelper.getAvailableScreens().size() >= faltantes.size()) {
                        System.out.println("Dispositivo pendiente detectado");
                        faltantes.forEach(f -> {
                            System.out.println("Intentando conexión");
                            try {
                                RentaController.rentaController.addController(f, ScreenHelper.getOneScreen());
                                System.out.println("Pantalla y dispositivo enlazados");
                            } catch (NoSuchScreenException e) {
                                System.out.println("No hay pantallas");
                            }
                        });
                    }
                }

                ScreenHelper.update();
            } else {
                try {
                    GestorConexion.getLauncher().launch();
                } catch (IOException | InterruptedException ignored) {
                    // Nothing
                }
            }

            PrincipalController.principalController.updateInfo(adbConnection.get(), screens,
                    os_process.contains("pktriot.exe"), os_process.contains("py.exe"));

        }, 0, 4, TimeUnit.SECONDS);
    }

    private static void startWebhook() {

        Thread thread1 = new Thread(() -> {

            try {
                // Kills previous processes if is necessary
                Runtime.getRuntime().exec(new String[]{"taskkill", "/IM", "py.exe", "/F"}).waitFor();
                Runtime.getRuntime().exec(new String[]{"taskkill", "/IM", "pktriot.exe", "/F"}).waitFor();
                Runtime.getRuntime().exec(new String[]{"taskkill", "/IM", "scrcpy.exe", "/F"}).waitFor();

                // Starts the processes
                String webhookResource = NinjaVRApplication.class.getResource("webhook.py").getPath().replaceFirst("file:/", "");
                if (webhookResource.charAt(0) == '/') {
                    webhookResource = webhookResource.replaceFirst("/", "");
                }
                System.out.println(webhookResource);
                ProcessBuilder pb = new ProcessBuilder("py", "-u", webhookResource);
                Process webhookProcess = pb.start();

                new ProcessBuilder("pktriot", "http", "5000").start();

                // Starts the scanner
                Scanner scanner = new Scanner(webhookProcess.getInputStream());
                while (true) {
                    if (scanner.hasNextLine()) {
                        String nextLine = scanner.nextLine();
                        try {
                            System.out.println(JsonParser.parseDoc(nextLine.replaceAll("'", "\"")).toFormattedString());
                        } catch (WrongArgumentException ignored) {
                            // Nothing
                            System.out.println(nextLine);
                        }
                    } else if (!webhookProcess.isAlive()) {
                        System.out.println("Proceso webhook cerrado");
                        break;
                    }
                }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        thread1.setDaemon(true);
        thread1.start();
    }

    public static List<JadbDevice> getCompletados() {
        return completados;
    }
}
