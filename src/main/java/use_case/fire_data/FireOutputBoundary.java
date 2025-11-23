package use_case.fire_data;

/**
 * The output boundary for the fire analytics use case
 * think of as a request that a user makes to the program, giving it a date and a day range
 */
public interface FireOutputBoundary {

    /**
     * Prepares the success view for the fire analytics use case
     * @param outputData the output data
     */
    void prepareSuccessView(FireOutputData outputData);

    /**
     * Prepares the failure view for the fire analytics use case
     * @param errorMessage the explanation of the failure
     */
    void prepareFailView(String errorMessage);

}
