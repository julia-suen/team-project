package interface_adapter.select_region;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * The ViewModel for the Select Region use case. It holds the state
 * that the view observes to update itself.
 */
public class SelectRegionViewModel {

    public static final String PROVINCE_LABEL = "Province: ";
    private String selectedProvince = "None";

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    /**
     * Fires a property change event to notify observers.
     */
    public void firePropertyChanged() {
        this.support.firePropertyChange("selectedProvince", null, this.selectedProvince);
    }

    /**
     * Adds a property change listener.
     * @param listener The listener to add.
     */
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        this.support.addPropertyChangeListener(listener);
    }

    /**
     * Gets the currently selected province name.
     * @return The name of the province.
     */
    public String getSelectedProvince() {
        return this.selectedProvince;
    }

    /**
     * Sets the selected province name and notifies listeners.
     * @param selectedProvince The new province name.
     */
    public void setSelectedProvince(final String selectedProvince) {
        this.selectedProvince = selectedProvince;
    }
}
