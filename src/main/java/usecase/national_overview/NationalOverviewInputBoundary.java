package usecase.national_overview;

/**
 * Input boundary for the "National Overview" use case.
 * Defines the contract for executing the national trend analysis.
 */
public interface NationalOverviewInputBoundary {

    /**
     * Executes the national overview analysis.
     * @param inputData the input data containing the reference date
     */
    void execute(NationalOverviewInputData inputData);
}
