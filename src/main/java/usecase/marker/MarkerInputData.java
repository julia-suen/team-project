package usecase.marker;

/**
 * The input data for the marker use case.
 * Represents a request that a user makes to the program by applying a filter to the fires loaded.
 */
public class MarkerInputData {
    private final double lat;
    private final double lon;

    public MarkerInputData(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

}
