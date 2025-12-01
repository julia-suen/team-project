package usecase.severity_filter;

import java.util.List;

import entities.Fire;

/**
 * The output data for the fire filtering use case.
 * Represents the final information that will be sent back out to the view.
 */

public record SeverityOutputData(List<Fire> filteredFires) {
}
