package interface_adapter.favourites;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * View Model for favourites use case.
 */
public class FavouritesViewModel {
    public static final String FAVOURITES_UPDATED = "favourites_updated";

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private FavouritesState state = new FavouritesState();

    public FavouritesState getState() {
        return state;
    }

    public void setState(FavouritesState state) {
        this.state = state;
    }

    public void firePropertyChange() {
        support.firePropertyChange(FAVOURITES_UPDATED, null, null);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}
