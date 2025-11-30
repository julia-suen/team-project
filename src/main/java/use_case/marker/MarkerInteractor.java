package use_case.marker;


public class MarkerInteractor implements MarkerInputBoundary {
    private final MarkerOutputBoundary markerOutputBoundary;

    public MarkerInteractor(MarkerOutputBoundary markerOutputBoundary) {
        this.markerOutputBoundary = markerOutputBoundary;
    }

    @Override
    public void execute(MarkerInputData markerInputData) {
        // to be edited
    }
}
