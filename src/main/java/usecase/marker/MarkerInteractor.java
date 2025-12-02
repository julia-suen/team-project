package usecase.marker;


import entities.Fire;

public class MarkerInteractor implements MarkerInputBoundary {
    private final MarkerOutputBoundary markerPresenter;

    public MarkerInteractor(MarkerOutputBoundary markerOutputBoundary) {
        this.markerPresenter = markerOutputBoundary;
    }

    @Override
    public void execute(MarkerInputData markerInputData) {
        try{
            final Fire fire = markerInputData.getFire();
            final double lat = fire.getLat();
            final double lon = fire.getLon();
            final int size = fire.getCoordinatesSize();
            final double frp = fire.getFrp();
            final MarkerOutputData markerOutputData = new MarkerOutputData(lat, lon, size, frp);
            markerPresenter.prepareSuccessView(markerOutputData);
        }catch(Exception e){
            markerPresenter.prepareFailView("Unexpected error: " + e.getMessage());
        }


    }
}
