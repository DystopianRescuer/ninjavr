package com.gmail.dystopianrescuer.ninjavrfx.models;

import com.gmail.dystopianrescuer.ninjavrfx.controllers.DashboardController;
import com.gmail.dystopianrescuer.ninjavrfx.utility.GestorTransacciones;

import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.*;
import java.awt.*;
import java.awt.print.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Ticket implements Printable {

    private static final DocFlavor psInFormat = DocFlavor.INPUT_STREAM.AUTOSENSE;
    private static final PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
    private static PrintService[] services = PrintServiceLookup.lookupPrintServices(psInFormat, aset);
    private static PrintService printService = PrintServiceLookup.lookupDefaultPrintService();
    private static PrinterJob printerJob = PrinterJob.getPrinterJob();

    //    private static final String LINE_SEPARATOR = "--------------------------------------------";
    private static final String LINE_SEPARATOR = "-".repeat(38);

    private String tipoTransaccion;
    private long folio;
    private final List<Transaccion> transacciones;
    private GestorTransacciones.TipoPago tipoPago;
    private Usuario empleado = new Usuario("ddedede", "frffrfr", "Cosme Fulanito", "USER");

    // Efectivo
    private double efectivo, cambio;
    // Tarjeta
    private String banco, last4, identificador;
    // Ninja Card
    private double saldoAnterior, saldoActual;


    private static double cm_to_pp(double cm) {
        return toPPI(cm * 0.393600787);
    }

    private static double toPPI(double inch) {
        return inch * 72d;
    }

    public static void main(String[] args) {

        for (PrintService service : services) {
            String svcName = service.toString();
            System.out.println("service found: " + svcName);
            if (svcName.contains("PDF")) {
                setImpresora(service);
                System.out.println("my printer found: " + svcName);
                break;
            }
        }

        if (printService != null) {

            new Ticket()
                    .setFolio(2323232)
                    .setTipoTransaccion("VENTA")
                    .addTransaction(new Transaccion("Hola", 50))
                    .addTransaction(new Transaccion("dadsds", 50))
                    .addTransaction(new Transaccion("Artursdsdoggkjjiigtgtg", 50))
                    .addTransaction(new Transaccion("gtgtgtg", 50))
                    .setTipoPago(GestorTransacciones.TipoPago.EFECTIVO)
                    .setDatosEfectivo(300, 0)
                    .imprimirTicket();

        } else {
            System.out.println("no printer services found");
        }
    }

    public Ticket() {
        transacciones = new ArrayList<>();
        printerJob = PrinterJob.getPrinterJob();


        if (DashboardController.getLogged() != null) {
            empleado = DashboardController.getLogged();
        }
    }

    public Ticket setTipoTransaccion(String tipoTransaccion) {
        this.tipoTransaccion = tipoTransaccion;
        return this;
    }

    public Ticket setFolio(long folio) {
        this.folio = folio;
        return this;
    }

    public Ticket addTransaction(Transaccion transaccion) {
        transacciones.add(transaccion);
        return this;
    }

    // No implementado aún
    public Ticket addDescuento(int descuento) {
        return this;
    }

    public Ticket setTipoPago(GestorTransacciones.TipoPago tipoPago) {
        this.tipoPago = tipoPago;
        return this;
    }

    public Ticket setDatosEfectivo(double efectivo, double cambio) {
        this.efectivo = efectivo;
        this.cambio = cambio;
        return this;
    }

    public Ticket setDatosTarjeta(String banco, String last4, String identificador) {
        this.banco = banco;
        this.last4 = last4;
        this.identificador = identificador;
        return this;
    }

    public Ticket setDatosNinjaCard(double saldoAnterior, double saldoActual) {
        this.saldoAnterior = saldoAnterior;
        this.saldoActual = saldoActual;
        return this;
    }

    public void imprimirTicket() {
        try {

            printerJob.setPrintService(printService);
            printerJob.setPrintable(this, getPageFormat(printerJob));
            printerJob.print();

        } catch (PrinterException e) {
            throw new RuntimeException(e);
        }
    }

    public PageFormat getPageFormat(PrinterJob pj) {

        PageFormat pf = pj.defaultPage();
        Paper paper = pf.getPaper();

        double bodyHeight = transacciones.size() * 0.347;
        double headerHeight = 7.0;
        double footerHeight = 7.0;
        double width = cm_to_pp(5.6);
        double height = cm_to_pp(headerHeight + bodyHeight + footerHeight);
        paper.setSize(width, footerHeight);
        paper.setImageableArea(0, 10, width, height);

        pf.setOrientation(PageFormat.PORTRAIT);
        pf.setPaper(paper);

        return pf;
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {

        Font fontPlain = new Font("Monospaced", Font.PLAIN, 1).deriveFont(5.5f);
        Font fontBold = new Font("Monospaced", Font.BOLD, 1).deriveFont(5.5f);

        int result = NO_SUCH_PAGE;

        if (pageIndex == 0) {

            ImageIcon icon = new ImageIcon("C:\\Users\\diego\\OneDrive\\Escritorio\\logos\\Envio\\Nombre\\OE Nombre -01.png");
            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());

            int y = 20;
            int yShift = 10;
            int precio = 0;

            g2d.setColor(Color.BLACK);
            g2d.setFont(fontPlain);
            g2d.drawImage(icon.getImage(), 30, 30, 75, 49, null);
            y += yShift + 55;
            g2d.drawString(LINE_SEPARATOR, 0, y);
            y += yShift;
            g2d.setFont(fontBold);
            g2d.drawString(formatear("NINJA VR PARQUE CELAYA"), 0, y);
            y += yShift;
            g2d.setFont(fontPlain);
            g2d.drawString(formatear("NinjaVR Parque Celaya"), 0, y);
            y += yShift;
            g2d.drawString(formatear("Av. El Sauz, 801"), 0, y);
            y += yShift;
            g2d.drawString(formatear("Col. La Misión"), 0, y);
            y += yShift;
            g2d.drawString(formatear("C.P 38016"), 0, y);
            y += yShift;
            g2d.drawString(formatear("Celaya, Guanajuato."), 0, y);
            y += yShift;
            g2d.drawString(formatear("Tel. (55) 5555-5555"), 0, y);
            y += yShift;
            g2d.drawString(LINE_SEPARATOR, 0, y);
            y += yShift + 5;
            g2d.setFont(fontBold);
            g2d.drawString(formatear("%s".formatted(tipoTransaccion)), 0, y);
            y += yShift;
            g2d.setFont(fontPlain);
            g2d.drawString(formatear("Folio #" + this.folio), 0, y);
            y += yShift + 10;
            g2d.drawString("Descripción                    Precio", 0, y);
            y += yShift - 7;
            g2d.drawLine(0, y, 100, y);
            y += yShift + 5;

            // Un for de transacciones
            for (Transaccion t : transacciones) {
                g2d.drawString(t.getDescripcion().concat(" ".repeat(30 - t.getDescripcion().length())).concat(String.valueOf(t.getPrecio())), 0, y);
                precio += t.getPrecio();
                y += yShift;
            }

            g2d.setFont(fontBold);
            y += 5;
            g2d.drawString(formatear("TOTAL %s    %s".formatted(!tipoPago.equals(GestorTransacciones.TipoPago.NINJACARD) ? "M. N  $" : "NinjaPoints", precio), 1), 0, y);
            y += yShift + 5;
            g2d.setFont(fontPlain);
            g2d.drawString(formatear("Pago:  %s".formatted(tipoPago.name()), 1), 0, y);
            y += yShift;

            if (tipoPago.equals(GestorTransacciones.TipoPago.EFECTIVO)) {
                g2d.drawString(formatear("Recibido: %.2f".formatted(this.efectivo), 1), 0, y);
                y += yShift;
                g2d.drawString(formatear("Cambio: %.2f".formatted(this.cambio), 1), 0, y);
                y += yShift;
            } else if (tipoPago.equals(GestorTransacciones.TipoPago.TARJETA)) {
                g2d.drawString(formatear("%s ****%s".formatted(this.banco, this.last4), 1), 0, y);
                y += yShift;
                g2d.drawString(formatear("Id trans.: %s".formatted(this.identificador), 1), 0, y);
                y += yShift;
                g2d.drawString(formatear("PAGO CONTADO", 1), 0, y);
                y += yShift;
            } else {
                g2d.drawString(formatear("Saldo anterior: %s".formatted(this.saldoAnterior), 1), 0, y);
                y += yShift;
                g2d.drawString(formatear("Saldo actual: %s".formatted(this.saldoActual), 1), 0, y);
                y += yShift;
            }

            g2d.drawString(LINE_SEPARATOR, 0, y);
            y += yShift;
            g2d.drawString(formatear("¡Esperamos verte pronto!"), 0, y);
            y += yShift + 3;
            g2d.drawString(formatear("Te atendió: %s".formatted(this.empleado.getNombre())), 0, y);
            y += yShift + 10; // Nombre del wey q atiende
            g2d.drawString(formatear(LocalDateTime.now().format(DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy hh:mm a", Locale.forLanguageTag("es-MX")))), 0, y);
            y += yShift + 6;

            g2d.setFont(fontBold);
            g2d.drawString(formatear("Algún mensaje para incitar el regreso"), 0, y);
            y += yShift;
            g2d.setFont(fontPlain);

            result = PAGE_EXISTS;

            // TODO Agregar sistema de calificacion y recomendación por ninjapoints (EMPLEADO DEL MES)
        }

        return result;
    }

    private String formatear(String entrada) {
        return formatear(entrada, 0);
    }

    private String formatear(String entrada, int alineacion) {
        int espacios = LINE_SEPARATOR.length();
        String formatted;
        try {
            formatted = " ".repeat(alineacion == 0 ? (espacios / 2 - entrada.length() / 2) : (espacios - entrada.length())).concat(entrada);
        } catch (IllegalArgumentException e) {
            return entrada;
        }
        return formatted;
    }

    public static PrintService[] getImpresoras() {

        services = PrintServiceLookup.lookupPrintServices(psInFormat, aset);

        return services;
    }

    public static void setImpresora(PrintService printService) {
        Ticket.printService = printService;
    }
}