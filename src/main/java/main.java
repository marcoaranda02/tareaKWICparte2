import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class main {
    public static void main(String[] args) {
        //Ruta de archivos
        String rutaResources = "src/main/resources/";

        String rutaCompletaPDF = "";
        String rutaCompletaTXT = "";

//se insertan los archivos
        try (Scanner leer = new Scanner(System.in)) {
            System.out.print("Nombre del pdf:");
            String nombrePDF = leer.nextLine();
            rutaCompletaPDF = rutaResources + nombrePDF;

            System.out.print("nombre del txt:");
            String nombreTXT = leer.nextLine();
            rutaCompletaTXT = rutaResources + nombreTXT;
        } catch (Exception e) {
            System.err.println("Error al leer los nombres: " + e.getMessage());
        }

        // Tuberías
        BlockingQueue<String> tuberia1 = new LinkedBlockingQueue<>();
        BlockingQueue<String> tuberia2 = new LinkedBlockingQueue<>();
        BlockingQueue<String> tuberia3 = new LinkedBlockingQueue<>();

    //creación de instancias de los filtros
        Filtro entrada = new FiltroLectorPDF(tuberia1, rutaCompletaPDF);
        Filtro filtroBuscador = new FiltroBuscadorIndice(tuberia1, tuberia2, rutaCompletaTXT);
        Filtro alphabetizer = new FiltroAlphabetizer(tuberia2, tuberia3);
        Filtro salida = new FiltroSalida(tuberia3);

   //Ejecución de hilos
        new Thread(entrada).start();
        new Thread(filtroBuscador).start();
        new Thread(alphabetizer).start();
        new Thread(salida).start();
    }
    }

