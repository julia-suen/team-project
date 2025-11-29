package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.WaypointRenderer;

/**
 * A renderer for painting FireWaypoint objects on the map.
 */
public class FireWaypointRenderer implements WaypointRenderer<FireWaypoint> {
    @Override
    public void paintWaypoint(final Graphics2D g, final JXMapViewer map, final FireWaypoint wp) {
        final Point2D centerPoint = map.getTileFactory().geoToPixel(wp.getPosition(), map.getZoom());
        final GeoPosition northPoint = new GeoPosition(
                wp.getPosition().getLatitude() + wp.getRadius(),
                wp.getPosition().getLongitude()
        );
        final Point2D radiusPoint = map.getTileFactory().geoToPixel(northPoint, map.getZoom());

        final double pixelRadius = Math.abs(centerPoint.getY() - radiusPoint.getY());
        int radius = (int) pixelRadius;
        if (radius < MapViewConfig.MIN_FIRE_RADIUS) {
            radius = MapViewConfig.MIN_FIRE_RADIUS;
        }
        final int diameter = radius * 2;
        final int x = (int) (centerPoint.getX() - radius);
        final int y = (int) (centerPoint.getY() - radius);

        g.setColor(MapViewConfig.FIRE_FILL_COLOR);
        g.fillOval(x, y, diameter, diameter);
        g.setColor(Color.RED);
        g.setStroke(new BasicStroke(MapViewConfig.FIRE_STROKE_WIDTH));
        g.drawOval(x, y, diameter, diameter);
    }
}
