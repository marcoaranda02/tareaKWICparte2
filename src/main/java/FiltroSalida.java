import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class FiltroSalida extends Filtro {

    public FiltroSalida(BlockingQueue<String> entrada) {
        super(entrada, null);
    }

    @Override
    protected void procesar() throws InterruptedException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("salida.txt"))) {
            int i = 1;
            while (true) {
                String texto = tuberiaEntrada.take();
                //System.out.println("Filtro S");
                if (texto.equals("EOF")) { // Señal de terminación
                    writer.close();
                    break;
                } else {
                    System.out.println("Iteración " + i + ":\n");
                    System.out.println(texto);
                    String contenido = "Iteración " + i + ":\n\n" + texto + "\n";
                    writer.write(contenido);
                    i++;
                }

            }
            System.out.println();
        } catch (IOException ex) {
            System.err.println("Error al generar el archivo txt: " + ex.getMessage());
        }
    }
}