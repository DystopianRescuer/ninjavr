package com.gmail.dystopianrescuer.ninjavrfx.controllers.modulesControllers;

import com.gmail.dystopianrescuer.ninjavrfx.models.Ticket;
import com.gmail.dystopianrescuer.ninjavrfx.utility.GestorTransacciones;
import com.gmail.dystopianrescuer.ninjavrfx.utility.db.ConsultasDB;
import com.gmail.dystopianrescuer.ninjavrfx.utility.db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.*;
import java.net.URL;
import java.sql.*;
import java.util.Arrays;
import java.util.ResourceBundle;

public class GestionController implements Initializable {

    private static final String ADMIN_PASSWORD = "AUDC660503";

    @FXML
    private TextField consulta;

    @FXML
    private PasswordField passwordEmpleado;

    @FXML
    private PasswordField passwordRegistro;

    @FXML
    private Label errorNoImpresoras;

    @FXML
    private TextArea generalConsole;

    @FXML
    private TabPane gestionTab;

    @FXML
    private RadioButton isAdmin;

    @FXML
    private TextField nombreEmpleado;

    @FXML
    private TextField precio20pesos;

    @FXML
    private TextField precio20puntos;

    @FXML
    private TextField precio40pesos;

    @FXML
    private TextField precio40puntos;

    @FXML
    private TextField precio60pesos;

    @FXML
    private TextField precio60puntos;

    @FXML
    private ChoiceBox<PrintService> printerSelection;

    @FXML
    private TextArea resultadoConsulta;

    @FXML
    private TextArea tunnelConsole;

    @FXML
    private TextField usuarioEmpleado;

    @FXML
    private TextArea webhookConsole;

    @FXML
    void onButtonAction(ActionEvent event) {
        Button bttnEvt = (Button) event.getSource();
        if (bttnEvt.getText().equals("Actualizar")) {
            try {
                GestorTransacciones.setPreciosPesos(Long.parseLong(precio20pesos.getText()), Long.parseLong(precio40pesos.getText()), Long.parseLong(precio60pesos.getText()));
                GestorTransacciones.setPreciosPuntos(Long.parseLong(precio20puntos.getText()), Long.parseLong(precio40puntos.getText()), Long.parseLong(precio60puntos.getText()));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Rellena todos los campos y hazlo correctamente", "Error precios", JOptionPane.ERROR_MESSAGE);
            }
        } else if (bttnEvt.getText().equals("Actualizar impresoras")) {
            loadPrinters();
        } else if (bttnEvt.getText().equals("Realizar")) {
            resultadoConsulta.clear();
            try (ResultSet rs = ConsultasDB.executeQuery(consulta.getText())) {
                ResultSetMetaData metadata = rs.getMetaData();
                int columnCount = metadata.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    resultadoConsulta.appendText(metadata.getColumnName(i) + ", ");
                }
                resultadoConsulta.appendText("\n");
                while (rs.next()) {
                    StringBuilder row = new StringBuilder();
                    for (int i = 1; i <= columnCount; i++) {
                        row.append(rs.getString(i)).append(", ");
                    }
                    resultadoConsulta.appendText("\n");
                    resultadoConsulta.appendText(row.toString());
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Tu consulta no es válida o no se pudo conectar a la base", "Error SQL", JOptionPane.ERROR_MESSAGE);
            }
        } else if (bttnEvt.getText().equals("Registrar empleado")) {
            if (passwordRegistro.getText().equals(ADMIN_PASSWORD)) {
                Connection connection;
                if (!nombreEmpleado.getText().isBlank() && !usuarioEmpleado.getText().isBlank() && !passwordEmpleado.getText().isBlank()) {
                    try {
                        connection = DBConnection.getInstance().connect();

                        try (PreparedStatement seleccionar = connection.prepareStatement("INSERT INTO usuarios(usuario, contraseña, nombre, privilegio) VALUES (?,?,?,?)")) {
                            seleccionar.setString(1, usuarioEmpleado.getText());
                            seleccionar.setString(2, passwordEmpleado.getText());
                            seleccionar.setString(3, nombreEmpleado.getText());
                            seleccionar.setString(4, isAdmin.isSelected() ? "ADMIN" : "USER");

                            seleccionar.execute();

                            JOptionPane.showMessageDialog(null, "Empleado registrado con éxito", "Registro", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(null, "Error de registro, comunicate con un superior", "Error registro", JOptionPane.ERROR_MESSAGE);
                        throw new RuntimeException(e);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Llena todos los campos", "Error de registro", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Contraseña de registro inválida", "Error de registro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void onCambioImpresora() {
        Ticket.setImpresora(printerSelection.getValue());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadPrinters();
    }

    private void loadPrinters() {
        printerSelection.getItems().clear();
        PrintService[] impresoras = Ticket.getImpresoras();

        if (!Arrays.asList(impresoras).isEmpty()) {
            errorNoImpresoras.setVisible(false);
            for (PrintService service : impresoras) {
                System.out.println("service found: " + service);
                printerSelection.getItems().add(service);
                if (PrintServiceLookup.lookupDefaultPrintService().equals(service)) {
                    printerSelection.setValue(service);
                }
            }
        } else {
            errorNoImpresoras.setVisible(true);
        }
    }
}
