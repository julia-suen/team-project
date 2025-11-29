package use_case.severity_filter;

import java.util.List;

import entities.Fire;

/**
 * The output data for the fire filtering use case.
 * Represents the final information that will be sent back out to the view.
 */

public class SeverityOutputData {
    private final List<Fire> filteredFires;

    public SeverityOutputData(List<Fire> filteredFires) {
        this.filteredFires = filteredFires;
    }

    public List<Fire> getFilteredFires() {
        return filteredFires;
    }
}
