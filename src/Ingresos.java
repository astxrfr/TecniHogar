package src;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class Ingresos {

    private final Map<String, Double> porRegistro = new LinkedHashMap<>();
    private final Map<String, Double> porVisitasAdicionales = new LinkedHashMap<>();

    void registrarPorRegistro(String categoria, double monto) {
        Validaciones.positivo(monto, "El monto");
        porRegistro.merge(categoria, monto, Double::sum);
    }

    void registrarVisitaAdicional(String categoria, double monto) {
        Validaciones.positivo(monto, "El monto");
        porVisitasAdicionales.merge(categoria, monto, Double::sum);
    }

    public double totalPorRegistro() { return suma(porRegistro); }

    public double totalPorVisitasAdicionales() { return suma(porVisitasAdicionales); }

    public double total() { return totalPorRegistro() + totalPorVisitasAdicionales(); }

    public double registroDe(String categoria) { return porRegistro.getOrDefault(categoria, 0.0); }

    public double visitasAdicionalesDe(String categoria) {
        return porVisitasAdicionales.getOrDefault(categoria, 0.0);
    }

    public double totalDe(String categoria) {
        return registroDe(categoria) + visitasAdicionalesDe(categoria);
    }

    public Set<String> categorias() {
        Set<String> todas = new LinkedHashSet<>(porRegistro.keySet());
        todas.addAll(porVisitasAdicionales.keySet());
        return Collections.unmodifiableSet(todas);
    }

    private static double suma(Map<String, Double> mapa) {
        double total = 0;
        for (double valor : mapa.values()) {
            total += valor;
        }
        return total;
    }
}
