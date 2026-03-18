
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class FiltroAlphabetizer extends Filtro {

    private List<String> lineas;
    private List<Integer> indicesOrdenados;

    public FiltroAlphabetizer(BlockingQueue<String> entrada, BlockingQueue<String> salida) {
        super(entrada, salida);
        lineas = new ArrayList<>();
        indicesOrdenados = new ArrayList<>();
    }

    @Override
    protected void procesar() throws InterruptedException {
        while (true) {
            String linea = tuberiaEntrada.take();
            //System.out.println("Filtro A");
            if (linea.equals("EOF")) { // Señal de terminación que también inicia el ordenamiento alfabetico de las lineas
                tuberiaSalida.put("EOF");
                break;
            }
            lineas.add(linea);
            OrdenarLineas();
        }
    }

    private void OrdenarLineas() throws InterruptedException {
        indicesOrdenados.clear();
        int numLineas = lineas.size();
        //se recorre un ciclo for hasta que se recorran todas las lineas
        for (int i = 0; i < numLineas; i++) {
            int pos = 0;
// Busca el punto de inserción: recorre los índices ya ordenados 
        // hasta encontrar una palabra que sea alfabéticamente "mayor" que la actual
            while (pos < indicesOrdenados.size()
                    && lineas.get(i).toLowerCase().compareTo(lineas.get(indicesOrdenados.get(pos)).toLowerCase()) > 0) {
                        //suma la posición para compararlo con el que sigue
                pos++;
            }
           //la lista de indices es la que se actualiza
            indicesOrdenados.add(pos, i);
        }
        StringBuilder lineasO = new StringBuilder();
        //por ejemplo se tiene fresa,aranda,carlos
        //la lista de indices sería algo como [1,2,0]
        for (Integer indice : indicesOrdenados) {
            //se imprimiria el aranda,carlos,fresa 
            lineasO.append(lineas.get(indice)).append("\n");
        }

        tuberiaSalida.put(lineasO.toString());
    }
}

