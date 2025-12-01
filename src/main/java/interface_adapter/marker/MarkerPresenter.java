package interface_adapter.marker;

import use_case.marker.MarkerOutputBoundary;
import use_case.marker.MarkerOutputData;

public class MarkerPresenter implements MarkerOutputBoundary {
    private final MarkerViewModel markerViewModel;
    public MarkerPresenter(MarkerViewModel markerViewModel) {
        this.markerViewModel = markerViewModel;
    }

    @Override
    public void prepareSuccessView(MarkerOutputData markerOutputData) {
        this.markerViewModel.setLat(markerOutputData.getLat());
        this.markerViewModel.setLon(markerOutputData.getLon());
        this.markerViewModel.setSize(markerOutputData.getSize());
        this.markerViewModel.setFrp(Math.round(markerOutputData.getFrp() * 10000.0) / 10000.0);
        this.markerViewModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        this.markerViewModel.setError(errorMessage);
        this.markerViewModel.firePropertyChanged();
    }
}
