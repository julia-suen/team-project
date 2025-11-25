package view;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactory;

import entities.Region;
import interface_adapter.region.RegionRepository;

/**
 * Handles region selection logic and painting.
 */

class RegionSelectionHandler {
    private final RegionRepository regionRepo;
    private final JLabel provinceLabel;
    private final JPanel repaintTarget;
    private Region currentSelectedRegion;

    /**
     * Constructs a RegionSelectionHandler.
     * @param repo The repository to get region data from.
     * @param label The label to update with the selected province name.
     * @param target The panel to repaint after selection changes.
     */
    RegionSelectionHandler(final RegionRepository repo, final JLabel label, final JPanel target) {
        this.regionRepo = repo;
        this.provinceLabel = label;
        this.repaintTarget = target;
    }

    /**
     * Handles the logic for selecting a region based on a mouse click.
     * @param point The point of the mouse click.
     * @param map The JXMapViewer instance.
     */
    public void handleRegionSelection(final Point point, final JXMapViewer map) {
        final Collection<Region> allRegions = this.regionRepo.getAllRegions();
        boolean found = false;

        if (allRegions != null) {
            for (final Region region : allRegions) {
                if (this.pointInRegion(point, region, map)) {
                    this.currentSelectedRegion = region;
                    found = true;
                    break;
                }
            }
        }

        if (found) {
            this.provinceLabel.setText("Province: " + this.currentSelectedRegion.getProvinceName());
        }
        else {
            this.currentSelectedRegion = null;
            this.provinceLabel.setText("Province: None");
        }
        this.repaintTarget.repaint();
    }

    private boolean pointInRegion(final Point clickPoint, final Region region, final JXMapViewer map) {
        final List<List<GeoPosition>> boundaries = region.getBoundary();
        if (boundaries == null) {
            return false;
        }

        final TileFactory tf = map.getTileFactory();
        final int zoom = map.getZoom();
        final Rectangle viewportBounds = map.getViewportBounds();

        final Point2D worldClickPoint = new Point2D.Double(
                clickPoint.getX() + viewportBounds.x,
                clickPoint.getY() + viewportBounds.y
        );

        boolean found = false;
        for (final List<GeoPosition> geoPoly : boundaries) {
            if (geoPoly.size() < MapViewConfig.MIN_POLYGON_POINTS) {
                continue;
            }

            final Path2D path = new Path2D.Double();
            boolean first = true;
            for (final GeoPosition gp : geoPoly) {
                final Point2D pt = tf.geoToPixel(gp, zoom);
                if (first) {
                    path.moveTo(pt.getX(), pt.getY());
                    first = false;
                }
                else {
                    path.lineTo(pt.getX(), pt.getY());
                }
            }
            path.closePath();

            if (path.contains(worldClickPoint)) {
                found = true;
                break;
            }
        }
        return found;
    }

    /**
     * Gets the painter responsible for drawing the selected region's boundary.
     * @return A {@link Painter} for the region boundary.
     */
    public Painter<JXMapViewer> getRegionPainter() {
        return (graphics, map, width, height) -> {
            if (this.currentSelectedRegion == null) {
                return;
            }
            final List<List<GeoPosition>> boundaries = this.currentSelectedRegion.getBoundary();
            if (boundaries == null) {
                return;
            }

            final Graphics2D g2 = (Graphics2D) graphics.create();
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

                drawBoundary(boundaries, tf, zoom, g2);
            } finally {
                g2.dispose();
            }
        };
    }

    private static void drawBoundary(List<List<GeoPosition>> boundaries, TileFactory tileFactory,
                                     int zoom, Graphics2D graphics) {
        for (final List<GeoPosition> poly : boundaries) {
            if (poly.size() < 2) {
                continue;
            }

            final Path2D path = new Path2D.Double();
            boolean first = true;
            for (final GeoPosition gp : poly) {
                final Point2D pt = tileFactory.geoToPixel(gp, zoom);
                if (first) {
                    path.moveTo(pt.getX(), pt.getY());
                    first = false;
                } else {
                    path.lineTo(pt.getX(), pt.getY());
                }
            }
            path.closePath();
            graphics.draw(path);
        }
    }
}
