package use_case.marker;

import entities.Fire;

public class MarkerInputData {
    private final Fire fire;

    public MarkerInputData(Fire fire) {
        this.fire = fire;
    }

    public Fire getFire() {
        return fire;
    }

}
