package interface_adapter.marker;

import entities.Fire;
import usecase.marker.MarkerInputBoundary;
import usecase.marker.MarkerInputData;

public class MarkerController {
    private final MarkerInputBoundary markerUseCaseInteractor;

    public MarkerController(MarkerInputBoundary loginUseCaseInteractor) {
        this.markerUseCaseInteractor = loginUseCaseInteractor;
    }


    public void execute(Fire fire) {
        final MarkerInputData loginInputData = new MarkerInputData(fire);
        markerUseCaseInteractor.execute(loginInputData);
    }
}
