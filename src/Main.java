package src;

public class Main {
    public static void main(String[] args) {
        TecniHogar empresa = new TecniHogar();
        DatosIniciales.cargar(empresa);
        new MenuConsola(empresa).ejecutar();
    }
}
