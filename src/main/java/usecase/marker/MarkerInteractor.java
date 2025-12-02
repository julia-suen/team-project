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
            if(foundFire != null){
                final double lat = foundFire.getLat();
                final double lon = foundFire.getLon();
                final int size = foundFire.getCoordinatesSize();
                final double frp = foundFire.getFrp();
                final MarkerOutputData markerOutputData = new MarkerOutputData(lat, lon, size, frp);
                markerPresenter.prepareSuccessView(markerOutputData);
            }
        }catch(Exception e){
            markerPresenter.prepareFailView("Unexpected error: " + e.getMessage());
        }
    }

    // Helper method for finding the fire (must be added to MarkerInteractor)
    private Fire findFireAtGeoPosition(List<Fire> fires, double mouseLat, double mouseLon) {
        for (Fire fire : fires) {
            double fireLat = fire.getLat();
            double fireLon = fire.getLon();

            if (fireLat == mouseLat && fireLon == mouseLon) {
                return fire;
            }
        }
        return null;
    }
}
