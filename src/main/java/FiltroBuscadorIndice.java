
import java.util.List;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FiltroBuscadorIndice extends Filtro {
    private String nombreArchivo;
    private List<String> palabrasBuscar;

    public FiltroBuscadorIndice(BlockingQueue<String> entrada, BlockingQueue<String> salida, String archivo) {
        super(entrada, salida);
        this.nombreArchivo = archivo;
    }
//se le da la ruta del archivo txt donde regresara una lista de Strings con las palabras a buscar
    private List<String> cargarPalabrasClave(String ruta) {
        List<String> palabras = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    palabras.add(linea.trim());
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar palabras: " + e.getMessage());
        }
        return palabras;
    }

    @Override
    protected void procesar() throws InterruptedException {
        // Cargamos las palabras clave del archivo .txt y se le asignan a la lista palabrasBuscar
        palabrasBuscar = cargarPalabrasClave(nombreArchivo);

        while (true) {
            // Recibimos el mensaje de la tubería (formato "contenido:indice")
            String mensajeRecibido = tuberiaEntrada.take();

            if (mensajeRecibido.equals("EOF")) {
                tuberiaSalida.put("EOF");
                break;
            }

           //se separa el mensaje entrante
            String[] partes = mensajeRecibido.split(":");

           //si si se tiene contenido e indice
            if (partes.length >= 2) {
                String lineaActual = partes[0];
                String indicePagina = partes[1];

//se recorre la lista palabras buscar buscando coincidencia con las lineas entrantes, palabra es el indice actual en la lista palabrasbuscar
                for (String palabra : palabrasBuscar) {
                    // Buscamos la palabra exacta
                    //se instancia el patron que buscara una coincidencia con la palabra del indice actual, buscando que se encuentre totalmente separado
                    Pattern patron = Pattern.compile("(?i)\\b" + Pattern.quote(palabra) + "\\b");
                    //se instancia el buscador que recibirá como parametro la linea actual
                    Matcher buscador = patron.matcher(lineaActual);

                    while (buscador.find()) {
   //se envia la palabra del indice actual de la lista si se encontro y la pagina
                        String resultado = palabra + " : " + indicePagina;
                        tuberiaSalida.put(resultado);
                    }
                }
            }
        }
    }
}