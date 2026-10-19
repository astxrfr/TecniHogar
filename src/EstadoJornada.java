package src;

public enum EstadoJornada {
    EN_PREPARACION("En preparación"),
    EN_ATENCION("En atención"),
    CERRADA("Cerrada");

    private final String etiqueta;

    EstadoJornada(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
