package interface_adapter.marker;

import interface_adapter.ViewManagerModel;
import use_case.marker.MarkerOutputBoundary;

public class MarkerPresenter implements MarkerOutputBoundary {
    private MarkerViewModel markerViewModel;
    public MarkerPresenter(MarkerViewModel markerViewModel) {
        this.markerViewModel = markerViewModel;
    }
}
