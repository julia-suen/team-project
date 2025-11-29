package interface_adapter.severity_filter;

import java.util.List;

import entities.Fire;
import entities.SeverityFilter;
import interface_adapter.fire_data.FireViewModel;
import use_case.severity_filter.SeverityInputBoundary;
import use_case.severity_filter.SeverityInputData;

/**
 * Controller for the severity filter use case, gets current fires from FireViewModel state.
 */

public class SeverityController {
    private final SeverityInputBoundary severityInteractor;
    private final FireViewModel viewModel;

    public SeverityController(SeverityInputBoundary severityInteractor, FireViewModel viewModel) {
        this.severityInteractor = severityInteractor;
        this.viewModel = viewModel;
    }

    /**
     * Execute the filter severity use case.
     * Gets current fires from the view model and applies the filter.
     *
     * @param filter the severity filter to apply
     */

    public void filterBySeverity(SeverityFilter filter) {

        final List<Fire> currentFires = viewModel.getState().getLoadedFires();
        final SeverityInputData inputData = new SeverityInputData(currentFires, filter);
        severityInteractor.execute(inputData);
    }
}
