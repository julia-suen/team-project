package interface_adapter.fire_data;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import interface_adapter.ViewModel;

/**
 * The ViewModel for the Fire Data View.
 * Manages the state of the fire analysis and notifies listeners of changes.
 */
public class FireViewModel extends ViewModel<FireState> {

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    /**
     * Constructs a new FireViewModel with an initial empty state.
     */
    public FireViewModel() {
        super("fire view");
        setState(new FireState());
    }

    /**
     * Fires a property change event to notify listeners that the state has changed.
     */
    @Override
    public void firePropertyChange() {
        support.firePropertyChange("state", null, this.getState());
    }

    /**
     * Adds a PropertyChangeListener to this ViewModel.
     * @param listener the listener to add
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}
