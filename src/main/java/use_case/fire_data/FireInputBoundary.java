package use_case.fire_data;

import java.io.ObjectInputStream.GetField;

import data_access.GetFireData;
import entities.MultiRegionFireStats;

/**
 * The fire data analysis input interface which specifies the general format expected for user input data.
 */

public interface FireInputBoundary {
    /**
     * Execute the fire data analysis use case.
     * @param fireInputData the input data for this use case
     * @throws GetFireData.InvalidDataException if an error is encountered during data parsing
     */

    void execute(FireInputData fireInputData) throws GetFireData.InvalidDataException;

    /**
     * Fetch data for multi-region data analysis use case.
     * If unsuccessful, return an empty MultiRegionFireStats object.
     * @param fireInputData
     * @return {@link MultiRegionFireStats} used for multi-regional analysis
     * @throws GetFireData.InvalidDataException
     */
    MultiRegionFireStats fetchStats(FireInputData fireInputData);
}