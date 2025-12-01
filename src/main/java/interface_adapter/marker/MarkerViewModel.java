package interface_adapter.marker;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class MarkerViewModel {

    private double lat;
    private double lon;
    private int size;
    private String date;
    private double frp;
    private String error;

    public static final String MARKER_HOVER_PROPERTY = "markerHover";

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.support.addPropertyChangeListener(listener);
    }

    public void firePropertyChanged() {
        this.support.firePropertyChange(MARKER_HOVER_PROPERTY, null, null);
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public int getSize() {
        return size;
    }

    public String getDate() {
        return date;
    }

    public double getFrp() {
        return frp;
    }

    public String getError() {
        return error;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setFrp(double frp) {
        this.frp = frp;
    }

    public void setError(String error) {
        this.error = error;
    }
}
