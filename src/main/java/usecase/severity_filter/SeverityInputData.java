package usecase.severity_filter;

import java.util.List;

import entities.Fire;
import entities.SeverityFilter;

/**
 * The input data for the filtering by severity use case.
 * Represents a request that a user makes to the program by applying a filter to the fires loaded.
 */

public record SeverityInputData(List<Fire> currentFires, SeverityFilter severityFilter) {
}
