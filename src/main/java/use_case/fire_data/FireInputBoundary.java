package use_case.fire_data;

import data_access.GetData;

/**
 * The fire data analysis input interface which specifies the general format expected for user input data.
 */

public interface FireInputBoundary {
    /**
     * Execute the fire data analysis use case.
     * @param fireInputData the input data for this use case
     * @throws GetData.InvalidDataException if an error is encountered during data parsing
     */

    void execute(FireInputData fireInputData) throws GetData.InvalidDataException;

}
