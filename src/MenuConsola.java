package src;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class MenuConsola {

    private final TecniHogar empresa;
    private final Scanner entrada = new Scanner(System.in);

    public MenuConsola(TecniHogar empresa) {
        this.empresa = empresa;
    }

    public void ejecutar() {
        boolean salir = false;
        while (!salir) {
            mostrarMenu();
            try {
                salir = procesar(leerEntero("Opción"));
            } catch (OperacionInvalidaException e) {
                System.out.println("[RECHAZADO] " + e.getMessage());
            } catch (NoSuchElementException e) {
                System.out.println("\nSe cerró la entrada. Saliendo...");
                salir = true;
            } catch (RuntimeException e) {
                System.out.println("[ERROR INESPERADO] " + e.getMessage());
            }
        }
        System.out.println("Hasta luego.");
    }

    private void mostrarMenu() {
        System.out.println();
        System.out.println("========== TecniHogar ==========");
        System.out.println(" 1. Cotizar orden (no registra nada)");
        System.out.println(" 2. Cotizar y registrar orden");
        System.out.println(" 3. Registrar cuadrilla");
        System.out.println(" 4. Crear jornada");
        System.out.println(" 5. Asignar orden a jornada");
        System.out.println(" 6. Retirar orden de jornada");
        System.out.println(" 7. Iniciar jornada");
        System.out.println(" 8. Registrar resultado de visita");
        System.out.println(" 9. Consultar órdenes");
        System.out.println("10. Consultar cuadrillas");
        System.out.println("11. Consultar jornadas");
        System.out.println("12. Reporte: órdenes por categoría y estado");
        System.out.println("13. Reporte: ingresos");
        System.out.println("14. Reporte: jornadas no cerradas");
        System.out.println(" 0. Salir");
    }

    private boolean procesar(int opcion) {
        switch (opcion) {
            case 1:  cotizar(false); break;
            case 2:  cotizar(true); break;
            case 3:  registrarCuadrilla(); break;
            case 4:  crearJornada(); break;
            case 5:  asignarOrden(); break;
            case 6:  retirarOrden(); break;
            case 7:  iniciarJornada(); break;
            case 8:  registrarResultado(); break;
            case 9:  consultarOrdenes(); break;
            case 10: consultarCuadrillas(); break;
            case 11: consultarJornadas(); break;
            case 12: reporteOrdenes(); break;
            case 13: reporteIngresos(); break;
            case 14: reporteJornadasNoCerradas(); break;
            case 0:  return true;
            default: throw new OperacionInvalidaException("Opción no válida: " + opcion + ".");
        }
        return false;
    }

    private void cotizar(boolean registrar) {
        OrdenServicio orden = leerOrden();

        System.out.println("---- COTIZACIÓN ----");
        System.out.println(orden);

        if (!registrar) {
            System.out.println("(Solo cotización: no se registró nada.)");
            return;
        }
        if (leerSiNo("¿El cliente acepta la cotización?")) {
            empresa.registrarOrden(orden);   // aquí se registra, queda pendiente y se cobra
            System.out.println("[OK] Orden " + orden.getCodigo() + " registrada y cobrada: "
                    + Formato.quetzales(orden.calcularCosto()));
        } else {
            System.out.println("El cliente no aceptó: no se registró nada.");
        }
    }

    private OrdenServicio leerOrden() {
        System.out.println("Categorías: 1) Diagnóstico  2) Instalación  3) Instalación eléctrica certificada"
                + "  4) Mantenimiento de aire acondicionado");
        int categoria = leerEntero("Categoría");
        if (categoria < 1 || categoria > 4) {
            throw new OperacionInvalidaException("Categoría no válida: " + categoria + ".");
        }

        String codigo = leerTexto("Código de la orden");
        String cliente = leerTexto("Nombre del cliente");
        String direccion = leerTexto("Dirección");
        double horas = leerDecimal("Horas estimadas");
        Zona zona = leerZona();

        switch (categoria) {
            case 1:
                return new Diagnostico(codigo, cliente, direccion, horas, zona);
            case 2:
                return new Instalacion(codigo, cliente, direccion, horas, zona,
                        leerEntero("Cantidad de puntos"));
            case 3: {
                int puntos = leerEntero("Cantidad de puntos");
                double materiales = leerDecimal("Valor declarado de los materiales (Q)");
                return new InstalacionElectricaCertificada(codigo, cliente, direccion, horas, zona,
                        puntos, materiales);
            }
            default:
                return new MantenimientoAire(codigo, cliente, direccion, horas, zona,
                        leerEntero("Capacidad del equipo en BTU"));
        }
    }

    private void registrarCuadrilla() {
        String codigo = leerTexto("Código de la cuadrilla");
        double capacidad = leerDecimal("Capacidad máxima de horas por jornada");
        boolean certificada = leerSiNo("¿Está certificada en refrigeración?");
        empresa.registrarCuadrilla(new Cuadrilla(codigo, capacidad, certificada));
        System.out.println("[OK] src.Cuadrilla " + codigo + " registrada.");
    }

    private void crearJornada() {
        Jornada jornada = empresa.crearJornada(leerTexto("Código de la cuadrilla"));
        System.out.println("[OK] src.Jornada creada: " + jornada);
    }

    private void asignarOrden() {
        String jornada = leerTexto("Código de la jornada");
        String orden = leerTexto("Código de la orden");
        empresa.asignarOrden(jornada, orden);
        System.out.println("[OK] " + empresa.buscarJornada(jornada));
    }

    private void retirarOrden() {
        String jornada = leerTexto("Código de la jornada");
        String orden = leerTexto("Código de la orden");
        empresa.retirarOrden(jornada, orden);
        System.out.println("[OK] Orden retirada; vuelve a quedar pendiente. " + empresa.buscarJornada(jornada));
    }

    private void iniciarJornada() {
        String jornada = leerTexto("Código de la jornada");
        empresa.iniciarJornada(jornada);
        System.out.println("[OK] src.Jornada iniciada. Sus órdenes pasaron a 'En atención'.");
    }

    private void registrarResultado() {
        String jornada = leerTexto("Código de la jornada");
        String orden = leerTexto("Código de la orden");
        int opcion = leerEntero("Resultado (1 = completada, 2 = visita fallida)");
        if (opcion != 1 && opcion != 2) {
            throw new OperacionInvalidaException("Resultado no válido: " + opcion + ".");
        }
        empresa.registrarResultado(jornada, orden,
                opcion == 1 ? ResultadoVisita.COMPLETADA : ResultadoVisita.FALLIDA);
        System.out.println("[OK] Orden " + empresa.buscarOrden(orden).getCodigo() + " ahora está: "
                + empresa.buscarOrden(orden).getEstado() + ". src.Jornada: "
                + empresa.buscarJornada(jornada).getEstado() + ".");
    }

    private void consultarOrdenes() {
        System.out.println("---- ÓRDENES ----");
        for (OrdenServicio orden : empresa.getOrdenes()) {
            System.out.println(orden);
        }
    }

    private void consultarCuadrillas() {
        System.out.println("---- CUADRILLAS ----");
        for (Cuadrilla cuadrilla : empresa.getCuadrillas()) {
            System.out.println(cuadrilla);
        }
    }

    private void consultarJornadas() {
        System.out.println("---- JORNADAS ----");
        if (empresa.getJornadas().isEmpty()) {
            System.out.println("No hay jornadas creadas.");
        }
        for (Jornada jornada : empresa.getJornadas()) {
            System.out.println(jornada);
        }
    }

    private void reporteOrdenes() {
        System.out.println("---- ÓRDENES POR CATEGORÍA Y ESTADO ----");
        System.out.printf("%-38s", "Categoría");
        for (EstadoOrden estado : EstadoOrden.values()) {
            System.out.printf("%13s", estado);
        }
        System.out.println();
        for (Map.Entry<String, Map<EstadoOrden, Integer>> fila : empresa.conteoPorCategoriaYEstado().entrySet()) {
            System.out.printf("%-38s", fila.getKey());
            for (EstadoOrden estado : EstadoOrden.values()) {
                System.out.printf("%13d", fila.getValue().get(estado));
            }
            System.out.println();
        }
    }

    private void reporteIngresos() {
        Ingresos ingresos = empresa.getIngresos();
        System.out.println("---- INGRESOS ----");
        System.out.println("Por registro:              " + Formato.quetzales(ingresos.totalPorRegistro()));
        System.out.println("Por visitas adicionales:   " + Formato.quetzales(ingresos.totalPorVisitasAdicionales()));
        System.out.println("TOTAL:                     " + Formato.quetzales(ingresos.total()));
        System.out.println();
        System.out.printf("%-38s%16s%16s%16s%n", "Categoría", "Registro", "Visitas adic.", "Total");
        for (String categoria : empresa.categoriasConocidas()) {
            System.out.printf("%-38s%16s%16s%16s%n", categoria,
                    Formato.quetzales(ingresos.registroDe(categoria)),
                    Formato.quetzales(ingresos.visitasAdicionalesDe(categoria)),
                    Formato.quetzales(ingresos.totalDe(categoria)));
        }
    }

    private void reporteJornadasNoCerradas() {
        System.out.println("---- JORNADAS NO CERRADAS ----");
        if (empresa.jornadasNoCerradas().isEmpty()) {
            System.out.println("No hay jornadas sin cerrar.");
        }
        for (Jornada jornada : empresa.jornadasNoCerradas()) {
            System.out.println(jornada);
        }
    }

    private String leerTexto(String mensaje) {
        System.out.print(mensaje + ": ");
        return entrada.nextLine().trim();
    }

    private int leerEntero(String mensaje) {
        String texto = leerTexto(mensaje);
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            throw new OperacionInvalidaException("src.Formato incorrecto: se esperaba un número entero y se recibió '" + texto + "'.");
        }
    }

    private double leerDecimal(String mensaje) {
        String texto = leerTexto(mensaje);
        try {
            return Double.parseDouble(texto.replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new OperacionInvalidaException("src.Formato incorrecto: se esperaba un número y se recibió '" + texto + "'.");
        }
    }

    private boolean leerSiNo(String mensaje) {
        String texto = leerTexto(mensaje + " (s/n)").toLowerCase();
        if (texto.equals("s") || texto.equals("si") || texto.equals("sí")) {
            return true;
        }
        if (texto.equals("n") || texto.equals("no")) {
            return false;
        }
        throw new OperacionInvalidaException("Respuesta no válida: escriba 's' o 'n'.");
    }

    private Zona leerZona() {
        int opcion = leerEntero("src.Zona (1 = metropolitana, 2 = periférica)");
        if (opcion == 1) {
            return Zona.METROPOLITANA;
        }
        if (opcion == 2) {
            return Zona.PERIFERICA;
        }
        throw new OperacionInvalidaException("src.Zona no válida: " + opcion + ".");
    }
}
