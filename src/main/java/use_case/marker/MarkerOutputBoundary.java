package use_case.marker;

/**
 * The output boundary for the Login Use Case.
 */
public interface MarkerOutputBoundary {
    /**
     * Prepares the success view for the Marker Use Case.
     * @param outputData the output data
     */
    void prepareSuccessView(MarkerOutputData outputData);

    /**
     * Prepares the failure view for the Login Use Case.
     * @param errorMessage the explanation of the failure
     */
    void prepareFailView(String errorMessage);
}