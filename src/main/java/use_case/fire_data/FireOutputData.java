package use_case.fire_data;

import entities.Fire;

import java.util.List;
import java.util.Map;

/**
 * The output data for the fire analytics use case
 * think of the final information that will be sent back out
 */
public class FireOutputData {
    private final List<Fire> fires;
    private final Map<Integer, Integer> fireTrendData; // Year -> Number of Fires

    public FireOutputData(List<Fire> fires, Map<Integer, Integer> fireTrendData) {
        this.fires = fires;
        this.fireTrendData = fireTrendData;
    }

    public List<Fire> getFires() {
        return fires;
    }

    public Map<Integer, Integer> getFireTrendData() {
        return fireTrendData;
    }
}