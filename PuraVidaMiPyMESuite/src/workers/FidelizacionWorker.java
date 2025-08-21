/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package workers;
import application.AnalisisService;
import javax.swing.SwingWorker;
/**
 *
 * @author Emesis
 */
public class FidelizacionWorker extends SwingWorker<Void, Integer> {
    private final AnalisisService.ProgresoListener listener;

    public FidelizacionWorker(AnalisisService.ProgresoListener listener) {
        this.listener = listener;
    }

    @Override
    protected Void doInBackground() throws Exception {
        int total = 100; // Simulación: 100 pasos
        for (int i = 0; i <= total; i++) {
            if (isCancelled()) {
                return null;
            }
            Thread.sleep(80); // Simula trabajo pesado
            int progreso = i;
            publish(progreso);
        }
        return null;
    }

    @Override
    protected void process(java.util.List<Integer> chunks) {
        int progreso = chunks.get(chunks.size() - 1);
        listener.onProgreso(progreso);
    }

    @Override
    protected void done() {
        try {
            get(); // Espera a que termine
            if (!isCancelled()) {
                listener.onCompletado();
            }
        } catch (Exception e) {
            if (isCancelled()) {
                listener.onCancelado();
            }
        }
    }
}
