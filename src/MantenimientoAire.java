package src;

public class MantenimientoAire extends OrdenServicio {

    public static final int BTU_MINIMO = 9000;
    public static final int BTU_MAXIMO = 60000;
    private static final double TARIFA_POR_HORA = 110.0;
    private static final double CARGO_GAS = 80.0;
    private static final int MAX_VISITAS = 1;

    private final int btu;

    public MantenimientoAire(String codigo, String cliente, String direccion,
                             double horasEstimadas, Zona zona, int btu) {
        super(codigo, cliente, direccion, horasEstimadas, zona);
        if (zona != Zona.METROPOLITANA) {
            throw new OperacionInvalidaException(
                    "El mantenimiento de aire acondicionado solo se acepta en zona metropolitana.");
        }
        this.btu = Validaciones.enRango(btu, BTU_MINIMO, BTU_MAXIMO, "La capacidad en BTU");
    }

    public int getBtu() { return btu; }

    @Override
    public String categoria() { return "Mantenimiento de aire acondicionado"; }

    @Override
    public double calcularCosto() {
        return tarifaBasePorZona() + TARIFA_POR_HORA * getHorasEstimadas() + CARGO_GAS;
    }

    @Override
    public int diasAtencion() { return 2; }

    @Override
    public int maxVisitas() { return MAX_VISITAS; }

    @Override
    public boolean esCompatibleCon(Cuadrilla cuadrilla) {
        return cuadrilla.isCertificadaRefrigeracion();
    }

    @Override
    protected String detalleEspecifico() {
        return super.detalleEspecifico() + " | BTU: " + btu;
    }
}
