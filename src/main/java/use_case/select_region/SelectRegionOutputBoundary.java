package use_case.select_region;

/**
 * The output boundary for the Select Region use case.
 * This interface is implemented by the presenter to receive the result from the interactor.
 */
public interface SelectRegionOutputBoundary {

    /**
     * Prepares the view to display the successful selection of a region.
     *
     * @param selectRegionOutputData The output data containing the selected province name.
     */
    void prepareSuccessView(SelectRegionOutputData selectRegionOutputData);

}
