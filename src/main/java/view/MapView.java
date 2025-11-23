package view;

import entities.Fire;
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

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MapView extends JPanel {

    private final JXMapKit mapKit;
    private Set<FireWaypoint> waypoints;
    private final WaypointPainter<FireWaypoint> waypointPainter;

    static final int MIN_ZOOM = 0;
    static final int MAX_ZOOM = 16;

    static final double MIN_LAT = 25.0;
    static final double MAX_LAT = 75.0;
    static final double MIN_LON = -170.0;
    static final double MAX_LON = -50.0;

    private int zoomAccumulator = 0;
    private static final int ZOOM_THRESHOLD = 3;

    private boolean isEnforcingBounds = false;

    public MapView() {
        waypoints = new HashSet<>();

        waypointPainter = new WaypointPainter<>();
        waypointPainter.setRenderer(new FireWaypointRenderer());
        waypointPainter.setWaypoints(waypoints);

        setLayout(new BorderLayout());

        mapKit = new JXMapKit();
        mapKit.setZoomSliderVisible(false);
        mapKit.setZoomButtonsVisible(true);

        OSMTileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapKit.setTileFactory(tileFactory);

        JXMapViewer map = mapKit.getMainMap();

        map.setBackground(new Color(181, 208, 208));

        map.setFocusable(true);
        map.requestFocusInWindow();

        map.addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                zoomAccumulator += e.getWheelRotation();

                if (Math.abs(zoomAccumulator) >= ZOOM_THRESHOLD) {

                    GeoPosition positionUnderMouse = map.convertPointToGeoPosition(e.getPoint());

                    int direction = (zoomAccumulator > 0) ? 1 : -1;
                    int newZoom = map.getZoom() + direction;

                    if (newZoom >= MIN_ZOOM && newZoom <= MAX_ZOOM) {
                        map.setZoom(newZoom);

                        map.setCenterPosition(positionUnderMouse);
                    }

                    zoomAccumulator = 0;
                }
            }
        });

        PanKeyListener panListener = new PanKeyListener(map);
        map.addKeyListener(panListener);

        PanMouseInputListener mouseListener = new PanMouseInputListener(map);
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

        map.setOverlayPainter(waypointPainter);

        GeoPosition toronto = new GeoPosition(43.6532, -79.3832);
        mapKit.setAddressLocation(toronto);
        mapKit.setZoom(12);

        // Bounds Enforcement using invokeLater
        map.addPropertyChangeListener("zoom", evt -> {
            SwingUtilities.invokeLater(() -> enforceBounds(map));
        });

        map.addPropertyChangeListener("centerPosition", evt -> enforceBounds(map));

        map.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                enforceBounds(map);
            }
        });

        setupZoomButtons(mapKit);

        add(mapKit, BorderLayout.CENTER);
    }

    public void displayFires(List<Fire> fires) {
        Set<FireWaypoint> newWaypoints = new HashSet<>();

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
        if (isEnforcingBounds) return;
        isEnforcingBounds = true;

        try {
            int zoom = map.getZoom();

            if (zoom < MIN_ZOOM) {
                map.setZoom(MIN_ZOOM);
                return;
            }
            if (zoom > MAX_ZOOM) {
                map.setZoom(MAX_ZOOM);
                return;
            }

            Rectangle viewport = map.getViewportBounds();
            TileFactory tf = map.getTileFactory();

            Point2D topLeftLimit = tf.geoToPixel(new GeoPosition(MAX_LAT, MIN_LON), zoom);
            Point2D botRightLimit = tf.geoToPixel(new GeoPosition(MIN_LAT, MAX_LON), zoom);

            double halfWidth = viewport.getWidth() / 2.0;
            double halfHeight = viewport.getHeight() / 2.0;

            double minCenterX = topLeftLimit.getX() + halfWidth;
            double maxCenterX = botRightLimit.getX() - halfWidth;

            double minCenterY = topLeftLimit.getY() + halfHeight;
            double maxCenterY = botRightLimit.getY() - halfHeight;

            Point2D currentCenter = tf.geoToPixel(map.getCenterPosition(), zoom);
            double newX = currentCenter.getX();
            double newY = currentCenter.getY();

            // Horizontal Logic
            if (minCenterX > maxCenterX) {
                newX = (topLeftLimit.getX() + botRightLimit.getX()) / 2.0;
            } else {
                newX = Math.max(minCenterX, Math.min(maxCenterX, newX));
            }

            // Vertical Logic
            if (minCenterY > maxCenterY) {
                newY = (topLeftLimit.getY() + botRightLimit.getY()) / 2.0;
            } else {
                newY = Math.max(minCenterY, Math.min(maxCenterY, newY));
            }

            if (Math.abs(newX - currentCenter.getX()) > 1.0 || Math.abs(newY - currentCenter.getY()) > 1.0) {
                GeoPosition newPos = tf.pixelToGeo(new Point2D.Double(newX, newY), zoom);
                map.setCenterPosition(newPos);
            }

        } finally {
            isEnforcingBounds = false;
        }
    }

    private void setupZoomButtons(JXMapKit kit) {
        JButton zoomIn = kit.getZoomInButton();
        JButton zoomOut = kit.getZoomOutButton();

        // Preserve previous styling
        Dimension buttonSize = new Dimension(40, 40);
        zoomIn.setPreferredSize(buttonSize);
        zoomOut.setPreferredSize(buttonSize);

        for (ActionListener al : zoomIn.getActionListeners()) zoomIn.removeActionListener(al);
        for (ActionListener al : zoomOut.getActionListeners()) zoomOut.removeActionListener(al);

        // Restore focus after button click
        zoomIn.addActionListener(ev -> {
            if (kit.getMainMap().getZoom() < MAX_ZOOM) {
                kit.getMainMap().setZoom(kit.getMainMap().getZoom() + 1);
            }
            kit.getMainMap().requestFocusInWindow();
        });

        zoomOut.addActionListener(ev -> {
            if (kit.getMainMap().getZoom() > MIN_ZOOM) {
                kit.getMainMap().setZoom(kit.getMainMap().getZoom() - 1);
            }
            kit.getMainMap().requestFocusInWindow();
        });
    }

    public void addFireMarker(GeoPosition location, double radius) {
        waypoints.add(new FireWaypoint(location, radius));
        waypointPainter.setWaypoints(waypoints);
        mapKit.getMainMap().repaint();
    }

    public void clearFires() {
        waypoints.clear();
        waypointPainter.setWaypoints(waypoints);
        mapKit.getMainMap().repaint();
    }

    public JXMapKit getMapKit() {
        return mapKit;
    }

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

    public class FireWaypointRenderer implements WaypointRenderer<FireWaypoint> {
        @Override
        public void paintWaypoint(Graphics2D g, JXMapViewer map, FireWaypoint wp) {
            Point2D centerPoint = map.getTileFactory().geoToPixel(wp.getPosition(), map.getZoom());
            double distanceDegrees = wp.getRadius();

            GeoPosition northPoint = new GeoPosition(
                    wp.getPosition().getLatitude() + distanceDegrees,
                    wp.getPosition().getLongitude()
            );
            Point2D radiusPoint = map.getTileFactory().geoToPixel(northPoint, map.getZoom());

            double pixelRadius = Math.abs(centerPoint.getY() - radiusPoint.getY());
            int radius = (int) pixelRadius;
            if (radius < 5) radius = 5;

            int diameter = radius * 2;
            int x = (int) (centerPoint.getX() - radius);
            int y = (int) (centerPoint.getY() - radius);

            g.setColor(new Color(255, 0, 0, 100));
            g.fillOval(x, y, diameter, diameter);

            g.setColor(Color.RED);
            g.setStroke(new BasicStroke(2));
            g.drawOval(x, y, diameter, diameter);
        }
    }
}