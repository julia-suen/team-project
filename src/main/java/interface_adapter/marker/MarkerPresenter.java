package interface_adapter.marker;

import interface_adapter.ViewManagerModel;
import use_case.marker.MarkerOutputBoundary;
import use_case.marker.MarkerOutputData;

public class MarkerPresenter implements MarkerOutputBoundary {
    private MarkerViewModel markerViewModel;
    public MarkerPresenter(MarkerViewModel markerViewModel) {
        this.markerViewModel = markerViewModel;
    }

    @Override
    public void prepareSuccessView(MarkerOutputData outputData) {

    }

    @Override
    public void prepareFailView(String errorMessage) {

    }
}
