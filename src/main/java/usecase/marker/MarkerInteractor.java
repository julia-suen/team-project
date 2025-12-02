package usecase.marker;


import entities.Fire;

import java.util.List;

public class MarkerInteractor implements MarkerInputBoundary {
    private final MarkerOutputBoundary markerPresenter;
    private final FireDisplayStateReader fireDisplayStateReader;

    public MarkerInteractor(MarkerOutputBoundary markerOutputBoundary, FireDisplayStateReader fireDisplayStateReader) {
        this.markerPresenter = markerOutputBoundary;
        this.fireDisplayStateReader = fireDisplayStateReader;
    }

    @Override
    public void execute(MarkerInputData markerInputData) {
        try{
            final double mouseLat = markerInputData.getLat();
            final double mouseLon = markerInputData.getLon();
            final List<Fire> currentFires = fireDisplayStateReader.getDisplayedFires();
            Fire foundFire = findFireAtGeoPosition(currentFires, mouseLat, mouseLon);
            final double lat = foundFire.getLat();
            final double lon = foundFire.getLon();
            final int size = foundFire.getCoordinatesSize();
            final double frp = foundFire.getFrp();
            final MarkerOutputData markerOutputData = new MarkerOutputData(lat, lon, size, frp);
            markerPresenter.prepareSuccessView(markerOutputData);
        }catch(Exception e){
            markerPresenter.prepareFailView("Unexpected error: " + e.getMessage());
        }
    }

    // Helper method for finding the fire (must be added to MarkerInteractor)
    private Fire findFireAtGeoPosition(List<Fire> fires, double lat, double lon) {
        // We use a proximity check, NOT exact coordinate matching (lat == fire.getLat()),
        // because of floating point inaccuracies and mouse location being approximate.
        final double MAX_DISTANCE_SQUARED = 0.0001;

        for (Fire fire : fires) {
            double fireLat = fire.getLat();
            double fireLon = fire.getLon();

            double distanceSq = Math.pow(fireLat - lat, 2) + Math.pow(fireLon - lon, 2);

            if (distanceSq < MAX_DISTANCE_SQUARED) {
                return fire;
            }
        }
        return null;
    }
}
