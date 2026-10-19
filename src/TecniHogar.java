package src;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TecniHogar {

    private final Map<String, OrdenServicio> ordenes = new LinkedHashMap<>();
    private final Map<String, Cuadrilla> cuadrillas = new LinkedHashMap<>();
    private final Map<String, Jornada> jornadas = new LinkedHashMap<>();
    private final Ingresos ingresos = new Ingresos();

    public void registrarOrden(OrdenServicio orden) {
        almacenarOrden(orden);
        ingresos.registrarPorRegistro(orden.categoria(), orden.calcularCosto());
    }

    void cargarOrdenInicial(OrdenServicio orden) {
        almacenarOrden(orden);
    }

    private void almacenarOrden(OrdenServicio orden) {
        Validaciones.noNulo(orden, "La orden");
        String clave = clave(orden.getCodigo());
        if (ordenes.containsKey(clave)) {
            throw new OperacionInvalidaException("Ya existe una orden con el código " + orden.getCodigo() + ".");
        }
        ordenes.put(clave, orden);
    }

    public OrdenServicio buscarOrden(String codigo) {
        OrdenServicio orden = ordenes.get(clave(codigo));
        if (orden == null) {
            throw new OperacionInvalidaException("No existe la orden " + codigo.trim() + ".");
        }
        return orden;
    }

    public void registrarCuadrilla(Cuadrilla cuadrilla) {
        Validaciones.noNulo(cuadrilla, "La cuadrilla");
        String clave = clave(cuadrilla.getCodigo());
        if (cuadrillas.containsKey(clave)) {
            throw new OperacionInvalidaException("Ya existe una cuadrilla con el código " + cuadrilla.getCodigo() + ".");
        }
        cuadrillas.put(clave, cuadrilla);
    }

    public Cuadrilla buscarCuadrilla(String codigo) {
        Cuadrilla cuadrilla = cuadrillas.get(clave(codigo));
        if (cuadrilla == null) {
            throw new OperacionInvalidaException("No existe la cuadrilla " + codigo.trim() + ".");
        }
        return cuadrilla;
    }

    public Jornada crearJornada(String codigoCuadrilla) {
        Cuadrilla cuadrilla = buscarCuadrilla(codigoCuadrilla);
        String codigo = String.format("J-%03d", jornadas.size() + 1);
        Jornada jornada = new Jornada(codigo, cuadrilla);
        jornadas.put(clave(codigo), jornada);
        return jornada;
    }

    public Jornada buscarJornada(String codigo) {
        Jornada jornada = jornadas.get(clave(codigo));
        if (jornada == null) {
            throw new OperacionInvalidaException("No existe la jornada " + codigo.trim() + ".");
        }
        return jornada;
    }

    public void asignarOrden(String codigoJornada, String codigoOrden) {
        buscarJornada(codigoJornada).asignar(buscarOrden(codigoOrden));
    }

    public void retirarOrden(String codigoJornada, String codigoOrden) {
        buscarJornada(codigoJornada).retirar(buscarOrden(codigoOrden));
    }

    public void iniciarJornada(String codigoJornada) {
        buscarJornada(codigoJornada).iniciar(ingresos);
    }

    public void registrarResultado(String codigoJornada, String codigoOrden, ResultadoVisita resultado) {
        buscarJornada(codigoJornada).registrarResultado(buscarOrden(codigoOrden), resultado);
    }

    public Map<String, Map<EstadoOrden, Integer>> conteoPorCategoriaYEstado() {
        Map<String, Map<EstadoOrden, Integer>> conteo = new LinkedHashMap<>();
        for (OrdenServicio orden : ordenes.values()) {
            Map<EstadoOrden, Integer> porEstado = conteo.computeIfAbsent(
                    orden.categoria(), k -> new EnumMap<>(EstadoOrden.class));
            porEstado.merge(orden.getEstado(), 1, Integer::sum);
        }
        for (Map<EstadoOrden, Integer> porEstado : conteo.values()) {
            for (EstadoOrden estado : EstadoOrden.values()) {
                porEstado.putIfAbsent(estado, 0);
            }
        }
        return conteo;
    }

    public Set<String> categoriasConocidas() {
        Set<String> categorias = new LinkedHashSet<>();
        for (OrdenServicio orden : ordenes.values()) {
            categorias.add(orden.categoria());
        }
        return categorias;
    }

    public List<Jornada> jornadasNoCerradas() {
        List<Jornada> abiertas = new ArrayList<>();
        for (Jornada jornada : jornadas.values()) {
            if (jornada.getEstado() != EstadoJornada.CERRADA) {
                abiertas.add(jornada);
            }
        }
        return abiertas;
    }

    public Ingresos getIngresos() { return ingresos; }

    public Collection<OrdenServicio> getOrdenes() { return Collections.unmodifiableCollection(ordenes.values()); }

    public Collection<Cuadrilla> getCuadrillas() { return Collections.unmodifiableCollection(cuadrillas.values()); }

    public Collection<Jornada> getJornadas() { return Collections.unmodifiableCollection(jornadas.values()); }

    private static String clave(String codigo) {
        return Validaciones.texto(codigo, "El código").toUpperCase();
    }
}
