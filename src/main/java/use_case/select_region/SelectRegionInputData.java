package use_case.select_region;

import org.jxmapviewer.viewer.GeoPosition;

/**
 * A data class to hold the input for the SelectRegion use case,
 * specifically the geographical position of the user's click.
 */
public class SelectRegionInputData {

    private final GeoPosition geoPosition;

    /**
     * Constructs a SelectRegionInputData object.
     * @param geoPosition The geographical position of the click.
     */
    public SelectRegionInputData(final GeoPosition geoPosition) {
        this.geoPosition = geoPosition;
    }

    /**
     * Gets the geographical position.
     * @return The {@link GeoPosition}.
     */
    public GeoPosition getGeoPosition() {
        return this.geoPosition;
    }
}
