package interface_adapter.marker;

import usecase.marker.MarkerOutputBoundary;
import usecase.marker.MarkerOutputData;

/**
 * The Presenter for the Marker Use Case.
 */
public class MarkerPresenter implements MarkerOutputBoundary {
    private final MarkerViewModel markerViewModel;

    /**
     * Constructs a SelectRegionPresenter.
     * @param markerViewModel The ViewModel to update.
     */
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
        this.markerViewModel.setError(null);
    }

    @Override
    public void prepareFailView(String errorMessage) {
        this.markerViewModel.setError(errorMessage);
        this.markerViewModel.firePropertyChanged();
    }
}
