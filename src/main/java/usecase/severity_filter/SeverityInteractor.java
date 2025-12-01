package usecase.severity_filter;

import java.util.ArrayList;
import java.util.List;

import entities.Coordinate;
import entities.Fire;
import entities.FireFactory;
import entities.SeverityFilter;
import usecase.common.FireService;

/**
 * Interactor for the Fire Data Use Case.
 * Filters a given list of fires based off of the filter given.
 */

public class SeverityInteractor implements SeverityInputBoundary {

    private final SeverityOutputBoundary severityOutputBoundary;
    private final FireService fireService;

    /**
     * Constructs a SeverityInteractor.
     * @param severityOutputBoundary the presenter
     */
    public SeverityInteractor(SeverityOutputBoundary severityOutputBoundary) {
        this.severityOutputBoundary = severityOutputBoundary;
        this.fireService = new FireService();
    }

    @Override
    public void execute(SeverityInputData inputData) {


        List<Fire> currentFires = inputData.currentFires();
        final SeverityFilter severityFilter = inputData.severityFilter();

        if (currentFires == null) {
            currentFires = new ArrayList<>();
        }

        final List<Fire> filteredFires = fireService.filterFiresBySeverity(currentFires, severityFilter);

        final SeverityOutputData outputData = new SeverityOutputData(
                filteredFires
        );

        severityOutputBoundary.prepareSuccessView(outputData);

    }

}
