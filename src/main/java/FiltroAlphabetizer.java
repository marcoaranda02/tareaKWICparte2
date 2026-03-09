
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
        for (int i = 0; i < numLineas; i++) {
            int pos = 0;
            while (pos < indicesOrdenados.size()
                    && lineas.get(i).toLowerCase().compareTo(lineas.get(indicesOrdenados.get(pos)).toLowerCase()) > 0) {
                pos++;
            }
            indicesOrdenados.add(pos, i);
        }
        StringBuilder lineasO = new StringBuilder();
        for (Integer indice : indicesOrdenados) {
            lineasO.append(lineas.get(indice)).append("\n");
        }
        tuberiaSalida.put(lineasO.toString());
    }
}

