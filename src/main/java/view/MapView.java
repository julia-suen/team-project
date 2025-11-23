package view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jxmapviewer.JXMapKit;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactory;
import org.jxmapviewer.viewer.WaypointPainter;
import org.jxmapviewer.viewer.WaypointRenderer;

import entities.Fire;

/**
 * The MapView panel responsible for displaying the map and fire markers.
 */
public class MapView extends JPanel {

    private static final int MIN_ZOOM = 0;
    private static final int MAX_ZOOM = 16;
    private static final int ZOOM_THRESHOLD = 3;
    private static final int DEFAULT_ZOOM = 12;
    private static final int BUTTON_SIZE = 40;
    private static final int MIN_PIXEL_RADIUS = 5;
    private static final int FILL_ALPHA = 100;
    private static final int STROKE_WIDTH = 2;

    private static final double MIN_LAT = 25.0;
    private static final double MAX_LAT = 75.0;
    private static final double MIN_LON = -170.0;
    private static final double MAX_LON = -50.0;
    private static final double TORONTO_LAT = 43.6532;
    private static final double TORONTO_LON = -79.3832;

    private static final Color MAP_BG_COLOR = new Color(181, 208, 208);
    private static final Color FIRE_FILL_COLOR = new Color(255, 0, 0, FILL_ALPHA);

    private final JXMapKit mapKit;
    private final WaypointPainter<FireWaypoint> waypointPainter;
    private Set<FireWaypoint> waypoints;

    private int zoomAccumulator;
    private boolean isEnforcingBounds;

    /**
     * Constructs the MapView.
     */
    public MapView() {
        setLayout(new BorderLayout());

        waypoints = new HashSet<>();
        waypointPainter = new WaypointPainter<>();
        waypointPainter.setRenderer(new FireWaypointRenderer());
        waypointPainter.setWaypoints(waypoints);

        mapKit = new JXMapKit();

        initializeMap();
        initializeInteractions();
        configureBoundsEnforcement();

        add(mapKit, BorderLayout.CENTER);
    }

    private void initializeMap() {
        mapKit.setZoomSliderVisible(false);
        mapKit.setZoomButtonsVisible(true);

        final OSMTileFactoryInfo info = new OSMTileFactoryInfo();
        final DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapKit.setTileFactory(tileFactory);

        final JXMapViewer map = mapKit.getMainMap();
        map.setBackground(MAP_BG_COLOR);
        map.setFocusable(true);
        map.requestFocusInWindow();
        map.setOverlayPainter(waypointPainter);

        final GeoPosition toronto = new GeoPosition(TORONTO_LAT, TORONTO_LON);
        mapKit.setAddressLocation(toronto);
        mapKit.setZoom(DEFAULT_ZOOM);

        setupZoomButtons(mapKit);
    }

    private void initializeInteractions() {
        final JXMapViewer map = mapKit.getMainMap();

        // Use custom inner class to reduce anonymous class length
        map.addMouseWheelListener(new ZoomScrollListener(map));

        final PanKeyListener panListener = new PanKeyListener(map);
        map.addKeyListener(panListener);

        final PanMouseInputListener mouseListener = new PanMouseInputListener(map);
        map.addMouseListener(mouseListener);
        map.addMouseMotionListener(mouseListener);

        map.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    if (map.getZoom() > 0) {
                        map.setZoom(map.getZoom() - 1);
                        map.requestFocusInWindow();
                    }
                }
            }
        });
    }

    private void configureBoundsEnforcement() {
        final JXMapViewer map = mapKit.getMainMap();

        map.addPropertyChangeListener("zoom", evt -> SwingUtilities.invokeLater(() -> enforceBounds(map)));
        map.addPropertyChangeListener("centerPosition", evt -> enforceBounds(map));
        map.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                enforceBounds(map);
            }
        });
    }

    /**
     * Updates the map with a new list of fires.
     * Uses a batch update strategy to prevent ConcurrentModificationException.
     * @param fires the list of fires to display
     */
    public void displayFires(List<Fire> fires) {
        final Set<FireWaypoint> newWaypoints = new HashSet<>();

        if (fires != null) {
            for (Fire fire : fires) {
                newWaypoints.add(new FireWaypoint(fire.getCenter(), fire.getRadius()));
            }
        }

        this.waypoints = newWaypoints;
        waypointPainter.setWaypoints(newWaypoints);
        mapKit.getMainMap().repaint();
    }

    private void enforceBounds(JXMapViewer map) {
        if (isEnforcingBounds) {
            return;
        }
        isEnforcingBounds = true;

        try {
            final int zoom = map.getZoom();

            if (zoom < MIN_ZOOM) {
                map.setZoom(MIN_ZOOM);
                return;
            }
            if (zoom > MAX_ZOOM) {
                map.setZoom(MAX_ZOOM);
                return;
            }

            final Rectangle viewport = map.getViewportBounds();
            final TileFactory tf = map.getTileFactory();

            final Point2D topLeftLimit = tf.geoToPixel(new GeoPosition(MAX_LAT, MIN_LON), zoom);
            final Point2D botRightLimit = tf.geoToPixel(new GeoPosition(MIN_LAT, MAX_LON), zoom);

            final double halfWidth = viewport.getWidth() / 2.0;
            final double halfHeight = viewport.getHeight() / 2.0;

            final double minCenterX = topLeftLimit.getX() + halfWidth;
            final double maxCenterX = botRightLimit.getX() - halfWidth;
            final double minCenterY = topLeftLimit.getY() + halfHeight;
            final double maxCenterY = botRightLimit.getY() - halfHeight;

            final Point2D currentCenter = tf.geoToPixel(map.getCenterPosition(), zoom);
            double newX = currentCenter.getX();
            double newY = currentCenter.getY();

            // Horizontal Logic
            if (minCenterX > maxCenterX) {
                newX = (topLeftLimit.getX() + botRightLimit.getX()) / 2.0;
            }
            else {
                newX = Math.max(minCenterX, Math.min(maxCenterX, newX));
            }

            // Vertical Logic
            if (minCenterY > maxCenterY) {
                newY = (topLeftLimit.getY() + botRightLimit.getY()) / 2.0;
            }
            else {
                newY = Math.max(minCenterY, Math.min(maxCenterY, newY));
            }

            if (Math.abs(newX - currentCenter.getX()) > 1.0 || Math.abs(newY - currentCenter.getY()) > 1.0) {
                final GeoPosition newPos = tf.pixelToGeo(new Point2D.Double(newX, newY), zoom);
                map.setCenterPosition(newPos);
            }

        }
        finally {
            isEnforcingBounds = false;
        }
    }

    private void setupZoomButtons(JXMapKit kit) {
        final JButton zoomIn = kit.getZoomInButton();
        final JButton zoomOut = kit.getZoomOutButton();

        final Dimension buttonSize = new Dimension(BUTTON_SIZE, BUTTON_SIZE);
        zoomIn.setPreferredSize(buttonSize);
        zoomOut.setPreferredSize(buttonSize);

        for (ActionListener al : zoomIn.getActionListeners()) {
            zoomIn.removeActionListener(al);
        }
        for (ActionListener al : zoomOut.getActionListeners()) {
            zoomOut.removeActionListener(al);
        }

        zoomIn.addActionListener(event -> {
            if (kit.getMainMap().getZoom() < MAX_ZOOM) {
                kit.getMainMap().setZoom(kit.getMainMap().getZoom() + 1);
            }
            kit.getMainMap().requestFocusInWindow();
        });

        zoomOut.addActionListener(event -> {
            if (kit.getMainMap().getZoom() > MIN_ZOOM) {
                kit.getMainMap().setZoom(kit.getMainMap().getZoom() - 1);
            }
            kit.getMainMap().requestFocusInWindow();
        });
    }

    /**
     * Adds a single fire marker. Note: prefer displayFires() for batch updates.
     * @param location the location of the fire
     * @param radius the radius of the fire
     */
    public void addFireMarker(GeoPosition location, double radius) {
        waypoints.add(new FireWaypoint(location, radius));
        waypointPainter.setWaypoints(waypoints);
        mapKit.getMainMap().repaint();
    }

    /**
     * Clears all fire markers from the map.
     */
    public void clearFires() {
        waypoints.clear();
        waypointPainter.setWaypoints(waypoints);
        mapKit.getMainMap().repaint();
    }

    public JXMapKit getMapKit() {
        return mapKit;
    }

    /**
     * Helper class for handling scroll zoom logic.
     */
    private class ZoomScrollListener extends MouseAdapter {
        private final JXMapViewer map;

        ZoomScrollListener(JXMapViewer map) {
            this.map = map;
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            zoomAccumulator += e.getWheelRotation();

            if (Math.abs(zoomAccumulator) >= ZOOM_THRESHOLD) {
                final GeoPosition positionUnderMouse = map.convertPointToGeoPosition(e.getPoint());
                final int direction = (zoomAccumulator > 0) ? 1 : -1;
                final int newZoom = map.getZoom() + direction;

                if (newZoom >= MIN_ZOOM && newZoom <= MAX_ZOOM) {
                    map.setZoom(newZoom);
                    map.setCenterPosition(positionUnderMouse);
                }
                zoomAccumulator = 0;
            }
        }
    }

    /**
     * Waypoint representing a Fire on the map.
     */
    public static class FireWaypoint extends DefaultWaypoint {
        private final double radius;

        public FireWaypoint(GeoPosition coord, double radius) {
            super(coord);
            this.radius = radius;
        }

        public double getRadius() {
            return radius;
        }
    }

    /**
     * Renderer for FireWaypoints.
     */
    public class FireWaypointRenderer implements WaypointRenderer<FireWaypoint> {
        @Override
        public void paintWaypoint(Graphics2D g, JXMapViewer map, FireWaypoint wp) {
            final Point2D centerPoint = map.getTileFactory().geoToPixel(wp.getPosition(), map.getZoom());
            final double distanceDegrees = wp.getRadius();

            final GeoPosition northPoint = new GeoPosition(
                    wp.getPosition().getLatitude() + distanceDegrees,
                    wp.getPosition().getLongitude()
            );
            final Point2D radiusPoint = map.getTileFactory().geoToPixel(northPoint, map.getZoom());

            final double pixelRadius = Math.abs(centerPoint.getY() - radiusPoint.getY());
            int radius = (int) pixelRadius;
            if (radius < MIN_PIXEL_RADIUS) {
                radius = MIN_PIXEL_RADIUS;
            }

            final int diameter = radius * 2;
            final int x = (int) (centerPoint.getX() - radius);
            final int y = (int) (centerPoint.getY() - radius);

            g.setColor(FIRE_FILL_COLOR);
            g.fillOval(x, y, diameter, diameter);

            g.setColor(Color.RED);
            g.setStroke(new BasicStroke(STROKE_WIDTH));
            g.drawOval(x, y, diameter, diameter);
        }
    }
}
