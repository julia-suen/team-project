package usecase.marker;

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
