package interface_adapter.select_region;

import org.jxmapviewer.viewer.GeoPosition;
import usecase.select_region.SelectRegionInputBoundary;
import usecase.select_region.SelectRegionInputData;

/**
 * The Controller for the Select Region use case. It takes user input from the
 * view and passes it to the interactor.
 */
public class SelectRegionController {

    private final SelectRegionInputBoundary selectRegionInteractor;

    /**
     * Constructs a SelectRegionController.
     * @param selectRegionInteractor The interactor to execute the use case.
     */
    public SelectRegionController(final SelectRegionInputBoundary selectRegionInteractor) {
        this.selectRegionInteractor = selectRegionInteractor;
    }

    /**
     * Executes the use case with the given geographical position.
     * @param geoPosition The position of the user's click.
     */
    public void execute(final GeoPosition geoPosition) {
        final SelectRegionInputData inputData = new SelectRegionInputData(geoPosition);
        this.selectRegionInteractor.execute(inputData);
    }
}
