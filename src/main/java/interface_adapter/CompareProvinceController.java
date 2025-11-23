package interface_adapter;

import use_case.fire_data.CompareProvinceFiresInteractor;
import use_case.fire_data.CompareProvinceFiresInteractor.Result;
import entities.Fire;
import java.util.List;

/**
 * Controller for comparing wildfire activity between two provinces.
 * Calls the CompareProvinceFiresInteractor and returns the result.
 */
public class CompareProvinceController {

    private final CompareProvinceFiresInteractor interactor;

    public CompareProvinceController(CompareProvinceFiresInteractor interactor) {
        this.interactor = interactor;
    }

    /**
     * Compare two provinces' fires and return the result structure.
     *
     * @param firesA     Fire list for province A
     * @param firesB     Fire list for province B
     * @return Result object containing counts and trends
     */
    public Result compare(List<Fire> firesA, List<Fire> firesB) {
        return interactor.compare(firesA, firesB);
    }
}
