import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

//filtro diseñado para juntar las palabras repetidas en diferentes páginas en una sola linea
public class FiltroSalidaUnificador extends Filtro {
    //variables para mostrar iteración
    int i=0;
    public FiltroSalidaUnificador(BlockingQueue<String> entrada) {
        super(entrada,null);
    }

    @Override
    protected void procesar() throws InterruptedException {
            //mapa que agrupa las palabras, la clave será la palabra ye valor el indice/indices
            LinkedHashMap<String, String> salida = new LinkedHashMap<>();
            //mientras no llegue el fin de la linea, no se imprimirá el resultado final ni se escribirá en el txt
            while (true) {
                String entrada = tuberiaEntrada.take();
               // System.err.println("Entrada recibida: "+entrada);
                if (entrada.equals("EOF")) {
                    break;
                }

                // Limpiar el mapa y reprocesar con el bloque más reciente
                //esto debido a que el afabetizer envia repetidamente las mismas palabras y puede causar intentar meter una palabra-indice ya existente
                salida.clear();
//se separan las lineas que llegan en un arreglo
                String[] lineas = entrada.split("\n");
                //por cada linea en el arreglo de lineas
                for (String linea : lineas) {
                    //se utiliza .trim para eliminar posibles espacios en blanco
                    linea = linea.trim();
                    if (linea.isEmpty()) continue; //si resulta que esta vacia, entonces se brinca

                    parsearEntradaYActualizar(salida,linea);
                }

               // mostrarIteraciones(salida);
                
            }

escribirArchivoSalida(salida, "salida.txt");

    }


    //escribir en archivo de salida
    private void escribirArchivoSalida(Map<String,String> mapa,String direccionSalida){
                    //se recore el mapa de salida
            //se convierte al mapa en un conjunto de parejas clave-valor, donde cada conjunto es un Map.Entry
                try (BufferedWriter escritor = new BufferedWriter(new FileWriter(direccionSalida))) {
                for (Map.Entry<String, String> entry : mapa.entrySet()) {
                //se va generando cada linea que concatena la palabra y sus indices
                String linea = entry.getKey() + " : " + entry.getValue();
                //se imprime la linea que se va generando y se guarda en el txt
                System.out.println(linea);
                escritor.write(linea + "\n");
            }
    }
    catch(IOException e){
        throw new RuntimeException(e);
    }
}

    private void parsearEntradaYActualizar(Map<String,String> mapa,String linea){
                            String[] contenido = linea.split(" : ");
                    //se separa el contenido de la linea y se asigna a palabra e indice
                    if (contenido.length == 2) {
                        String palabra = contenido[0].trim();
                        String indice = contenido[1].trim();
//actualizar mapa
ActualizarMapa(mapa,palabra,indice);
                    }
    }


    private void ActualizarMapa(Map<String, String> mapa, String palabra, String indice){
        //si el mapa actual contiene ya la clave (palabra)
                                if (mapa.containsKey(palabra)) {
                                    //si contiene la palabra pero no el indice
                                    if (!mapa.get(palabra).contains(indice)) {
                                        //se convierten los indices actuales (los values) en una lista
                                        List<String> ListaIndices = new ArrayList<>(Arrays.asList(mapa.get(palabra).split(", ")));
                                        //el indice entrante se convierte en un integer
                                        int nuevoIndice = Integer.parseInt(indice);
        
                                       //Se busca la posicion donde será insertado
                                        int pos = 0;
                                        //este sera incrementado mientras el indice de la lista actual sea menor al indice entrante
                                        while (pos < ListaIndices.size() && Integer.parseInt(ListaIndices.get(pos)) < nuevoIndice) {
                                            pos++;
                                        }
        
                                        //a la lista indices se le agregara en la pos el indice entrante
                                        ListaIndices.add(pos, indice);
                                        //se manda al mapa, en la clave n, una lista de strings separados por " ,"
                                        mapa.put(palabra, String.join(", ", ListaIndices));
                                    }
                                } else {
                                    mapa.put(palabra, indice);
                                }
    }

//metodo para mostrar iteraciones
    public void mostrarIteraciones(Map<String,String> salida){
                        i++;
                        System.out.println("Iteración: "+i);
                        //opcional para ver como se van organizando
                        for (Map.Entry<String, String> entry : salida.entrySet()) {
                            //se va generando cada linea que concatena la palabra y sus indices
                            String linea = entry.getKey() + " : " + entry.getValue();
                            //se imprime la linea que se va generando y se guarda en el txt
                            System.out.println(linea);
                        }
    }
}
