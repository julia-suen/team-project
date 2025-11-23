package use_case.fire_data;

import entities.Fire;

import java.util.List;


/**
 * The output data for the fire analytics use case
 * think of the final information that will be sent back out
 */
public class FireOutputData {
    private final List<Fire> fires;

    public FireOutputData(List<Fire> fires) {
        this.fires = fires;
    }

    public List<Fire> getFires() {
        return fires;
    }

    public int getnumFires() {
        return getFires().size();
    }


}
