package src;

public class Cuadrilla {

    private final String codigo;
    private final double capacidadHoras;
    private final boolean certificadaRefrigeracion;
    private boolean libre;

    public Cuadrilla(String codigo, double capacidadHoras, boolean certificadaRefrigeracion) {
        this.codigo = Validaciones.texto(codigo, "El código de la cuadrilla");
        this.capacidadHoras = Validaciones.positivo(capacidadHoras, "La capacidad de horas");
        this.certificadaRefrigeracion = certificadaRefrigeracion;
        this.libre = true;
    }

    public String getCodigo() { return codigo; }
    public double getCapacidadHoras() { return capacidadHoras; }
    public boolean isCertificadaRefrigeracion() { return certificadaRefrigeracion; }
    public boolean estaLibre() { return libre; }

    void ocupar() {
        if (!libre) {
            throw new OperacionInvalidaException("La cuadrilla " + codigo
                    + " ya tiene una jornada sin cerrar.");
        }
        libre = false;
    }

    void liberar() {
        libre = true;
    }

    @Override
    public String toString() {
        return String.format("[%s] Capacidad: %s | Certificada en refrigeración: %s | %s",
                codigo, Formato.horas(capacidadHoras),
                certificadaRefrigeracion ? "Sí" : "No",
                libre ? "Libre" : "Con jornada sin cerrar");
    }
}
