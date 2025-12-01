package interface_adapter.severity_filter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import entities.Fire;
import entities.SeverityFilter;
import interface_adapter.fire_data.FireState;
import usecase.severity_filter.SeverityInputBoundary;
import usecase.severity_filter.SeverityInputData;

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
        if ("state".equals(evt.getPropertyName())) {
            FireState state = (FireState) evt.getNewValue();
            if (state.getLoadedFires() != null) {
                this.cachedFires = new ArrayList<>(state.getLoadedFires());
            }
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
