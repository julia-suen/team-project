package interface_adapter.marker;

import interface_adapter.ViewManagerModel;
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
        this.markerViewModel.setDate(markerOutputData.getDate());
        this.markerViewModel.setSize(markerOutputData.getSize());
        this.markerViewModel.setFrp(markerOutputData.getFrp());
        this.markerViewModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        this.markerViewModel.setError(errorMessage);
        this.markerViewModel.firePropertyChanged();
    }
}
