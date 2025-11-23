package interface_adapter.fire_data;

import entities.Fire;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class FireState {
    private List<Fire> fires = new ArrayList<>();
    private Map<Integer, Integer> graphData = new TreeMap<>();
    private String error = null;

    public FireState(FireState copy) {
        this.fires = copy.fires;
        this.graphData = copy.graphData;
        this.error = copy.error;
    }

    public FireState() {
    }

    public List<Fire> getFires() {
        return fires;
    }

    public void setFires(List<Fire> fires) {
        this.fires = fires;
    }

    public Map<Integer, Integer> getGraphData() {
        return graphData;
    }

    public void setGraphData(Map<Integer, Integer> graphData) {
        this.graphData = graphData;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}