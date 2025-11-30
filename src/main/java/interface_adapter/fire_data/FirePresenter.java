package interface_adapter.fire_data;

import interface_adapter.ViewManagerModel;
import use_case.load_fires.LoadFiresOutputBoundary;
import use_case.load_fires.LoadFiresOutputData;
import use_case.national_overview.NationalOverviewOutputBoundary;
import use_case.national_overview.NationalOverviewOutputData;

/**
 * The presenter for fire analysis use cases.
 * Handles output from both LoadFires and NationalOverview.
 */
public class FirePresenter implements LoadFiresOutputBoundary, NationalOverviewOutputBoundary {

    private final FireViewModel fireViewModel;
    private final ViewManagerModel viewManagerModel;

    public FirePresenter(FireViewModel fireViewModel, ViewManagerModel viewManagerModel) {
        this.fireViewModel = fireViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    // --- Load Fires Output ---
    @Override
    public void prepareSuccessView(LoadFiresOutputData response) {
        updateState(response.getFires(), response.getFireTrendData());
    }

    // --- National Overview Output ---
    @Override
    public void prepareSuccessView(NationalOverviewOutputData response) {
        updateState(response.getFires(), response.getFireTrendData());
    }

    // --- Shared Fail View ---
    @Override
    public void prepareFailView(String error) {
        final FireState state = fireViewModel.getState();
        state.setError(error);
        fireViewModel.firePropertyChange();
    }

    private void updateState(java.util.List<entities.Fire> fires, java.util.Map<String, Integer> trendData) {
        final FireState state = fireViewModel.getState();
        state.setLoadedFires(fires);
        state.setDisplayedFires(fires);
        state.setGraphData(trendData);
        state.setError(null);

        fireViewModel.setState(state);
        fireViewModel.firePropertyChange();
    }
}
