import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;

public class BinaryHeap extends Estructura {

    private int[] frecuencias;
    private Nodo[] heap;
    private int elementosActuales;

    public BinaryHeap() {
        this.frecuencias = new int[ASCII];
        this.heap = new Nodo[ASCII];
        this.elementosActuales = 0;
    }

    private long getNodoPadre(long i) {
        return (i - 1) / 2;
    }

    private int getNodoIzquierdo(int i) {
        return 2 * i + 1;
    }

    private int getNodoDerecho(int i) {
        return 2 * i + 2;
    }

    @Override
    public void insertar(Nodo nuevoNodo) {
        if (elementosActuales >= heap.length)
            return;

        long espacio = elementosActuales++;

        while (espacio > 0 && nuevoNodo.compareTo(heap[(int) getNodoPadre(espacio)]) < 0) {
            heap[(int) espacio] = heap[(int) getNodoPadre(espacio)];
            espacio = getNodoPadre(espacio);
        }
        heap[(int) espacio] = nuevoNodo;
    }

    @Override
    public Nodo extraerMin() {
        if (elementosActuales == 0)
            return null;

        Nodo min = heap[0];
        heap[0] = heap[(int) --elementosActuales];
        desplazarAbajo(0);
        return min;
    }

    private void desplazarAbajo(int indice) {
        int hijo;
        Nodo temporal = heap[indice];

        while (getNodoIzquierdo(indice) < elementosActuales) {
            hijo = getNodoIzquierdo(indice);

            if (hijo != elementosActuales - 1 && heap[hijo + 1].compareTo(heap[hijo]) < 0) {
                hijo++;
            }

            if (heap[hijo].compareTo(temporal) < 0) {
                heap[indice] = heap[hijo];
            } else {
                break;
            }
            indice = hijo;
        }
        heap[indice] = temporal;
    }

    @Override
    public int[] obtenerFrecuencias(File archivo, ProgressCallback callback) {
        try (BufferedInputStream lector = new BufferedInputStream(new FileInputStream(archivo))) {
        int c;
        int bytesLeidos = 0;
        while ((c = lector.read()) != -1) {
            if (c < ASCII) { 
                frecuencias[c]++;
            }
            callback.onProgress(++bytesLeidos);
        }
    } catch (IOException e) {
        System.err.println("Error al leer el archivo: " + e.getMessage());
    }
    
    return frecuencias;
    }

    @Override
    public void cargarFrecuencias(int[] frec) {
        this.frecuencias = frec;
        for (int i = 0; i < frecuencias.length; i++) {
            if (frecuencias[i] > 0) {
                insertar(new Nodo(i, frecuencias[i]));
            }
        }
    }

    @Override
    public void printFrecuencias() {
        for (int c = 0; c < frecuencias.length; c++) {
            if (frecuencias[c] > 0) {
                System.out.printf("Byte %d (%c) : %d%n", c, (char) c, frecuencias[c]);
            } 
        }
    }

    
    @Override
    public int getElementosActuales() {
        return elementosActuales;
    }

    @Override
    public int[] getFrecuencias() {
        return frecuencias;
    }

    public Nodo[] getHeap() {
        return heap;
    }

    /**
     * Total de bytes leídos del fichero
     */
    public int getTotalBytes() {
        int total = 0;
        for (int f : frecuencias) {
            total += f;
        }
        return total;
    }
}