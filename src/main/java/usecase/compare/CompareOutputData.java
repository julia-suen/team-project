package usecase.compare;

import entities.MultiRegionFireStats;

public class CompareOutputData {
    private final MultiRegionFireStats stats;

    public CompareOutputData(MultiRegionFireStats stats) {
        this.stats = stats;
    }

    public MultiRegionFireStats getStats() {
        return stats;
    }
}
