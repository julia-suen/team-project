package usecase.compare;

import entities.Fire;
import entities.MultiRegionFireStats;
import java.util.List;

public class CompareOutputData {
    private final MultiRegionFireStats stats;
    private final List<Fire> fires;

    public CompareOutputData(MultiRegionFireStats stats, List<Fire> fires) {
        this.stats = stats;
        this.fires = fires;
    }

    public MultiRegionFireStats getStats() {
        return stats;
    }

    public List<Fire> getFires() {
        return fires;
    }
}
