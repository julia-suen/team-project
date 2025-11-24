package view;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import javax.swing.SwingUtilities;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactory;

/**
 * Handles the logic for enforcing map boundaries.
 */
class MapBoundsEnforcer {
    private boolean isEnforcing;

    /**
     * Constructs a MapBoundsEnforcer and attaches it to the given map.
     * @param map The JXMapViewer instance to enforce bounds on.
     */
    MapBoundsEnforcer(final JXMapViewer map) {
        this.isEnforcing = false;
        map.addPropertyChangeListener("zoom", event -> SwingUtilities.invokeLater(() -> this.enforce(map)));
        map.addPropertyChangeListener("centerPosition", event -> SwingUtilities.invokeLater(() -> this.enforce(map)));
    }

    private void enforce(final JXMapViewer map) {
        if (this.isEnforcing) {
            return;
        }
        this.isEnforcing = true;

        try {
            final int zoom = map.getZoom();
            if (zoom > 16 || zoom < 0) {
                map.setZoom(Math.max(0, Math.min(16, zoom)));
                return;
            }

            final Rectangle viewport = map.getViewportBounds();
            final TileFactory tf = map.getTileFactory();

            final Point2D currentCenter = tf.geoToPixel(map.getCenterPosition(), zoom);
            final double newX = clampValue(currentCenter.getX(), viewport.getWidth(),
                    tf.geoToPixel(new GeoPosition(MapViewConfig.MAX_LAT, MapViewConfig.MIN_LON), zoom).getX(),
                    tf.geoToPixel(new GeoPosition(MapViewConfig.MIN_LAT, MapViewConfig.MAX_LON), zoom).getX());
            final double newY = clampValue(currentCenter.getY(), viewport.getHeight(),
                    tf.geoToPixel(new GeoPosition(MapViewConfig.MAX_LAT, MapViewConfig.MIN_LON), zoom).getY(),
                    tf.geoToPixel(new GeoPosition(MapViewConfig.MIN_LAT, MapViewConfig.MAX_LON), zoom).getY());

            if (Math.abs(newX - currentCenter.getX()) > MapViewConfig.CENTER_POSITION_TOLERANCE
                    || Math.abs(newY - currentCenter.getY()) > MapViewConfig.CENTER_POSITION_TOLERANCE) {
                final GeoPosition newPos = tf.pixelToGeo(new Point2D.Double(newX, newY), zoom);
                map.setCenterPosition(newPos);
            }
        } finally {
            this.isEnforcing = false;
        }
    }

    private double clampValue(final double value, final double viewportSize, final double min, final double max) {
        final double half = viewportSize / 2.0;
        final double minCenter = min + half;
        final double maxCenter = max - half;

        if (minCenter > maxCenter) {
            return (min + max) / 2.0;
        } else {
            return Math.max(minCenter, Math.min(maxCenter, value));
        }
    }
}
