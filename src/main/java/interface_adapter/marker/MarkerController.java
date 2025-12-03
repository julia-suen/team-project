package interface_adapter.marker;

import usecase.marker.MarkerInputBoundary;
import usecase.marker.MarkerInputData;

/**
 * The controller for the Marker Use Case.
 */
public class MarkerController {
    private final MarkerInputBoundary markerUseCaseInteractor;

    public MarkerController(MarkerInputBoundary markerUseCaseInteractor) {
        this.markerUseCaseInteractor = markerUseCaseInteractor;
    }

    /**
     * Executes the Login Use Case.
     * @param lat the latitude of the hovered fireWaypoint
     * @param lon the longitude of the hovered fireWaypoint
     */
    public void execute(double lat, double lon) {
        final MarkerInputData markerInputData = new MarkerInputData(lat, lon);
        markerUseCaseInteractor.execute(markerInputData);
    }
}
