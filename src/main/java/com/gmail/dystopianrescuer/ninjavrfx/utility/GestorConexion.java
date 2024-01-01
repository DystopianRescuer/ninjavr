package com.gmail.dystopianrescuer.ninjavrfx.utility;

import se.vidstige.jadb.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Comparator;

public final class GestorConexion {

    public static void main(String[] args) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api-gw.payclip.com/paymentrequest"))
                .header("accept", "application/vnd.com.payclip.v1+json")
                .header("content-type", "application/json; charset=UTF-8")
                .header("x-api-key", "Basic ODBhMTkxMDQtNzMxNC00NmI5LWFjMTEtMGY0MmVhODNkYzY1OmQyNzhjNjBiLTQ2MjMtNDM1Yi04ZjIzLWYxNjQ3YzI5NjkyNA==")
                .method("POST", HttpRequest.BodyPublishers.ofString("{\"amount\":1,\"assigned_user\":\"diegobravis20@gmail.com\",\"reference\":\"fsdfdfsfddsffd\",\"message\":\"cobro\"}"))
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
    }

    public static final String IP_REGEX = "^((\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])\\.){3}(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5]):\\d+$";
    public static final Comparator<JadbDevice> DEVICE_COMPARATOR = (o1, o2) -> {
        if (o1.getSerial().matches(GestorConexion.IP_REGEX)) {
            if (o2.getSerial().matches(GestorConexion.IP_REGEX)) {
                return o1.getSerial().compareTo(o2.getSerial());
            } else {
                return 1;
            }
        } else {
            return -1;
        }
    };


    public static void initScrCpy(JadbDevice device, GraphicsDevice screen, boolean fullscreen) {
        try {
            String command = "scrcpy -s " + device.getSerial() +
                    " --window-x " + screen.getDefaultConfiguration().getBounds().x
                    + " --window-title " +
                    new String(device.executeShell("getprop ro.serialno").readAllBytes()).replace("\n", "")
                    + " --crop 1600:900:2017:510"
                    + (fullscreen ? " --fullscreen" : "");
            System.out.println("command = " + command);
            Runtime.getRuntime().exec(command.split(" "));
        } catch (IOException | JadbException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean checkADBconnection() {
        JadbConnection prueba = new JadbConnection();
        try {
            prueba.getHostVersion();
        } catch (IOException | JadbException e) {
            return false;
        }
        return true;
    }

    static AdbServerLauncher launcher = new AdbServerLauncher(new Subprocess(), System.getenv());

    public static AdbServerLauncher getLauncher() {
        return launcher;
    }

    static JadbConnection connection = new JadbConnection();

    public static void purgeUSBandOfflineConnections() {
        try {
            connection.getDevices().forEach(d -> {
                try {
                    if (!d.getSerial().matches(IP_REGEX) && !DaemonThread.getCompletados().contains(d)) {
                        String ip;
                        if ((ip = new String(d.executeShell("ip route | awk '{print $9}'").readAllBytes()).replace("\n", "")).isBlank()) {
                            JOptionPane.showMessageDialog(null, "Revisa la conexión de las gafas");
                            return;
                        }
                        System.out.println(ip);
                        d.enableAdbOverTCP();
                        connection.connectToTcpDevice(new InetSocketAddress(ip, 5555));
                        DaemonThread.getCompletados().add(d);
                        JOptionPane.showMessageDialog(null, "Conexión inalambrica del dispositivo habilitada, puedes desconectar si así lo necesitas");
                    } else {
                        try {
                            d.getState();
                        } catch (JadbException e) {
                            connection.disconnectFromTcpDevice(new InetSocketAddress(d.getSerial().split(":")[0], Integer.parseInt(d.getSerial().split(":")[1])));
                        }
                    }
                } catch (IOException | JadbException | ConnectionToRemoteDeviceException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException | JadbException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getDeviceSerial(JadbDevice device) {
        try {
            return new String(device.executeShell("getprop ro.serialno").readAllBytes());
        } catch (IOException | JadbException e) {
            throw new RuntimeException(e);
        }
    }
}
