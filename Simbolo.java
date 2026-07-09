public class Simbolo {
    private char caracter;
    private int ascii;
    private int frecuencia;
    private String codigo;

    public Simbolo(char caracter, int frecuencia, String codigo) {
        this.caracter = caracter;
        this.ascii = (int) caracter;
        this.frecuencia = frecuencia;
        this.codigo = codigo;
    }

    public char getCaracter() { 
        return caracter; 
    }
    public int getAscii() { 
        return ascii;
    }
    public int getFrecuencia() { 
        return frecuencia; 
    }
    public String getCodigo() { 
        return codigo; 
    }
}
