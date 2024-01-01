package com.gmail.dystopianrescuer.ninjavrfx.utility;


import com.gmail.dystopianrescuer.ninjavrfx.exceptions.NoSuchScreenException;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ScreenHelper {

    static List<GraphicsDevice> screens;
    static List<GraphicsDevice> busy = new ArrayList<>();

    public static List<GraphicsDevice> getAllScreens() {
        update();
        return screens;
    }

    public static List<GraphicsDevice> getAvailableScreens() {
        update();
        return screens.stream().filter(s -> !busy.contains(s)).toList();
    }

    public static GraphicsDevice getOneScreen() throws NoSuchScreenException {
        update();
        if (!getAvailableScreens().isEmpty()) {
            GraphicsDevice screen = getAvailableScreens().get(0);
            busy.add(screen);
            return screen;
        } else {
            throw new NoSuchScreenException("There are no screens available");
        }
    }

    public static void addAvailableScreen(GraphicsDevice g) {
        busy.remove(g);
        update();
    }

    public static void update() {
        screens = Arrays.stream(GraphicsEnvironment.
                getLocalGraphicsEnvironment().getScreenDevices()).filter(s -> !s.equals(GraphicsEnvironment
                .getLocalGraphicsEnvironment().getDefaultScreenDevice())).toList();

        List<GraphicsDevice> oldBusy = busy.stream().filter(b -> screens.contains(b)).toList();
        busy = new ArrayList<>(oldBusy);
    }

}
