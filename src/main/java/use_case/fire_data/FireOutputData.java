package use_case.fire_data;

import java.util.List;
import java.util.Map;

import entities.Fire;

/**
 * The output data for the fire analytics use case.
 * Represents the final information that will be sent back out to the view.
 */
public class FireOutputData {
    private final List<Fire> fires;
    /**
     * Map of Time Label (e.g., "AUG") to Number of Fires.
     */
    private final Map<String, Integer> fireTrendData;

    /**
     * Constructs a new FireOutputData.
     * @param fires the list of fires to display on the map
     * @param fireTrendData the trend data for the graph (Label -> Count)
     */
    public FireOutputData(List<Fire> fires, Map<String, Integer> fireTrendData) {
        this.fires = fires;
        this.fireTrendData = fireTrendData;
    }

    public List<Fire> getFires() {
        return fires;
    }

    public Map<String, Integer> getFireTrendData() {
        return fireTrendData;
    }
}
