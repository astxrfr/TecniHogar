package src;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class Jornada {

    private static final double EPSILON = 1e-9;

    private final String codigo;
    private final Cuadrilla cuadrilla;
    private final List<OrdenServicio> ordenes = new ArrayList<>();
    private final Map<OrdenServicio, ResultadoVisita> resultados = new IdentityHashMap<>();
    private EstadoJornada estado;

    public Jornada(String codigo, Cuadrilla cuadrilla) {
        this.codigo = Validaciones.texto(codigo, "El código de la jornada");
        this.cuadrilla = Validaciones.noNulo(cuadrilla, "La cuadrilla");
        this.cuadrilla.ocupar();
        this.estado = EstadoJornada.EN_PREPARACION;
    }

    public void asignar(OrdenServicio orden) {
        Validaciones.noNulo(orden, "La orden");
        exigirEstado(EstadoJornada.EN_PREPARACION, "asignar órdenes");

        orden.validarAsignable();

        if (!orden.esCompatibleCon(cuadrilla)) {
            throw new OperacionInvalidaException("La orden " + orden.getCodigo() + " ("
                    + orden.categoria() + ") no es compatible con la cuadrilla " + cuadrilla.getCodigo()
                    + ": requiere una cuadrilla certificada en refrigeración.");
        }

        double nuevasHoras = horasAsignadas() + orden.getHorasEstimadas();
        if (nuevasHoras > cuadrilla.getCapacidadHoras() + EPSILON) {
            throw new OperacionInvalidaException("La orden " + orden.getCodigo() + " excede la capacidad: "
                    + Formato.horas(nuevasHoras) + " asignadas vs " + Formato.horas(cuadrilla.getCapacidadHoras())
                    + " de capacidad.");
        }

        orden.marcarAsignada();
        ordenes.add(orden);
    }

    public void retirar(OrdenServicio orden) {
        Validaciones.noNulo(orden, "La orden");
        exigirEstado(EstadoJornada.EN_PREPARACION, "retirar órdenes");
        if (!ordenes.contains(orden)) {
            throw new OperacionInvalidaException("La orden " + orden.getCodigo()
                    + " no pertenece a la jornada " + codigo + ".");
        }
        orden.marcarPendiente();
        ordenes.remove(orden);
    }

    public void iniciar(Ingresos ingresos) {
        Validaciones.noNulo(ingresos, "Los ingresos");
        exigirEstado(EstadoJornada.EN_PREPARACION, "iniciarse");
        if (ordenes.isEmpty()) {
            throw new OperacionInvalidaException("No se puede iniciar una jornada vacía.");
        }
        for (OrdenServicio orden : ordenes) {
            double cargo = orden.iniciarVisita();
            if (cargo > 0) {
                ingresos.registrarVisitaAdicional(orden.categoria(), cargo);
            }
        }
        estado = EstadoJornada.EN_ATENCION;
    }

    public void registrarResultado(OrdenServicio orden, ResultadoVisita resultado) {
        Validaciones.noNulo(orden, "La orden");
        Validaciones.noNulo(resultado, "El resultado");
        exigirEstado(EstadoJornada.EN_ATENCION, "registrar resultados");
        if (!ordenes.contains(orden)) {
            throw new OperacionInvalidaException("La orden " + orden.getCodigo()
                    + " no pertenece a la jornada " + codigo + ".");
        }
        if (resultados.containsKey(orden)) {
            throw new OperacionInvalidaException("La orden " + orden.getCodigo()
                    + " ya tiene resultado registrado en esta jornada.");
        }
        orden.registrarResultado(resultado);
        resultados.put(orden, resultado);
        if (resultados.size() == ordenes.size()) {
            estado = EstadoJornada.CERRADA;
            cuadrilla.liberar();
        }
    }

    private void exigirEstado(EstadoJornada requerido, String accion) {
        if (estado != requerido) {
            throw new OperacionInvalidaException("La jornada " + codigo + " no puede " + accion
                    + ": su estado es '" + estado + "' (debe estar '" + requerido + "').");
        }
    }

    public double horasAsignadas() {
        double suma = 0;
        for (OrdenServicio orden : ordenes) {
            suma += orden.getHorasEstimadas();
        }
        return suma;
    }

    public double capacidadDisponible() {
        return cuadrilla.getCapacidadHoras() - horasAsignadas();
    }

    public String getCodigo() { return codigo; }
    public Cuadrilla getCuadrilla() { return cuadrilla; }
    public EstadoJornada getEstado() { return estado; }
    public List<OrdenServicio> getOrdenes() { return Collections.unmodifiableList(ordenes); }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("[%s] src.Cuadrilla: %s | Estado: %s | Horas asignadas: %s | Disponible: %s | Órdenes: ",
                codigo, cuadrilla.getCodigo(), estado, Formato.horas(horasAsignadas()),
                Formato.horas(capacidadDisponible())));
        if (ordenes.isEmpty()) {
            sb.append("ninguna");
        }
        for (int i = 0; i < ordenes.size(); i++) {
            sb.append(i > 0 ? ", " : "").append(ordenes.get(i).getCodigo());
        }
        return sb.toString();
    }
}
