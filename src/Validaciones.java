package src;

public final class Validaciones {

    private Validaciones() { }

    public static String texto(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new OperacionInvalidaException(campo + " no puede estar vacío.");
        }
        return valor.trim();
    }

    public static double positivo(double valor, String campo) {
        if (!Double.isFinite(valor) || valor <= 0) {
            throw new OperacionInvalidaException(campo + " debe ser un número mayor que cero.");
        }
        return valor;
    }

    public static int enteroPositivo(int valor, String campo) {
        if (valor <= 0) {
            throw new OperacionInvalidaException(campo + " debe ser un entero positivo.");
        }
        return valor;
    }

    public static int enRango(int valor, int min, int max, String campo) {
        if (valor < min || valor > max) {
            throw new OperacionInvalidaException(campo + " debe estar entre " + min + " y " + max + ".");
        }
        return valor;
    }

    public static <T> T noNulo(T valor, String campo) {
        if (valor == null) {
            throw new OperacionInvalidaException(campo + " es obligatorio.");
        }
        return valor;
    }
}
