package src;

public class InstalacionElectricaCertificada extends Instalacion {

    private static final double CARGO_CERTIFICADO = 120.0;
    private static final double PORCENTAJE_GARANTIA = 0.05;
    private static final int DIAS_ADICIONALES = 2;
    private static final int MAX_VISITAS = 2;

    private final double valorMateriales;

    public InstalacionElectricaCertificada(String codigo, String cliente, String direccion,
                                           double horasEstimadas, Zona zona, int puntos,
                                           double valorMateriales) {
        super(codigo, cliente, direccion, horasEstimadas, zona, puntos);
        this.valorMateriales = Validaciones.positivo(valorMateriales, "El valor de los materiales");
    }

    public double getValorMateriales() { return valorMateriales; }

    public double calcularGarantia() {
        return valorMateriales * PORCENTAJE_GARANTIA;
    }

    @Override
    public String categoria() { return "Instalación eléctrica certificada"; }

    @Override
    public double calcularCosto() {
        return super.calcularCosto() + CARGO_CERTIFICADO + calcularGarantia();
    }

    @Override
    public int diasAtencion() { return super.diasAtencion() + DIAS_ADICIONALES; }

    @Override
    public int maxVisitas() { return MAX_VISITAS; }

    @Override
    protected String detalleEspecifico() {
        return super.detalleEspecifico() + " | Materiales: " + Formato.quetzales(valorMateriales);
    }
}
