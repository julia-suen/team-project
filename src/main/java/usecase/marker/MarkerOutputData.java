package usecase.marker;

/**
 * Output Data for the Marker Use Case.
 * Containing the fire's latitude, longitude, number of fires (size) and frp.
 */
public class MarkerOutputData {
    final double lat;
    final double lon;
    final int size;
    final double frp;

    /**
     * Constructs a MarkerOutput object.
     * @param lat the latitude of hovered fire
     * @param lon the longitude of hovered fire
     * @param size the number of fires that are bundled in the hovered fire
     * @param frp the average frp of the hovered fire
     */
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
