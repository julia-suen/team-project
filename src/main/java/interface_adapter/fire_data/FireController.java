package interface_adapter.fire_data;

import fireapi.GetData;
import use_case.fire_data.FireInputBoundary;
import use_case.fire_data.FireInputData;
import use_case.fire_data.FireInteractor;

/**
 * Controller for the fire analysis use case
 */
public class FireController {
    private final FireInputBoundary fireInteractor;

    public FireController(FireInputBoundary fireInteractor) {
        this.fireInteractor = fireInteractor;
    }

    /**
     * Executes the Wildfire analysis use case.
     * @param date the given date
     * @param dateRange the day range requested
     */

    public void execute(String date, int dateRange) throws GetData.InvalidDataException {
        final FireInputData fireInputData = new FireInputData(date, dateRange);

        fireInteractor.execute(fireInputData);
    }
}
