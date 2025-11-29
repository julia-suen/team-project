package view;

import entities.Region;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.List;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactory;

/**
 * A painter for drawing the boundary of a given region.
 */
public class RegionBoundaryPainter implements Painter<JXMapViewer> {

    private Region region;

    /**
     * Creates a new instance of RegionBoundaryPainter.
     */
    public RegionBoundaryPainter() {
        this.region = null;
    }

    /**
     * Creates a new instance of RegionBoundaryPainter with the given region.
     * @param region The region to display.
     */
    public RegionBoundaryPainter(final Region region) {
        this.region = region;
    }

    /**
     * Sets the region to be painted.
     * @param region The region to display, or null to clear the boundary.
     */
    public void setRegion(final Region region) {
        this.region = region;
    }

    /**
     * Checks if a given point is inside the region's boundary.
     * @param p The point to check, in viewport coordinates.
     * @param map The map viewer.
     * @return true if the point is inside the region, false otherwise.
     */
    public boolean isPointInside(final Point p, final JXMapViewer map) {
        if (this.region == null) {
            return false;
        }
        final List<List<GeoPosition>> boundaries = this.region.getBoundary();
        if (boundaries == null) {
            return false;
        }

        final int zoom = map.getZoom();
        final TileFactory tf = map.getTileFactory();
        final Rectangle viewportBounds = map.getViewportBounds();

        // The point 'p' is in viewport coordinates. We need to convert it to world coordinates
        // to match the path created from geoToPixel coordinates.
        final Point2D pWorld = new Point2D.Double(p.x + viewportBounds.x, p.y + viewportBounds.y);

        for (final List<GeoPosition> poly : boundaries) {
            if (poly.size() < 2) {
                continue;
            }

            // Create a path in world coordinates
            final Path2D path = new Path2D.Double();
            boolean first = true;
            for (final GeoPosition gp : poly) {
                final Point2D pt = tf.geoToPixel(gp, zoom);
                if (first) {
                    path.moveTo(pt.getX(), pt.getY());
                    first = false;
                } else {
                    path.lineTo(pt.getX(), pt.getY());
                }
            }
            path.closePath();

            if (path.contains(pWorld)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void paint(final Graphics2D g, final JXMapViewer map, final int w, final int h) {
        if (this.region == null) {
            return;
        }
        final List<List<GeoPosition>> boundaries = this.region.getBoundary();
        if (boundaries == null) {
            return;
        }

        final Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            final Rectangle viewportBounds = map.getViewportBounds();
            g2.translate(-viewportBounds.x, -viewportBounds.y);

            g2.setColor(MapViewConfig.BOUNDARY_COLOR);
            g2.setStroke(new BasicStroke(
                    MapViewConfig.BOUNDARY_STROKE_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND
            ));

            final int zoom = map.getZoom();
            final TileFactory tf = map.getTileFactory();

            for (final List<GeoPosition> poly : boundaries) {
                if (poly.size() < 2) {
                    continue;
                }

                final Path2D path = new Path2D.Double();
                boolean first = true;
                for (final GeoPosition gp : poly) {
                    final Point2D pt = tf.geoToPixel(gp, zoom);
                    if (first) {
                        path.moveTo(pt.getX(), pt.getY());
                        first = false;
                    } else {
                        path.lineTo(pt.getX(), pt.getY());
                    }
                }
                path.closePath();
                g2.draw(path);
            }
        } finally {
            g2.dispose();
        }
    }
}
