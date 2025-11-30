package use_case.marker;

public class MarkerOutputData {
    final double lat;
    final double lon;
    final int size;
    final String date;
    final double frp;

    public MarkerOutputData(double lat, double lon, int size, String date, double frp) {
        this.lat = lat;
        this.lon = lon;
        this.size = size;
        this.date = date;
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

    public String getDate() {
        return date;
    }

    public double getFrp() {
        return frp;
    }
}
