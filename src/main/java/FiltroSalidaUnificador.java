import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;
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
  LinkedHashMap<String, TreeSet<Integer>> salida = new LinkedHashMap<>();
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
    private void escribirArchivoSalida(Map<String, TreeSet<Integer>> mapa,String direccionSalida){
                    //se recore el mapa de salida
            //se convierte al mapa en un conjunto de parejas clave-valor, donde cada conjunto es un Map.Entry
                try (BufferedWriter escritor = new BufferedWriter(new FileWriter(direccionSalida))) {
                for (Map.Entry<String, TreeSet<Integer>> entry : mapa.entrySet()) {
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

    private void parsearEntradaYActualizar(Map<String, TreeSet<Integer>> mapa,String linea){
                            String[] contenido = linea.split(" : ");
                    //se separa el contenido de la linea y se asigna a palabra e indice
                    if (contenido.length == 2) {
                        String palabra = contenido[0].trim();
                        Integer indice = Integer.parseInt(contenido[1].trim());
//actualizar mapa
ActualizarMapa(mapa,palabra,indice);
                    }
    }


    private void ActualizarMapa(Map<String, TreeSet<Integer>> mapa, String palabra, Integer indice){
        //si el mapa actual contiene ya la clave (palabra)
                                if (mapa.containsKey(palabra)) {
//al manejar set si se trata de volver a agregar el indice no pasara nada
mapa.get(palabra).add(indice);
                                    }

                                 else {
                                    TreeSet<Integer> nuevoSet= new TreeSet<>();
                                    nuevoSet.add(indice);
                                    mapa.put(palabra, nuevoSet);
                                }
    }

//metodo para mostrar iteraciones
    public void mostrarIteraciones(Map<String, TreeSet<Integer>> salida){
                        i++;
                        System.out.println("Iteración: "+i);
                        //opcional para ver como se van organizando
                        for (Map.Entry<String, TreeSet<Integer>>  entry : salida.entrySet()) {
                            //se va generando cada linea que concatena la palabra y sus indices
                            String linea = entry.getKey() + " : " + entry.getValue();
                            //se imprime la linea que se va generando y se guarda en el txt
                            System.out.println(linea);
                        }
    }
}
