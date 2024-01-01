module com.gmail.dystopianrescuer.ninjavrfx {
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.controls;
    requires java.sql;
    requires java.net.http;
    requires java.desktop;
    requires jadb;
    requires mysql.connector.java;
    requires com.sun.jna.platform;
    requires Medusa;


    opens com.gmail.dystopianrescuer.ninjavrfx to javafx.fxml;
    exports com.gmail.dystopianrescuer.ninjavrfx;
    exports com.gmail.dystopianrescuer.ninjavrfx.controllers;
    opens com.gmail.dystopianrescuer.ninjavrfx.controllers to javafx.fxml;
    exports com.gmail.dystopianrescuer.ninjavrfx.controllers.modulesControllers;
    opens com.gmail.dystopianrescuer.ninjavrfx.controllers.modulesControllers to javafx.fxml;
    exports com.gmail.dystopianrescuer.ninjavrfx.controllers.modulesControllers.renta;
    opens com.gmail.dystopianrescuer.ninjavrfx.controllers.modulesControllers.renta to javafx.fxml;
}