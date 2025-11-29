package use_case.severity_filter;

import java.util.List;

import entities.Fire;
import entities.SeverityFilter;

/**
 * The input data for the filtering by severity use case.
 * Represents a request that a user makes to the program by applying a filter to the fires loaded.
 */

public class SeverityInputData {
    private final List<Fire> currentFires;
    private final SeverityFilter severityFilter;

    public SeverityInputData(List<Fire> currentFires, SeverityFilter severityFilter) {
        this.currentFires = currentFires;
        this.severityFilter = severityFilter;
    }

    public List<Fire> getCurrentFires() {
        return currentFires;
    }

    public SeverityFilter getSeverityFilter() {
        return severityFilter;
    }
}
