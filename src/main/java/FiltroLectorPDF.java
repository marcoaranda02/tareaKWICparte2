
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class FiltroLectorPDF extends Filtro {
    private String rutaArchivo;

    //constructor del filtro tiene la salida a donde enviara las lineas y la ruta del pdf
    public FiltroLectorPDF(BlockingQueue<String> salida, String ruta) {
        super(null, salida); // No requiere entrada, debido a que su "entrada" es el la ruta del pdf
        this.rutaArchivo = ruta;
    }

    @Override
    protected void procesar() throws InterruptedException {
        //Se utiliza la libreria ApachePDF para cargar el archivo
        try (PDDocument documento = Loader.loadPDF(new File(rutaArchivo))) {
            //se crea el extractor
            PDFTextStripper stripper = new PDFTextStripper();
            //se obtiene el total de paginas
            int totalPaginas = documento.getNumberOfPages();
//se itera la cantidad total de pagines
            for (int i = 1; i <= totalPaginas; i++) {
                //se instancia de donde a donde se realizara la extracción
                //en este caso del principio de la pagina 1 al final de esta misma
                stripper.setStartPage(i);
                stripper.setEndPage(i);
                //se extrae todo el texto de la pagina actual
                String textoPagina = stripper.getText(documento);
                //se crea un arreglo de lineas a partir de la pagina actual, el r es para procesar los saltos de linea estilo Windows
                String[] lineas = textoPagina.split("\\r?\\n");
//se recorre cada linea del arreglo para enviarselo al siguiente filtro
                for (String linea : lineas) {
                    linea = linea.trim();
                    if (!linea.isEmpty()) { //mientras no se encuentre vacia, la envia al siguiente
                        //concatenar linea y indice
                        String mensaje = linea + ":" + i;
                        tuberiaSalida.put(mensaje);
                    }
                }
            }
            // Señal de fin de archivo para el siguiente filtro+
            //cuando el filtro siguiente la reciba significara que ya proceso toda una pagina
            //necesario para buscar frases en una misma pagina
            tuberiaSalida.put("EOF");

        } catch (IOException e) {
            System.err.println("Error al leer el PDF: " + e.getMessage());
            tuberiaSalida.put("EOF");
        }
    }
}