package interface_adapter.marker;

import usecase.marker.MarkerInputBoundary;
import usecase.marker.MarkerInputData;

public class MarkerController {
    private final MarkerInputBoundary markerUseCaseInteractor;

    public MarkerController(MarkerInputBoundary markerUseCaseInteractor) {
        this.markerUseCaseInteractor = markerUseCaseInteractor;
    }

    public void execute(double lat, double lon) {
        final MarkerInputData markerInputData = new MarkerInputData(lat, lon);
        markerUseCaseInteractor.execute(markerInputData);
    }
}
