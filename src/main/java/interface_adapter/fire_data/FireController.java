package interface_adapter.fire_data;

import javax.swing.SwingWorker;

import use_case.load_fires.LoadFiresInputBoundary;
import use_case.load_fires.LoadFiresInputData;
import use_case.national_overview.NationalOverviewInputBoundary;
import use_case.national_overview.NationalOverviewInputData;

/**
 * Controller for fire analysis use cases.
 * Directs input to either the LoadFires or NationalOverview interactor.
 */
public class FireController {
    private final LoadFiresInputBoundary loadFiresInteractor;
    private final NationalOverviewInputBoundary nationalOverviewInteractor;

    public FireController(LoadFiresInputBoundary loadFiresInteractor,
                          NationalOverviewInputBoundary nationalOverviewInteractor) {
        this.loadFiresInteractor = loadFiresInteractor;
        this.nationalOverviewInteractor = nationalOverviewInteractor;
    }

    /**
     * Executes the Wildfire analysis use case.
     * @param province   the province chosen
     * @param date       the given date
     * @param dateRange  the day range requested
     * @param isNational whether to fetch national overview data
     */
    public void execute(String province, String date, int dateRange, boolean isNational) {
        // Run on background thread
        final SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                if (isNational) {
                    final NationalOverviewInputData inputData = new NationalOverviewInputData(date, dateRange);
                    nationalOverviewInteractor.execute(inputData);
                }
                else {
                    final LoadFiresInputData inputData = new LoadFiresInputData(province, date, dateRange);
                    loadFiresInteractor.execute(inputData);
                }
                return null;
            }
        };
        worker.execute();
    }
}
