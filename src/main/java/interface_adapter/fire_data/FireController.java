package interface_adapter.fire_data;

import use_case.fire_data.FireInputBoundary;
import use_case.fire_data.FireInputData;

import javax.swing.SwingWorker;

/**
 * Controller for the fire analysis use case
 */
public class FireController {
    private final FireInputBoundary fireInteractor;

    public FireController(FireInputBoundary fireInteractor) {
        this.fireInteractor = fireInteractor;
    }

    public void execute(String date, int dateRange, boolean isNational) {
        // Run on background thread to prevent UI freezing during multiple API calls
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                FireInputData inputData = new FireInputData(date, dateRange, isNational);
                try {
                    fireInteractor.execute(inputData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        worker.execute();
    }
}