package usecase.marker;

/**
 * Input boundary for the Marker use case.
 * Defines the contract for executing the marker use case.
 */
public interface MarkerInputBoundary {

    /**
     * Executes the marker use case.
     * @param markerInputData the input data containing the hovered fireWaypoint's latitude and longitude.
     */
    void execute(MarkerInputData markerInputData);
}
