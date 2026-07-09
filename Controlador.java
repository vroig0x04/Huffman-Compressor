import java.io.File;
import java.util.List;

public class Controlador {
    private Modelo modelo;
    private Estructura struct;

    public Controlador() {
        this.modelo = new Modelo();
    }

    public void inicializar_estructura(String tipo_estructura) {
        if (tipo_estructura.equals("BinaryHeap")) {
            struct = new BinaryHeap();
        }
    }

    public ResultadoCompresion comprimir_archivo(File archivo_a_comprimir, ProgressCallback callback) {
        long tiempo_pre = System.currentTimeMillis();

        int[] frecuencias = struct.obtenerFrecuencias(archivo_a_comprimir, callback);
        struct.cargarFrecuencias(frecuencias);

        Estructura.Nodo raiz = modelo.ejecutarHuffman(struct);

        modelo.generarCodigos(raiz, "");
        modelo.calcularEficiencia(frecuencias, struct.getTotalBytes());
        modelo.mostrarCodigos();

        long size = modelo.comprimir(archivo_a_comprimir, callback);

        long tiempo_post = System.currentTimeMillis();
        long tiempo = tiempo_post - tiempo_pre;

        double entropia = modelo.getEntropia();
        double longitud = modelo.getLongitudMedia();
        double eficiencia = modelo.getEficiencia();

        return new ResultadoCompresion(tiempo, size, entropia, longitud, eficiencia);
    }

    public void descomprimir(File archivo) {
        modelo.descomprimir(archivo, struct);
    }

    public List<Simbolo> obtenerSimbolos() {
        return modelo.getSimbolos();
    }

    public Estructura.Nodo getRaiz() {
        return modelo.getRaizArbol();
    }
}