package usecase.load_fires;

import entities.MultiRegionFireStats;
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
    /**
     * Fetch data for multi-region data analysis use case.
     * If unsuccessful, return an empty MultiRegionFireStats object.
     * @param fireInputData the input data
     * @return {@link MultiRegionFireStats} used for multi-regional analysis
     */
    MultiRegionFireStats fetchStats(LoadFiresInputData fireInputData) ;
}
