import java.io.File;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public abstract class Estructura {
    protected static final int ASCII = 256;

    protected static class Nodo implements Comparable<Nodo> {
        private int valorByte;
        private int frecuencia;
        private Nodo izq, der;

        public Nodo(int valorByte, int frecuencia) {
            this.valorByte = valorByte;
            this.frecuencia = frecuencia;
        }

        public int getValorByte() { return valorByte; }
        public int getFrecuencia() { return frecuencia; }
        public Nodo getNodoIzq() { return izq; }
        public Nodo getNodoDer() { return der; }

        public void setValorByte(int valor) { this.valorByte = valor; }
        public void setFrecuencia(int frecuencia) { this.frecuencia = frecuencia; }
        public void setNodoIzq(Nodo nodo) { this.izq = nodo; }
        public void setNodoDer(Nodo nodo) { this.der = nodo; }


        @Override
        public int compareTo(Nodo otro) {
            return Integer.compare(this.frecuencia, otro.frecuencia);
        }
    }

    public abstract void insertar(Nodo nodo);
    public abstract Nodo extraerMin();
    public abstract int getElementosActuales();

    // Métodos comunes de utilidad
    protected abstract int[] obtenerFrecuencias(File archivo, ProgressCallback callback);
    protected abstract void cargarFrecuencias(int[] frecuencias);
    protected abstract void printFrecuencias();
    public abstract int getTotalBytes();
    protected abstract int[] getFrecuencias();

    protected class Reader {
        private BufferedInputStream bis;

        protected Reader(File archivo) {
            try {
                this.bis = new BufferedInputStream(new FileInputStream(archivo));
            }catch (IOException e) {
                e.printStackTrace();
            }
            
        }

        protected int leerByte() {
            try {
                return bis.read();
            }catch (IOException e) {
                e.printStackTrace();
                return -1;
            }   
        }

        protected void cerrar() {
            try {
                if (bis != null) bis.close();
            }catch (IOException e) {
                e.printStackTrace();
            }  
        }
    }

    protected class Writer {
        private BufferedOutputStream bos;
        
        protected Writer(String ruta) {
            try {
                this.bos = new BufferedOutputStream(new FileOutputStream(ruta));
            }catch (IOException e) {
                e.printStackTrace();
            }
            
        }

        protected void escribirByte(int b) {
            try {
                bos.write(b);
            }catch (IOException e) {
                e.printStackTrace();
            }
        }

        protected void cerrar() {
            try {
                if (bos != null) bos.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}