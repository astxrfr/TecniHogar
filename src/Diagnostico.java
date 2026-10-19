package src;

public class Diagnostico extends OrdenServicio {

    public static final double HORAS_MAXIMAS = 1.5;
    private static final double COSTO_METROPOLITANA = 150.0;
    private static final double COSTO_PERIFERICA = 225.0;
    private static final int MAX_VISITAS = 3;

    public Diagnostico(String codigo, String cliente, String direccion,
                       double horasEstimadas, Zona zona) {
        super(codigo, cliente, direccion, horasEstimadas, zona);
        if (horasEstimadas > HORAS_MAXIMAS) {
            throw new OperacionInvalidaException("Un diagnóstico no puede superar "
                    + HORAS_MAXIMAS + " horas estimadas.");
        }
    }

    @Override
    public String categoria() { return "Diagnóstico"; }

    @Override
    public double calcularCosto() { return porZona(COSTO_METROPOLITANA, COSTO_PERIFERICA); }

    @Override
    public int diasAtencion() { return porZona(1, 2); }

    @Override
    public int maxVisitas() { return MAX_VISITAS; }
}
