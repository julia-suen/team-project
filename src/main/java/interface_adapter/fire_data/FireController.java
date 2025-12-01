package interface_adapter.fire_data;

import java.util.List;

import javax.swing.SwingWorker;

import data_access.GetFireData;
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
     *
     * @param province       the province chosen
     * @param date           the given date
     * @param dateRange      the day range requested
     * @param isNational     whether to fetch national overview data
     */

    public void execute(String province, String date, int dateRange, boolean isNational) {
        // Run on background thread to prevent UI freezing during multiple API calls
        final SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                final FireInputData inputData = new FireInputData(List.of(province), date, dateRange, isNational);
                try {
                    fireInteractor.execute(inputData);
                }
                catch (GetFireData.InvalidDataException ex) {
                    ex.printStackTrace();
                }
                return null;
            }
        };
        worker.execute();
    }
}