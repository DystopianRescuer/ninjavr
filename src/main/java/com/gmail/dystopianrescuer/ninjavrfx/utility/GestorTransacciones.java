package com.gmail.dystopianrescuer.ninjavrfx.utility;

import com.gmail.dystopianrescuer.ninjavrfx.controllers.DashboardController;
import com.gmail.dystopianrescuer.ninjavrfx.controllers.modulesControllers.renta.DeviceController;
import com.gmail.dystopianrescuer.ninjavrfx.models.Ticket;
import com.gmail.dystopianrescuer.ninjavrfx.models.Transaccion;
import com.gmail.dystopianrescuer.ninjavrfx.utility.db.ConsultasDB;

import javax.swing.*;
import java.sql.SQLException;
import java.sql.Timestamp;

public class GestorTransacciones {

    public enum TipoPago {
        EFECTIVO,
        TARJETA,
        NINJACARD;

        public static TipoPago parseTipoPago(String parse) {
            return switch (parse) {
                case "Pago efectivo" -> EFECTIVO;
                case "Pago tarjeta" -> TARJETA;
                default -> NINJACARD;
            };
        }
    }

    private static long precioEfectivo20 = 79, precioEfectivo40 = 139, precioEfectivo60 = 199;
    private static long precioPuntos20 = 79, precioPuntos40 = 139, precioPuntos60 = 199;

    public static void setPreciosPesos(long precioEfectivo20, long precioEfectivo40, long precioEfectivo60) {
        GestorTransacciones.precioEfectivo20 = precioEfectivo20;
        GestorTransacciones.precioEfectivo40 = precioEfectivo40;
        GestorTransacciones.precioEfectivo60 = precioEfectivo60;
    }

    public static void setPreciosPuntos(long precioPuntos20, long precioPuntos40, long precioPuntos60) {
        GestorTransacciones.precioPuntos20 = precioPuntos20;
        GestorTransacciones.precioPuntos40 = precioPuntos40;
        GestorTransacciones.precioPuntos60 = precioPuntos60;
    }

    public static long getPrecioPesos(int minutos) {
        return switch (minutos) {
            case 20 -> precioEfectivo20;
            case 40 -> precioEfectivo40;
            case 60 -> precioEfectivo60;
            default -> throw new IllegalStateException("Unexpected value: " + minutos);
        };
    }

    public static long getPrecioPuntos(int minutos) {
        return switch (minutos) {
            case 20 -> precioPuntos20;
            case 40 -> precioPuntos40;
            case 60 -> precioPuntos60;
            default -> throw new IllegalStateException("Unexpected value: " + minutos);
        };
    }


    public static void nuevaRenta(DeviceController device, TipoPago tipoPago, int minutos) {

        new Thread(() -> {

            if (!tipoPago.equals(TipoPago.NINJACARD)) {
                try {
                    double precio = getPrecioPesos(minutos);
                    int referencia = ConsultasDB.lastTransactionID() + 1;
                    String bankReference = null;

                    Ticket ticket = new Ticket();

                    if (tipoPago.equals(TipoPago.EFECTIVO)) {
                        try {
                            int efectivo = Integer.parseInt(JOptionPane.showInputDialog(null, "¿Con cuanto pagará el cliente?", "Pago efectivo", JOptionPane.QUESTION_MESSAGE));
                            int cambio = (int) (efectivo - getPrecioPesos(minutos));

                            if (cambio >= 0) {
                                ticket.setTipoTransaccion("RENTA")
                                        .setFolio(referencia)
                                        .addTransaction(new Transaccion("Renta (".concat(minutos + " minutos)"), precio))
                                        .setTipoPago(TipoPago.EFECTIVO)
                                        .setDatosEfectivo(efectivo, cambio)
                                        .imprimirTicket();
                                JOptionPane.showMessageDialog(null, "Cambio: " + cambio, "Pago Efectivo", JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                throw new IllegalArgumentException();
                            }

                        } catch (Exception e) {

                            Class<? extends Exception> exClass = e.getClass();
                            if (exClass == NumberFormatException.class) {
                                JOptionPane.showMessageDialog(null, "Favor de ingresar un número válido y vuelve a intentarlo", "Error formateo de número", JOptionPane.ERROR_MESSAGE);
                            } else if (exClass == IllegalArgumentException.class) {
                                JOptionPane.showMessageDialog(null, "La cantidad no puede ser menor que el pago, intenta de nuevo", "Error transacción", JOptionPane.ERROR_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(null, "Un error inusual ha ocurrido, vuelve a intentarlo o contacta con tu superior", "Error transacción", JOptionPane.ERROR_MESSAGE);
                            }

                            device.confirmTransaction.setDisable(false);
                            return;
                        }

                    } else if (tipoPago.equals(TipoPago.TARJETA)) {
                        // Aparece boton de cancelar pago y dejar el de operacion mientras llega alguna confirmación del webhook
                    }

                    // Registrar transacción en base de datos
                    ConsultasDB.insertTransaction(new Timestamp(System.currentTimeMillis()), "RENTA", String.valueOf(minutos), tipoPago, (int) getPrecioPesos(minutos), bankReference, DashboardController.getLogged());

                    // Autorizar en controlador para iniciar la renta
                    device.iniciarRenta(minutos);

                    JOptionPane.showMessageDialog(null, "Simulación transacción finalizada: " + tipoPago.name());
                    // Agregar nuevo cliente completado
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                }
            } else {

            }
            // Imprimir ticket
            device.confirmTransaction.setDisable(false);
        }).start();
    }

    public static void rentaNinjaCard(int minutos, String ninjaID) {

    }

    public static void regalarMinutos(String ninjaID, int minutos) {

    }
}
