package interface_adapter.fire_data;

import javax.swing.SwingWorker;

import fireapi.GetData;
import use_case.fire_data.FireInputBoundary;
import use_case.fire_data.FireInputData;

/**
 * Controller for the fire analysis use case.
 */
public class FireController {
    private final FireInputBoundary fireInteractor;

    /**
     * Constructs a FireController.
     * @param fireInteractor the interactor to use
     */
    public FireController(FireInputBoundary fireInteractor) {
        this.fireInteractor = fireInteractor;
    }

    /**
     * Executes the Wildfire analysis use case.
     * @param date the given date
     * @param dateRange the day range requested
     * @param isNational whether to fetch national overview data
     */
    public void execute(String date, int dateRange, boolean isNational) {
        // Run on background thread to prevent UI freezing during multiple API calls
        final SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                final FireInputData inputData = new FireInputData(date, dateRange, isNational);
                try {
                    fireInteractor.execute(inputData);
                }
                catch (GetData.InvalidDataException ex) {
                    ex.printStackTrace();
                }
                return null;
            }
        };
        worker.execute();
    }
}
