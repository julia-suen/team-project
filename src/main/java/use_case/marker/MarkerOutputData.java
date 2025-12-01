package use_case.marker;

public class MarkerOutputData {
    final double lat;
    final double lon;
    final int size;
    final double frp;

    public MarkerOutputData(double lat, double lon, int size, double frp) {
        this.lat = lat;
        this.lon = lon;
        this.size = size;
        this.frp = frp;
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

    public double getFrp() {
        return frp;
    }
}
