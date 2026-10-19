package src;

import java.util.Locale;

public class Pruebas {

    private static int total = 0;
    private static int fallidas = 0;

    public static void main(String[] args) {
        costos();
        tiempos();
        validacionesDeDatos();
        capacidadYCertificacion();
        politicaDeVisitas();
        ingresosNoAlterados();
        estadosInvalidos();
        System.out.println();
        System.out.println("Resultado: " + (total - fallidas) + "/" + total + " casos correctos.");
        if (fallidas > 0) {
            System.exit(1);
        }
    }

    // ------------------------------------------------------------------ casos

    private static void costos() {
        seccion("Reglas de cobro");
        Zona m = Zona.METROPOLITANA, p = Zona.PERIFERICA;
        verificar("Diagnóstico metropolitano (1 h)", "Q150.00", q(new Diagnostico("X", "c", "d", 1.0, m)));
        verificar("Diagnóstico periférico (1.5 h)", "Q225.00", q(new Diagnostico("X", "c", "d", 1.5, p)));
        verificar("Instalación metro 2.5 h, 5 pts (horas por puntos 3.75 -> 4 h)", "Q460.00",
                q(new Instalacion("X", "c", "d", 2.5, m, 5)));
        verificar("Instalación periférica 4 h, 3 pts (gana horas estimadas)", "Q535.00",
                q(new Instalacion("X", "c", "d", 4.0, p, 3)));
        verificar("Instalación metro 2.5 h, 2 pts (hora por fracción -> 3 h)", "Q370.00",
                q(new Instalacion("X", "c", "d", 2.5, m, 2)));
        verificar("Eléctrica metro 2.5 h, 5 pts, materiales Q2,000", "Q680.00",
                q(new InstalacionElectricaCertificada("X", "c", "d", 2.5, m, 5, 2000)));
        verificar("Eléctrica periférica 3 h, 4 pts, materiales Q1,500 (garantía Q75)", "Q640.00",
                q(new InstalacionElectricaCertificada("X", "c", "d", 3.0, p, 4, 1500)));
        verificar("Aire metro 2 h, 12,000 BTU", "Q400.00", q(new MantenimientoAire("X", "c", "d", 2.0, m, 12000)));
        verificar("Aire metro 1.5 h, 24,000 BTU", "Q345.00", q(new MantenimientoAire("X", "c", "d", 1.5, m, 24000)));
    }

    private static void tiempos() {
        seccion("Tiempos de atención (días hábiles)");
        Zona m = Zona.METROPOLITANA, p = Zona.PERIFERICA;
        verificar("Diagnóstico metro", "1", dias(new Diagnostico("X", "c", "d", 1, m)));
        verificar("Diagnóstico periférica", "2", dias(new Diagnostico("X", "c", "d", 1, p)));
        verificar("Instalación metro", "3", dias(new Instalacion("X", "c", "d", 1, m, 1)));
        verificar("Instalación periférica", "5", dias(new Instalacion("X", "c", "d", 1, p, 1)));
        verificar("Eléctrica metro (+2)", "5", dias(new InstalacionElectricaCertificada("X", "c", "d", 1, m, 1, 100)));
        verificar("Eléctrica periférica (+2)", "7", dias(new InstalacionElectricaCertificada("X", "c", "d", 1, p, 1, 100)));
        verificar("Aire acondicionado", "2", dias(new MantenimientoAire("X", "c", "d", 1, m, 9000)));
    }

    private static void validacionesDeDatos() {
        seccion("src.Validaciones de datos (deben rechazarse)");
        Zona m = Zona.METROPOLITANA, p = Zona.PERIFERICA;
        rechazo("Diagnóstico de 1.6 h", () -> new Diagnostico("X", "c", "d", 1.6, m));
        rechazo("Aire acondicionado en zona periférica", () -> new MantenimientoAire("X", "c", "d", 1, p, 12000));
        rechazo("BTU 8,999", () -> new MantenimientoAire("X", "c", "d", 1, m, 8999));
        rechazo("BTU 60,001", () -> new MantenimientoAire("X", "c", "d", 1, m, 60001));
        verificar("BTU 9,000 y 60,000 aceptados (límites)", "ok", intenta(() -> {
            new MantenimientoAire("X", "c", "d", 1, m, 9000);
            new MantenimientoAire("X", "c", "d", 1, m, 60000);
        }));
        rechazo("Horas estimadas = 0", () -> new Instalacion("X", "c", "d", 0, m, 1));
        rechazo("Horas estimadas negativas", () -> new Instalacion("X", "c", "d", -2, m, 1));
        rechazo("Horas estimadas NaN", () -> new Instalacion("X", "c", "d", Double.NaN, m, 1));
        rechazo("Puntos = 0", () -> new Instalacion("X", "c", "d", 1, m, 0));
        rechazo("Materiales = 0", () -> new InstalacionElectricaCertificada("X", "c", "d", 1, m, 1, 0));
        rechazo("Código vacío", () -> new Diagnostico("  ", "c", "d", 1, m));
        rechazo("Capacidad de cuadrilla = 0", () -> new Cuadrilla("C-X", 0, false));

        TecniHogar t = nuevaEmpresa();
        rechazo("Código de orden repetido (también en minúsculas)",
                () -> t.registrarOrden(new Diagnostico("d-001", "c", "d", 1, m)));
        rechazo("Código de cuadrilla repetido", () -> t.registrarCuadrilla(new Cuadrilla("C-01", 5, false)));
        verificar("src.Ingresos tras los rechazos", "Q0.00", q(t.getIngresos().total()));
    }

    private static void capacidadYCertificacion() {
        seccion("Capacidad y certificación");
        TecniHogar t = nuevaEmpresa();
        String j = t.crearJornada("C-03").getCodigo();             // capacidad 3 h, no certificada
        t.asignarOrden(j, "I-003");                                 // 2.5 h
        verificar("I-003 (2.5 h) cabe en C-03 (3 h)", "2.50", horas(t.buscarJornada(j)));
        rechazo("D-002 (1.5 h) excede la capacidad: 4 > 3", () -> t.asignarOrden(j, "D-002"));
        verificar("Horas asignadas no cambian tras el rechazo", "2.50", horas(t.buscarJornada(j)));
        verificar("D-002 sigue pendiente", "Pendiente", t.buscarOrden("D-002").getEstado().toString());
        rechazo("Aire A-001 en cuadrilla no certificada", () -> t.asignarOrden(j, "A-001"));
        rechazo("I-003 ya asignada: no se puede asignar otra vez", () -> t.asignarOrden(j, "I-003"));

        String j2 = t.crearJornada("C-01").getCodigo();             // capacidad 8 h, certificada
        rechazo("I-003 está asignada a otra jornada", () -> t.asignarOrden(j2, "I-003"));
        verificar("Aire A-001 en cuadrilla certificada", "ok", intenta(() -> t.asignarOrden(j2, "A-001")));
        t.asignarOrden(j2, "A-002");   // 3.5 h
        t.asignarOrden(j2, "I-001");   // 6.0 h
        verificar("Horas de C-01 tras A-001, A-002, I-001", "6.00", horas(t.buscarJornada(j2)));
        rechazo("E-001 (2.5 h) excedería C-01: 8.5 > 8", () -> t.asignarOrden(j2, "E-001"));
        rechazo("src.Cuadrilla C-01 ya tiene una jornada sin cerrar", () -> t.crearJornada("C-01"));
        t.registrarCuadrilla(new Cuadrilla("C-EXACTA", 5.0, false));
        String j3 = t.crearJornada("C-EXACTA").getCodigo();         // capacidad 5 h
        verificar("Llenar la capacidad exacta (2.5 + 1.0 + 1.5 = 5 h) es válido", "ok", intenta(() -> {
            t.asignarOrden(j3, "E-001");   // 2.5 h
            t.asignarOrden(j3, "D-001");   // 3.5 h
            t.asignarOrden(j3, "D-002");   // 5.0 h
        }));
        rechazo("Iniciar jornada vacía", () -> t.iniciarJornada(t.crearJornada("C-03").getCodigo()));
    }

    private static void politicaDeVisitas() {
        seccion("Política de visitas hasta 'cancelada'");
        TecniHogar t = nuevaEmpresa();
        fallarHastaCancelar(t, "D-001", 3, 80.0);   // 3 visitas: cargos en la 2.ª y 3.ª (Q40 x 2)
        fallarHastaCancelar(t, "I-001", 3, 80.0);
        fallarHastaCancelar(t, "E-001", 2, 40.0);   // 2 visitas: un cargo
        fallarHastaCancelar(t, "A-001", 1, 0.0);    // 1 visita: sin cargos

        verificar("src.Ingresos por visitas adicionales totales", "Q200.00", q(t.getIngresos().totalPorVisitasAdicionales()));
        rechazo("Orden cancelada no puede asignarse de nuevo", () -> {
            String j = t.crearJornada("C-01").getCodigo();
            t.asignarOrden(j, "D-001");
        });

        // Una orden completada no genera más cargos y queda completada
        TecniHogar t2 = nuevaEmpresa();
        String j = t2.crearJornada("C-02").getCodigo();
        t2.asignarOrden(j, "D-001");
        t2.iniciarJornada(j);
        t2.registrarResultado(j, "D-001", ResultadoVisita.COMPLETADA);
        verificar("Orden completada", "Completada", t2.buscarOrden("D-001").getEstado().toString());
        verificar("src.Jornada cerrada al registrar todos los resultados", "Cerrada", t2.buscarJornada(j).getEstado().toString());
        verificar("src.Cuadrilla libre tras cerrar", "true", String.valueOf(t2.buscarCuadrilla("C-02").estaLibre()));
        verificar("Sin cargos adicionales", "Q0.00", q(t2.getIngresos().totalPorVisitasAdicionales()));
    }

    /** Falla la visita 'visitas' veces seguidas en jornadas distintas y verifica estado, cargo y cancelación. */
    private static void fallarHastaCancelar(TecniHogar t, String orden, int visitas, double cargoEsperado) {
        double antes = t.getIngresos().totalPorVisitasAdicionales();
        for (int i = 1; i <= visitas; i++) {
            String j = t.crearJornada("C-01").getCodigo();
            t.asignarOrden(j, orden);
            t.iniciarJornada(j);
            t.registrarResultado(j, orden, ResultadoVisita.FALLIDA);
            String esperado = i < visitas ? "Pendiente" : "Cancelada";
            verificar(orden + " visita fallida " + i + "/" + visitas, esperado, t.buscarOrden(orden).getEstado().toString());
        }
        verificar(orden + " cargos por visitas adicionales", q(cargoEsperado),
                q(t.getIngresos().totalPorVisitasAdicionales() - antes));
    }

    private static void ingresosNoAlterados() {
        seccion("Cotizar, retirar y rechazos no alteran los ingresos");
        TecniHogar t = nuevaEmpresa();
        verificar("src.Ingresos iniciales", "Q0.00", q(t.getIngresos().total()));

        OrdenServicio cotizada = new Instalacion("COT-1", "c", "d", 2.5, Zona.METROPOLITANA, 5);
        cotizada.calcularCosto();
        cotizada.diasAtencion();
        verificar("Tras cotizar (sin registrar)", "Q0.00", q(t.getIngresos().total()));

        t.registrarOrden(cotizada);
        verificar("Tras registrar COT-1 se cobra el monto completo", "Q460.00", q(t.getIngresos().total()));
        verificar("COT-1 queda pendiente", "Pendiente", t.buscarOrden("COT-1").getEstado().toString());

        String j = t.crearJornada("C-01").getCodigo();
        t.asignarOrden(j, "COT-1");
        verificar("Tras asignar (no cobra)", "Q460.00", q(t.getIngresos().total()));
        t.retirarOrden(j, "COT-1");
        verificar("Tras retirar la orden", "Q460.00", q(t.getIngresos().total()));
        verificar("COT-1 vuelve a pendiente", "Pendiente", t.buscarOrden("COT-1").getEstado().toString());

        rechazo("Asignación inválida", () -> t.asignarOrden(j, "NO-EXISTE"));
        verificar("Tras una operación rechazada", "Q460.00", q(t.getIngresos().total()));

        // El cargo adicional se registra al INICIAR, no al asignar
        t.asignarOrden(j, "COT-1");
        t.iniciarJornada(j);
        t.registrarResultado(j, "COT-1", ResultadoVisita.FALLIDA);
        String j2 = t.crearJornada("C-01").getCodigo();
        t.asignarOrden(j2, "COT-1");
        verificar("Reasignar tras visita fallida no cobra todavía", "Q40.00".equals(q(t.getIngresos().totalPorVisitasAdicionales())) ? "cobró" : "no cobró", "no cobró");
        t.iniciarJornada(j2);
        verificar("Al iniciar la 2.ª visita se cobra Q40", "Q40.00", q(t.getIngresos().totalPorVisitasAdicionales()));
    }

    private static void estadosInvalidos() {
        seccion("Operaciones en estado que no las permite");
        TecniHogar t = nuevaEmpresa();
        String j = t.crearJornada("C-01").getCodigo();
        t.asignarOrden(j, "D-001");
        rechazo("Registrar resultado antes de iniciar", () -> t.registrarResultado(j, "D-001", ResultadoVisita.COMPLETADA));
        t.iniciarJornada(j);
        rechazo("Asignar con la jornada en atención", () -> t.asignarOrden(j, "D-002"));
        rechazo("Retirar con la jornada en atención", () -> t.retirarOrden(j, "D-001"));
        rechazo("Iniciar una jornada ya iniciada", () -> t.iniciarJornada(j));
        t.registrarResultado(j, "D-001", ResultadoVisita.COMPLETADA);
        rechazo("Registrar dos veces el resultado", () -> t.registrarResultado(j, "D-001", ResultadoVisita.COMPLETADA));
        rechazo("Asignar una orden completada", () -> t.asignarOrden(t.crearJornada("C-02").getCodigo(), "D-001"));
    }

    // ------------------------------------------------------------------ utilidades

    private static TecniHogar nuevaEmpresa() {
        TecniHogar t = new TecniHogar();
        DatosIniciales.cargar(t);
        return t;
    }

    private static String q(OrdenServicio o) { return q(o.calcularCosto()); }
    private static String q(double monto) { return Formato.quetzales(monto); }
    private static String dias(OrdenServicio o) { return String.valueOf(o.diasAtencion()); }
    private static String horas(Jornada j) { return String.format(Locale.US, "%.2f", j.horasAsignadas()); }

    private static void seccion(String titulo) {
        System.out.println();
        System.out.println("=== " + titulo + " ===");
    }

    private static void verificar(String caso, String esperado, String obtenido) {
        total++;
        boolean ok = esperado.equals(obtenido);
        if (!ok) fallidas++;
        System.out.printf("[%s] %s | esperado: %s | obtenido: %s%n", ok ? "PASA" : "FALLA", caso, esperado, obtenido);
    }

    /** Verifica que la acción sea rechazada con src.OperacionInvalidaException. */
    private static void rechazo(String caso, Runnable accion) {
        String resultado = intenta(accion);
        total++;
        boolean ok = resultado.startsWith("rechazado");
        if (!ok) fallidas++;
        System.out.printf("[%s] %s | esperado: rechazado | obtenido: %s%n", ok ? "PASA" : "FALLA", caso, resultado);
    }

    private static String intenta(Runnable accion) {
        try {
            accion.run();
            return "ok";
        } catch (OperacionInvalidaException e) {
            return "rechazado (" + e.getMessage() + ")";
        }
    }
}
