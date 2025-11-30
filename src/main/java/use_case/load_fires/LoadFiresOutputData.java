package use_case.load_fires;

import entities.Fire;
import java.util.List;
import java.util.Map;

public class LoadFiresOutputData {
    private final List<Fire> fires;
    private final Map<String, Integer> fireTrendData;

    public LoadFiresOutputData(List<Fire> fires, Map<String, Integer> fireTrendData) {
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
