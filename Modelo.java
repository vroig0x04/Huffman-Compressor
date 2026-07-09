import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ArrayList;

public class Modelo {
    private String[] tablaCodigos;
    private double eficiencia;
    private double longitudMedia;
    private double entropia;
    private Estructura estructura;
    private long elementosActuales;
    private Estructura.Nodo raiz;

    public Modelo() {
        this.tablaCodigos = new String[Estructura.ASCII];
        this.eficiencia = 0.0;
        this.longitudMedia = 0.0;
        this.entropia = 0.0;
        this.elementosActuales = 0;
    }

    /**
     * Construye el árbol según la estructura proporcionada
     * que se desea comprimir
     * @param estructura BinaryHeap o FibonacciHeap
     * @return nodo con frecuencia mínima (nodo raiz)
     */
    public Estructura.Nodo ejecutarHuffman(Estructura estructura) {
        this.estructura = estructura;
        this.elementosActuales = this.estructura.getElementosActuales();

        while (estructura.getElementosActuales() > 1) {
            Estructura.Nodo izq = estructura.extraerMin();
            Estructura.Nodo der = estructura.extraerMin();

            Estructura.Nodo padre = new Estructura.Nodo(-1, izq.getFrecuencia() + der.getFrecuencia());
            padre.setNodoIzq(izq);
            padre.setNodoDer(der);

            estructura.insertar(padre);
        }

        this.raiz = estructura.extraerMin();
        return raiz;
    }

    /**
     * Genera las palabras código, garantizando un código unívocamente
     * descifrable, es decir, todas son palabras prefijo.
     * @param nodoRaiz del árbol
     * @param codigoActual con el que se desea empezar. Siempre <"">
     */
    public void generarCodigos(Estructura.Nodo nodoRaiz, String codigoActual) {
        if (codigoActual.equals("")) {
            tablaCodigos = new String[Estructura.ASCII];
        }

        if (nodoRaiz == null) return;

        if (nodoRaiz.getNodoIzq() == null && nodoRaiz.getNodoDer() == null) {
            if (codigoActual.isEmpty()) {
                codigoActual = "0";
            }
            tablaCodigos[nodoRaiz.getValorByte()] = codigoActual;
            return;
        }

        generarCodigos(nodoRaiz.getNodoIzq(), codigoActual + "0");
        generarCodigos(nodoRaiz.getNodoDer(), codigoActual + "1");
    }

    /**
     * Calcula la entropía de Shannon H = -sum(p(i) * log2(p(i)))
     */
    private double calcularEntropia(int[] frecuencias, long totalBytes) {
        double h = 0.0;
        for (int i = 0; i < Estructura.ASCII; i++) {
            if (frecuencias[i] > 0) {
                double p = (double) frecuencias[i] / totalBytes;
                h += -p * (Math.log(p) / Math.log(2));
            }
        }
        return h;
    }

    /**
     * Calcula la longitud media de los códigos L = sum(p(i) * |código(i)|)
     */
    private double calcularLongitudMedia(int[] frecuencias, long totalBytes) {
        double l = 0.0;
        for (int i = 0; i < Estructura.ASCII; i++) {
            if (frecuencias[i] > 0 && tablaCodigos[i] != null) {
                double p = (double) frecuencias[i] / totalBytes;
                l += p * tablaCodigos[i].length();
            }
        }
        return l;
    }

    /**
     * Calcula la eficiencia del código como η = H / L * 100
     */
    private double calcularEficienciaRelativa(double entropia, double longitudMedia) {
        if (longitudMedia == 0) return 0.0;
        return (entropia / longitudMedia) * 100;
    }

    /**
     * Calcula la eficiencia
     * @param frecuencias array de frecuencias de cada byte
     * @param totalBytes  número total de bytes del fichero original
     */
    public void calcularEficiencia(int[] frecuencias, long totalBytes) {
        entropia = 0.0;
        longitudMedia = 0.0;
        eficiencia = 0.0;

        if (totalBytes == 0) return;

        entropia = calcularEntropia(frecuencias, totalBytes);
        longitudMedia = calcularLongitudMedia(frecuencias, totalBytes);
        eficiencia = calcularEficienciaRelativa(entropia, longitudMedia);
    }

    /**
     * Muestra los códigos prefijo de cada byte y su representación ASCII
     */
    public void mostrarCodigos() {
        System.out.println("\n--- Tabla de códigos Huffman ---");
        System.out.printf("%-10s %-15s %-10s%n", "Byte", "Carácter", "Código");
        System.out.println("-".repeat(40));

        for (int i = 0; i < Estructura.ASCII; i++) {
            if (tablaCodigos[i] != null) {
                String representacion;

                if (i == '\n')               representacion = "\\n";
                else if (i == '\t')          representacion = "\\t";
                else if (i == '\r')          representacion = "\\r";
                else if (i == ' ')           representacion = "(espacio)";
                else if (i < 32 || i == 127) representacion = "(ctrl-" + i + ")";
                else                         representacion = String.valueOf((char) i);

                System.out.printf("%-10d %-15s %-10s%n", i, representacion, tablaCodigos[i]);
            }
        }
    }

    /**
     * Esta función es la encargada de llevar a cabo la compresión real del fichero. Dicha compresión
     * requiere leer el fichero 2 veces. Una primera para obtener las frecuencias y generar el árbol
     * con las palabras código y una segunda vez para generar el header y comprimirlo.
     * @param arhivo_original fichero a comprimir
     * @param callback        se invoca con el número de bytes leídos hasta el momento,
     *                        permitiendo actualizar la barra de progreso en tiempo real
     */
    public long comprimir(File arhivo_original, ProgressCallback callback) {
        long totalBits = 0;
        int[] frecuencias = estructura.getFrecuencias();
        String extension = obtenerExtension(arhivo_original.toString());
        byte[] extBytes = extension.getBytes(StandardCharsets.UTF_8);
        int numSimbolos = 0;

        for (int i = 0; i < Estructura.ASCII; i++) {
            if (frecuencias[i] > 0) {
                totalBits += (long) frecuencias[i] * tablaCodigos[i].length();
                numSimbolos++;
            }
        }

        String rutaSalida = arhivo_original.getAbsolutePath().replaceAll("\\.[^.]+$", "") + ".huff";
        Estructura.Writer writer = estructura.new Writer(rutaSalida);

        writer.escribirByte(extBytes.length & 0xFF);
        for (byte b : extBytes) {
            writer.escribirByte(b);
        }
        numSimbolos--;

        writer.escribirByte(numSimbolos);

        for (int i = 0; i < Estructura.ASCII; i++) {
            if (frecuencias[i] > 0) {
                writer.escribirByte(i);
                writer.escribirByte((frecuencias[i] >> 24) & 0xFF);
                writer.escribirByte((frecuencias[i] >> 16) & 0xFF);
                writer.escribirByte((frecuencias[i] >> 8)  & 0xFF);
                writer.escribirByte(frecuencias[i]         & 0xFF);
            }
        }

        for (int shift = 56; shift >= 0; shift -= 8) {
            writer.escribirByte((int) ((totalBits >> shift) & 0xFF));
        }

        Estructura.Reader reader = estructura.new Reader(arhivo_original);
        int buffer = 0, bitsBuffer = 0;
        int bytesLeidos = 0;
        int bytesFrecuencias = estructura.getTotalBytes();
        int c;

        while ((c = reader.leerByte()) != -1) {
            String codigo = tablaCodigos[c];
            for (char bit : codigo.toCharArray()) {
                buffer = (buffer << 1) | (bit - '0');
                bitsBuffer++;
                if (bitsBuffer == 8) {
                    writer.escribirByte(buffer);
                    buffer = 0;
                    bitsBuffer = 0;
                }
            }
            bytesLeidos++;
            callback.onProgress(bytesFrecuencias + bytesLeidos);
        }

        if (bitsBuffer > 0) {
            buffer <<= (8 - bitsBuffer);
            writer.escribirByte(buffer);
        }

        reader.cerrar();
        writer.cerrar();

        File archivo_comprimido = new File(rutaSalida);
        return archivo_comprimido.length();
    }

    public String obtenerExtension(String nombreFichero) {
        if (nombreFichero == null) return "";

        int indicePunto = nombreFichero.lastIndexOf('.');

        /*
        1. Si no hay punto (-1).
        2. Si el punto es el último carácter (nombre.)
        3. Si el punto es el primero (.gitignore)
        */
        if (indicePunto == -1 || indicePunto == nombreFichero.length() - 1) {
            return "";
        }

        return nombreFichero.substring(indicePunto + 1);
    }

    public void descomprimir(File archivo, Estructura struct) {
        this.estructura = struct;
        Estructura.Reader reader = estructura.new Reader(archivo);

        int bytesExtension = reader.leerByte();
        byte[] extBytes = new byte[bytesExtension];
        for (int i = 0; i < extBytes.length; i++) {
            extBytes[i] = (byte) reader.leerByte();
        }
        String extension = new String(extBytes, StandardCharsets.UTF_8);

        int numSimbolos = reader.leerByte() & 0xFF;
        numSimbolos++;
        int[] frecuencias = new int[Estructura.ASCII];

        for (int i = 0; i < numSimbolos; i++) {
            int valorByte = reader.leerByte() & 0xFF;
            int frecuencia = ((reader.leerByte() & 0xFF) << 24) |
                             ((reader.leerByte() & 0xFF) << 16) |
                             ((reader.leerByte() & 0xFF) << 8)  |
                              (reader.leerByte() & 0xFF);
            frecuencias[valorByte] = frecuencia;
        }

        long totalBits = 0;
        for (int i = 0; i < 8; i++) {
            totalBits = (totalBits << 8) | (reader.leerByte() & 0xFF);
        }

        estructura.cargarFrecuencias(frecuencias);
        estructura.printFrecuencias();
        Estructura.Nodo raiz = ejecutarHuffman(estructura);

        String rutaSalida = archivo.getAbsolutePath().replaceAll("\\.[^.]+$", "") + "_dec." + extension;
        Estructura.Writer writer = estructura.new Writer(rutaSalida);

        Estructura.Nodo actual = raiz;
        long bitsLeidos = 0;
        int byteActual;

        if (raiz.getNodoIzq() == null && raiz.getNodoDer() == null) {
            long bits = totalBits;
            for (long i = 0; i < bits; i++) {
                writer.escribirByte(raiz.getValorByte());
            }
            reader.cerrar();
            writer.cerrar();
            return;
        }

        while (bitsLeidos < totalBits && (byteActual = reader.leerByte()) != -1) {
            for (int bit = 7; bit >= 0 && bitsLeidos < totalBits; bit--) {
                int b = (byteActual >> bit) & 1;

                actual = (b == 0) ? actual.getNodoIzq() : actual.getNodoDer();

                if (actual.getNodoIzq() == null && actual.getNodoDer() == null) {
                    writer.escribirByte(actual.getValorByte());
                    actual = raiz;
                }
                bitsLeidos++;
            }
        }

        reader.cerrar();
        writer.cerrar();
    }

    public List<Simbolo> getSimbolos() {
        List<Simbolo> lista = new ArrayList<>();

        for (int i = 0; i < Estructura.ASCII; i++) {
            if (tablaCodigos[i] != null && tablaCodigos[i].length() > 0) {
                int frecuencia = 0;
                if (estructura instanceof BinaryHeap) {
                    frecuencia = ((BinaryHeap) estructura).getFrecuencias()[i];
                }
                if (frecuencia > 0) {
                    lista.add(new Simbolo((char) i, frecuencia, tablaCodigos[i]));
                }
            }
        }
        return lista;
    }

    public double getEficiencia() { return eficiencia; }
    public double getEntropia() { return entropia; }
    public double getLongitudMedia() { return longitudMedia; }
    public long getElementosActuales() { return elementosActuales; }
    public Estructura.Nodo getRaizArbol() { return raiz; }
}