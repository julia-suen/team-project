package usecase.national_overview;

import entities.Fire;
import java.util.List;
import java.util.Map;

/**
 * Data structure representing the output of the "National Overview" use case.
 */
public class NationalOverviewOutputData {
    private final List<Fire> fires;
    private final Map<String, Integer> fireTrendData;

    /**
     * Constructs a NationalOverviewOutputData object.
     * @param fires         the aggregated list of fires across the 3-month period
     * @param fireTrendData the trend map (Month -> Count)
     */
    public NationalOverviewOutputData(List<Fire> fires, Map<String, Integer> fireTrendData) {
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
