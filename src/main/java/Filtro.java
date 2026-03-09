
import java.util.concurrent.BlockingQueue;

public abstract class Filtro implements Runnable {
    protected BlockingQueue<String> tuberiaEntrada;
    protected BlockingQueue<String> tuberiaSalida;

    public Filtro(BlockingQueue<String> entrada, BlockingQueue<String> salida) {
        tuberiaEntrada = entrada;
        tuberiaSalida = salida;
    }

    @Override
    public void run() {
        try {
            procesar();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Ocurrio una interrupción en uno de los filtros");
            if (tuberiaSalida != null) {
                tuberiaSalida.offer("EOF");
            }
        }
    }

    // El método que cada filtro específico implementará
    protected abstract void procesar() throws InterruptedException;
}