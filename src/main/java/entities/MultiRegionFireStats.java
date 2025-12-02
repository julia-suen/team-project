package entities;

import kotlin.Pair;
import java.util.List;
import java.util.Map;

/**
 * Entity class representing fire statistics for multiple regions.
 * Each region has a list of date-count pairs representing daily fire counts.
 */
public class MultiRegionFireStats {
    private final Map<String, List<Pair<String, Integer>>> data;

    /**
     * Constructs a MultiRegionFireStats object.
     *
     * @param data A map where keys are region names and values are lists of date-count pairs.
     */
    public MultiRegionFireStats(Map<String, List<Pair<String, Integer>>> data) {
        this.data = data;
    }

    /**
     * Retrieves the statistics data.
     *
     * @return A map where keys are region names and values are lists of date-count pairs.
     */
    public Map<String, List<Pair<String, Integer>>> getData() {
        return data;
    }
}

