package use_case.fire_data;

import entities.Fire;


/**
 * The output data for the fire analytics use case
 * think of the final information that will be sent back out
 */
public class FireOutputData {
    private final Fire[] fires;

    public FireOutputData(Fire[] fires) {
        this.fires = fires;
    }

    public Fire[] getFires() {
        return fires;
    }

    public int getnumFires() {
        return getFires().length;
    }


}
