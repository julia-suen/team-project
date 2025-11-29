package use_case.severity_filter;

/**
 * The output boundary for the severity filtering use case.
 */

public interface SeverityOutputBoundary {
    /**
     * Prepares the success view for the fire analytics use case.
     * @param outputData the output data
     */
    void prepareSuccessView(SeverityOutputData outputData);

    /**
     * Prepares the failure view for the fire analytics use case.
     * @param errorMessage the explanation of the failure
     */
    void prepareFailView(String errorMessage);

}
