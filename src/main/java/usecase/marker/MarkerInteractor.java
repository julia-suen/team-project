package usecase.marker;

import entities.Fire;

import java.util.List;

/**
 * Interactor for the Marker Use Case.
 * Find the fire from the list of fire that are currently displaying
 */

public class MarkerInteractor implements MarkerInputBoundary {
    private final MarkerOutputBoundary markerPresenter;
    private final FireDisplayStateReader fireDisplayStateReader;

    /**
     * Constructs a MarkerInteractor.
     * @param markerOutputBoundary the presenter
     * @param fireDisplayStateReader the fireViewModel
     */
    public MarkerInteractor(MarkerOutputBoundary markerOutputBoundary, FireDisplayStateReader fireDisplayStateReader) {
        this.markerPresenter = markerOutputBoundary;
        this.fireDisplayStateReader = fireDisplayStateReader;
    }

    @Override
    public void execute(MarkerInputData markerInputData) {
        try {
            final double mouseLat = markerInputData.getLat();
            final double mouseLon = markerInputData.getLon();
            final List<Fire> currentFires = fireDisplayStateReader.getDisplayedFires();
            Fire foundFire = findFireAtCoord(currentFires, mouseLat, mouseLon);
            if(foundFire != null){
                final double lat = foundFire.getLat();
                final double lon = foundFire.getLon();
                final int size = foundFire.getCoordinatesSize();
                final double frp = foundFire.getFrp();
                final MarkerOutputData markerOutputData = new MarkerOutputData(lat, lon, size, frp);
                markerPresenter.prepareSuccessView(markerOutputData);
            }else{
                markerPresenter.prepareFailView("No fires are found");
            }
        }catch(Exception e){
            markerPresenter.prepareFailView("Unexpected error: " + e.getMessage());
        }
    }

    // Helper method for finding the fire with a coordinate
    private Fire findFireAtCoord(List<Fire> fires, double mouseLat, double mouseLon) {
        for (Fire fire : fires) {
            final double TOLERANCE = 1e-10;
            double fireLat = fire.getLat();
            double fireLon = fire.getLon();

            if (Math.abs(fireLat - mouseLat) < TOLERANCE && Math.abs(fireLon - mouseLon) < TOLERANCE) {
                return fire;
            }
        }
        return null;
    }
}
