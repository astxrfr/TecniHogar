package src;

public class Instalacion extends OrdenServicio {

    private static final double TARIFA_POR_HORA = 90.0;
    private static final double HORAS_POR_PUNTO = 0.75;
    private static final int MAX_VISITAS = 3;

    private final int puntos;

    public Instalacion(String codigo, String cliente, String direccion,
                       double horasEstimadas, Zona zona, int puntos) {
        super(codigo, cliente, direccion, horasEstimadas, zona);
        this.puntos = Validaciones.enteroPositivo(puntos, "La cantidad de puntos");
    }

    public int getPuntos() { return puntos; }

    public double horasPorPuntos() {
        return puntos * HORAS_POR_PUNTO;
    }

    public int horasFacturables() {
        double mayor = Math.max(getHorasEstimadas(), horasPorPuntos());
        return (int) Math.ceil(mayor - 1e-9);
    }

    @Override
    public String categoria() { return "Instalación"; }

    @Override
    public double calcularCosto() {
        return tarifaBasePorZona() + TARIFA_POR_HORA * horasFacturables();
    }

    @Override
    public int diasAtencion() { return porZona(3, 5); }

    @Override
    public int maxVisitas() { return MAX_VISITAS; }

    @Override
    protected String detalleEspecifico() {
        return super.detalleEspecifico() + " | Puntos: " + puntos;
    }
}
