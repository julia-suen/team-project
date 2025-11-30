package interface_adapter.severity_filter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import entities.Fire;
import entities.SeverityFilter;
import interface_adapter.fire_data.FireViewModel;
import use_case.severity_filter.SeverityInputBoundary;
import use_case.severity_filter.SeverityInputData;

/**
 * Controller for the severity filter use case, stores fires on the map in a cache.
 */

public class SeverityController implements PropertyChangeListener {
    private final SeverityInputBoundary severityInteractor;
    private List<Fire> cachedFires = new ArrayList<>();

    public SeverityController(SeverityInputBoundary severityInteractor) {
        this.severityInteractor = severityInteractor;
    }

    /**
     * Updates cache with fires on the map.
     */

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("fires_loaded".equals(evt.getPropertyName())) {
            List<Fire> fires = (List<Fire>) evt.getNewValue();
            this.cachedFires = new ArrayList<>(fires);
        }
    }

    /**
     * Execute the filter severity use case using fires currently on map (stored in cache).
     * @param filter the severity filter to apply
     */
    public void filterBySeverity(SeverityFilter filter) {

        final SeverityInputData inputData = new SeverityInputData(cachedFires, filter);
        severityInteractor.execute(inputData);
    }
}
