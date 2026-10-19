package src;

public final class DatosIniciales {

    private DatosIniciales() { }

    public static void cargar(TecniHogar empresa) {
        empresa.cargarOrdenInicial(new Diagnostico("D-001", "María López", "src.Zona 10, Guatemala", 1.0, Zona.METROPOLITANA));
        empresa.cargarOrdenInicial(new Diagnostico("D-002", "Carlos Pérez", "Mixco, km 15 CA-1", 1.5, Zona.PERIFERICA));

        empresa.cargarOrdenInicial(new Instalacion("I-001", "Ana Morales", "src.Zona 15, Guatemala", 2.5, Zona.METROPOLITANA, 5));
        empresa.cargarOrdenInicial(new Instalacion("I-002", "Luis Ramírez", "San José Pinula", 4.0, Zona.PERIFERICA, 3));
        empresa.cargarOrdenInicial(new Instalacion("I-003", "Sofía Díaz", "src.Zona 4, Guatemala", 2.5, Zona.METROPOLITANA, 2));

        empresa.cargarOrdenInicial(new InstalacionElectricaCertificada("E-001", "Jorge Castillo", "src.Zona 11, Guatemala",
                2.5, Zona.METROPOLITANA, 5, 2000.0));
        empresa.cargarOrdenInicial(new InstalacionElectricaCertificada("E-002", "Elena Gómez", "Villa Nueva",
                3.0, Zona.PERIFERICA, 4, 1500.0));

        empresa.cargarOrdenInicial(new MantenimientoAire("A-001", "Pedro Alvarado", "src.Zona 14, Guatemala",
                2.0, Zona.METROPOLITANA, 12000));
        empresa.cargarOrdenInicial(new MantenimientoAire("A-002", "Lucía Herrera", "src.Zona 1, Guatemala",
                1.5, Zona.METROPOLITANA, 24000));

        empresa.registrarCuadrilla(new Cuadrilla("C-01", 8.0, true));
        empresa.registrarCuadrilla(new Cuadrilla("C-02", 6.0, false));
        empresa.registrarCuadrilla(new Cuadrilla("C-03", 3.0, false));
    }
}
