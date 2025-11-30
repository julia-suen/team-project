package use_case.severity_filter;

import java.util.ArrayList;
import java.util.List;

import entities.Coordinate;
import entities.Fire;
import entities.FireFactory;
import entities.SeverityFilter;

/**
 * Interactor for the Fire Data Use Case.
 * Filters a given list of fires based off of the filter given.
 */

public class SeverityInteractor implements SeverityInputBoundary {

    private final SeverityOutputBoundary severityOutputBoundary;

    /**
     * Constructs a SeverityInteractor.
     * @param severityOutputBoundary the presenter
     */
    public SeverityInteractor(SeverityOutputBoundary severityOutputBoundary) {
        this.severityOutputBoundary = severityOutputBoundary;
    }

    @Override
    public void execute(SeverityInputData inputData) {

        final List<Fire> currentFires = inputData.currentFires();
        final SeverityFilter severityFilter = inputData.severityFilter();

        final List<Fire> filteredFires;

        if (currentFires == null || currentFires.isEmpty()) {
            filteredFires = new ArrayList<>();
        }
        else {
            final List<List<Coordinate>> bundles = convertFires(currentFires);
            filteredFires = FireFactory.filterFires(bundles, severityFilter);
        }

        final SeverityOutputData outputData = new SeverityOutputData(
                filteredFires
        );

        severityOutputBoundary.prepareSuccessView(outputData);

    }

    /**
     * Helper method that converts Fire objects back to bundles of coordinates to reuse FireFactory.filterFires method.
     * @param fires the fires to unpack into bundles of coordinates
     * @return bundles of coordinates extracted from the given Fire objects
     */
    private List<List<Coordinate>> convertFires(List<Fire> fires) {
        final List<List<Coordinate>> bundles = new ArrayList<>();

        for (Fire fire : fires) {
            bundles.add(fire.getCoordinates());
        }
        return bundles;
    }

}
