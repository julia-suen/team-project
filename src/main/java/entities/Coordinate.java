package entities;

/**
 * A simple entity representing a coordinate with a latitude and longitude.
 */

public class Coordinate {

    private static final int LAT_UP_BOUNDARY = 90;
    private static final int LAT_LOW_BOUNDARY = -90;
    private static final int LON_UP_BOUNDARY = 180;
    private static final int LON_LOW_BOUNDARY = -180;
    private final double lat;
    private final double lon;
    private final String[] dateDayConfidence;
    private final double[] brightness;

    /**
     * Creates a coordinate with the given latitude, longitude, and other data.
     * @param lat the latitude of the coordinate
     * @param lon the longitude of the coordinate
     * @param date_day_confidence the date, day/night value, and confidence value of a given coordinate.
     *                            may be set to "n/a" as appropriate.
     * @param brightness the 2 brightness values of the given coordinate
     * @throws IllegalArgumentException if the input values are not valid coordinates
     */

    public Coordinate(double lat, double lon, String[] date_day_confidence, double[] brightness) {
        if (lat > LAT_UP_BOUNDARY || lat < LAT_LOW_BOUNDARY
                || lon > LON_UP_BOUNDARY || lon < LON_LOW_BOUNDARY) {
            throw new IllegalArgumentException("Invalid coordinates given.");
        }
        this.lat = lat;
        this.lon = lon;
        this.dateDayConfidence = date_day_confidence;
        this.brightness = brightness;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String[] getDateDayConfidence() {
        return dateDayConfidence;
    }

    public double[] getBrightness() {
        return brightness;
    }
}
