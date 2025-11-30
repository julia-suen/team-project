package interface_adapter.severity_filter;

import interface_adapter.fire_data.FireState;
import interface_adapter.fire_data.FireViewModel;
import use_case.severity_filter.SeverityOutputBoundary;
import use_case.severity_filter.SeverityOutputData;

/**
 * The presenter for the fire severity use case, updates displayed fires.
 */

public class SeverityPresenter implements SeverityOutputBoundary {

    private final FireViewModel fireViewModel;

    public SeverityPresenter(FireViewModel fireViewModel) {
        this.fireViewModel = fireViewModel;
    }

    @Override
    public void prepareSuccessView(SeverityOutputData outputData) {
        final FireState state = fireViewModel.getState();
        state.setDisplayedFires(outputData.filteredFires());

        state.setError(null);
        fireViewModel.setState(state);
        fireViewModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String error) {
        final FireState state = fireViewModel.getState();
        state.setError(error);
        fireViewModel.firePropertyChange();
    }
}
