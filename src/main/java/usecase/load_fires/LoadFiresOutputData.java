package usecase.load_fires;

import entities.Fire;
import java.util.List;
import java.util.Map;

/**
 * Data structure representing the output of the "Load Fires" use case.
 */
public class LoadFiresOutputData {
    private final List<Fire> fires;
    private final Map<String, Integer> fireTrendData;

    /**
     * Constructs a LoadFiresOutputData object.
     * @param fires         the list of fires to display on the map
     * @param fireTrendData the trend data mapping labels to fire counts
     */
    public LoadFiresOutputData(List<Fire> fires, Map<String, Integer> fireTrendData) {
        this.fires = fires;
        this.fireTrendData = fireTrendData;
    }

    /**
     * Gets the list of fires.
     * @return the list of Fire entities
     */
    public List<Fire> getFires() {
        return fires;
    }

    /**
     * Gets the trend data.
     * @return a map of trend labels to counts
     */
    public Map<String, Integer> getFireTrendData() {
        return fireTrendData;
    }
}
