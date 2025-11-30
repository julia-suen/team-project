package interface_adapter.marker;

import entities.Fire;
import use_case.marker.MarkerInputBoundary;
import use_case.marker.MarkerInputData;

public class MarkerController {
    private final MarkerInputBoundary markerUseCaseInteractor;

    public MarkerController(MarkerInputBoundary loginUseCaseInteractor) {
        this.markerUseCaseInteractor = loginUseCaseInteractor;
    }


    public void execute(Fire fire) {
        final MarkerInputData loginInputData = new MarkerInputData();

        markerUseCaseInteractor.execute(loginInputData);
    }
}
