package src;

public abstract class OrdenServicio {

    private static final double TARIFA_BASE_METROPOLITANA = 100.0;
    private static final double TARIFA_BASE_PERIFERICA = 175.0;

    private static final double CARGO_VISITA_ADICIONAL = 40.0;

    private final String codigo;
    private final String cliente;
    private final String direccion;
    private final double horasEstimadas;
    private final Zona zona;
    private EstadoOrden estado;
    private int visitasRealizadas;

    protected OrdenServicio(String codigo, String cliente, String direccion,
                            double horasEstimadas, Zona zona) {
        this.codigo = Validaciones.texto(codigo, "El código");
        this.cliente = Validaciones.texto(cliente, "El nombre del cliente");
        this.direccion = Validaciones.texto(direccion, "La dirección");
        this.horasEstimadas = Validaciones.positivo(horasEstimadas, "Las horas estimadas");
        this.zona = Validaciones.noNulo(zona, "La zona");
        this.estado = EstadoOrden.PENDIENTE;
        this.visitasRealizadas = 0;
    }

    public abstract String categoria();

    public abstract double calcularCosto();

    public abstract int diasAtencion();

    public abstract int maxVisitas();

    public boolean esCompatibleCon(Cuadrilla cuadrilla) {
        return true;
    }

    protected double cargoVisitaAdicional() {
        return CARGO_VISITA_ADICIONAL;
    }

    protected String detalleEspecifico() {
        return "";
    }

    protected final double porZona(double metropolitana, double periferica) {
        return zona == Zona.METROPOLITANA ? metropolitana : periferica;
    }

    protected final int porZona(int metropolitana, int periferica) {
        return zona == Zona.METROPOLITANA ? metropolitana : periferica;
    }

    protected final double tarifaBasePorZona() {
        return porZona(TARIFA_BASE_METROPOLITANA, TARIFA_BASE_PERIFERICA);
    }

    void validarAsignable() {
        if (estado != EstadoOrden.PENDIENTE) {
            throw new OperacionInvalidaException("La orden " + codigo
                    + " no puede asignarse: su estado es '" + estado + "' (debe estar Pendiente).");
        }
    }

    void marcarAsignada() {
        validarAsignable();
        estado = EstadoOrden.ASIGNADA;
    }

    void marcarPendiente() {
        if (estado != EstadoOrden.ASIGNADA) {
            throw new OperacionInvalidaException("La orden " + codigo
                    + " no está asignada; su estado es '" + estado + "'.");
        }
        estado = EstadoOrden.PENDIENTE;
    }

    double iniciarVisita() {
        if (estado != EstadoOrden.ASIGNADA) {
            throw new OperacionInvalidaException("La orden " + codigo
                    + " no puede iniciar visita; su estado es '" + estado + "'.");
        }
        visitasRealizadas++;
        estado = EstadoOrden.EN_ATENCION;
        return visitasRealizadas > 1 ? cargoVisitaAdicional() : 0.0;
    }

    void registrarResultado(ResultadoVisita resultado) {
        Validaciones.noNulo(resultado, "El resultado");
        if (estado != EstadoOrden.EN_ATENCION) {
            throw new OperacionInvalidaException("La orden " + codigo
                    + " no está en atención; su estado es '" + estado + "'.");
        }
        if (resultado == ResultadoVisita.COMPLETADA) {
            estado = EstadoOrden.COMPLETADA;
        } else if (visitasRealizadas >= maxVisitas()) {
            estado = EstadoOrden.CANCELADA;
        } else {
            estado = EstadoOrden.PENDIENTE;
        }
    }

    public String getCodigo() { return codigo; }
    public String getCliente() { return cliente; }
    public String getDireccion() { return direccion; }
    public double getHorasEstimadas() { return horasEstimadas; }
    public Zona getZona() { return zona; }
    public EstadoOrden getEstado() { return estado; }
    public int getVisitasRealizadas() { return visitasRealizadas; }

    @Override
    public String toString() {
        return String.format("[%s] %s | Cliente: %s | Dir: %s | src.Zona: %s | Horas est.: %s%s"
                        + " | Estado: %s | Visitas: %d/%d | Costo: %s | Atención: %d día(s)",
                codigo, categoria(), cliente, direccion, zona, Formato.horas(horasEstimadas),
                detalleEspecifico(), estado, visitasRealizadas, maxVisitas(),
                Formato.quetzales(calcularCosto()), diasAtencion());
    }
}
