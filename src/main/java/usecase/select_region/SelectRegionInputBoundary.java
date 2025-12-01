package usecase.select_region;

/**
 * The input boundary for the Select Region use case.
 * This interface is implemented by the interactor and called by the controller.
 */
public interface SelectRegionInputBoundary {

    /**
     * Executes the region selection use case.
     *
     * @param selectRegionInputData The input data containing the clicked position.
     */
    void execute(SelectRegionInputData selectRegionInputData);
}
