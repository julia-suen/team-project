package usecase.severity_filter;

/**
 * The fire data analysis input interface which specifies the general format expected for user input data.
 */

public interface SeverityInputBoundary {
    /**
     * Execute the fire data analysis use case.
     * @param severityInputData the input data for this use case
     */

    void execute(SeverityInputData severityInputData);

}
