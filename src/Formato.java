package src;

public final class Formato {

    private Formato() { }

    public static String quetzales(double monto) {
        return "Q" + String.format("%,.2f", monto);
    }

    public static String horas(double horas) {
        return String.format("%.2f h", horas);
    }
}
