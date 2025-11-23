package interface_adapter.fire_data;

import interface_adapter.ViewModel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class FireViewModel extends ViewModel<FireState> {

    public FireViewModel() {
        super("fire view");
        setState(new FireState());
    }

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public void firePropertyChange() {
        support.firePropertyChange("state", null, this.getState());
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}