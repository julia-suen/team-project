package interface_adapter.marker;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * The View Model for the Marker View used in Map View.
 */
public class MarkerViewModel {

    private double lat;
    private double lon;
    private int size;
    private double frp;
    private String error;

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    /**
     * Adds a property change listener.
     * @param listener The listener to add.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.support.addPropertyChangeListener(listener);
    }

    /**
     * Fires a property change event to notify listeners.
     */
    public void firePropertyChanged() {
        this.support.firePropertyChange("markerHover", null, null);
    }

    // getters
    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public int getSize() {
        return size;
    }

    public double getFrp() {
        return frp;
    }

    public String getError() {
        return error;
    }

    // setters
    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setFrp(double frp) {
        this.frp = frp;
    }

    public void setError(String error) {
        this.error = error;
    }
}
