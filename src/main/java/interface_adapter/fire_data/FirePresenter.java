package interface_adapter.fire_data;

import interface_adapter.ViewManagerModel;
import use_case.fire_data.FireOutputBoundary;
import use_case.fire_data.FireOutputData;

/**
 * The presenter for the fire analysis use case.
 */

public class FirePresenter implements FireOutputBoundary {

    private final FireViewModel fireViewModel;
    private final ViewManagerModel viewManagerModel;

    public FirePresenter(FireViewModel fireViewModel, ViewManagerModel viewManagerModel) {
        this.fireViewModel = fireViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void prepareSuccessView(FireOutputData response) {
        final FireState state = fireViewModel.getState();

        state.setLoadedFires(response.getFires());
        state.setDisplayedFires(response.getFires());
        state.setGraphData(response.getFireTrendData());
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
