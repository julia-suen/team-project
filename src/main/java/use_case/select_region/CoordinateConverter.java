package use_case.select_region;

import java.awt.geom.Point2D;
import org.jxmapviewer.viewer.GeoPosition;

/**
 * An interface for converting geographical coordinates to pixel coordinates.
 */
public interface CoordinateConverter {
    /**
     * Converts a GeoPosition to a Point2D.
     * @param geoPosition The geographical position to convert.
     * @return The corresponding Point2D.
     */
    Point2D geoToPixel(GeoPosition geoPosition);
}
