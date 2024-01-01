package com.gmail.dystopianrescuer.ninjavrfx.utility;

import java.awt.*;

public class NotificationHelper {

    //Obtain only one instance of the SystemTray object
    static SystemTray tray = SystemTray.getSystemTray();

    public static void displayNotification(String message) {

        TrayIcon trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().createImage(""),
                "NinjaVR notification");

        //Let the system resize the image if needed
        trayIcon.setImageAutoSize(true);
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }

        trayIcon.displayMessage("NinjaVR", message, TrayIcon.MessageType.INFO);

        tray.remove(trayIcon);
    }
}
