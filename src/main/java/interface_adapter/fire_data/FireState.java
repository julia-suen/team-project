package interface_adapter.fire_data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import entities.Fire;

/**
 * The state for the Fire ViewModel.
 * Stores the list of fires, graph data, and any error messages.
 */
public class FireState {
    private List<Fire> loadedFires = new ArrayList<>();
    private List<Fire> displayedFires = new ArrayList<>();
    private Map<String, Integer> graphData = new LinkedHashMap<>();
    private String error;

    /**
     * Copy constructor.
     * @param copy the state to copy from
     */
    public FireState(FireState copy) {
        this.loadedFires = copy.loadedFires;
        this.displayedFires = copy.displayedFires;
        this.graphData = copy.graphData;
        this.error = copy.error;
    }

    /**
     * Default constructor.
     */
    public FireState() {
    }

    public List<Fire> getLoadedFires() {
        return loadedFires;
    }

    public void setLoadedFires(List<Fire> fires) {
        this.loadedFires = fires;
    }

    public List<Fire> getDisplayedFires() {
        return displayedFires;
    }

    public void setDisplayedFires(List<Fire> fires) {
        this.displayedFires = fires;
    }

    public Map<String, Integer> getGraphData() {
        return graphData;
    }

    public void setGraphData(Map<String, Integer> graphData) {
        this.graphData = graphData;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
