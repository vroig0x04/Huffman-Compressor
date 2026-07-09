public class ResultadoCompresion {
    long tiempo;
    long size;
    double entropia;
    double longitudMedia;
    double eficiencia;

    public ResultadoCompresion(long tiempo, long size, double entropia, double longitudMedia, double eficiencia) {
        this.tiempo = tiempo;
        this.size = size;
        this.entropia = entropia;
        this.longitudMedia = longitudMedia;
        this.eficiencia = eficiencia;
    }
}
