package com.gmail.dystopianrescuer.ninjavrfx;

import com.gmail.dystopianrescuer.ninjavrfx.utility.db.DBConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class NinjaVRApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setMaximized(true);
        stage.setTitle("NinjaVR - Inicio de Sesión");
        stage.setScene(scene);
        stage.show();

    }

    @Override
    public void stop() throws IOException, InterruptedException {
        Runtime.getRuntime().exec(new String[]{"taskkill", "/IM", "py.exe", "/F"}).waitFor();
        Runtime.getRuntime().exec(new String[]{"taskkill", "/IM", "pktriot.exe", "/F"}).waitFor();
        Runtime.getRuntime().exec(new String[]{"taskkill", "/IM", "scrcpy.exe", "/F"}).waitFor();

        try {
            DBConnection.getInstance().closeConnection();
        } catch (NullPointerException | SQLException ignored) {
            // It doesn't matter if this happens so
        }
    }
}