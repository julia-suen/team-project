package usecase.load_fires;

/**
 * Input boundary for the "Load Fires" use case.
 * Defines the contract for the interactor to execute the logic.
 */
public interface LoadFiresInputBoundary {

    /**
     * Executes the use case with the provided input data.
     * @param inputData the input data containing user selection (province, date, range)
     */
    void execute(LoadFiresInputData inputData);
}
