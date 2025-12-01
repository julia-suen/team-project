package entities;

import java.util.List;
import java.util.Map;

import kotlin.Pair;

public class MultiRegionFireStats {
    private final Map<String, List<Pair<String, Integer>>> stats;

    public MultiRegionFireStats(Map<String, List<Pair<String, Integer>>> stats) {
        this.stats = stats;
    }

    public Map<String, List<Pair<String, Integer>>> getData() {
        return stats;
    }

    public void put(String province, List<Pair<String, Integer>> pointsByMonth) {
        stats.put(province, pointsByMonth);
    }
}