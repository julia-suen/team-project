package usecase.national_overview;

/**
 * Output boundary for the "National Overview" use case.
 * Defines the methods required to present the trend analysis results.
 */
public interface NationalOverviewOutputBoundary {

    /**
     * Prepares the success view with the national fire data.
     * @param outputData the output data containing aggregated fires and trends
     */
    void prepareSuccessView(NationalOverviewOutputData outputData);

    /**
     * Prepares the failure view with an error message.
     * @param errorMessage the description of the error
     */
    void prepareFailView(String errorMessage);
}
