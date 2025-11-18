package view;

import org.jxmapviewer.JXMapKit;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.WaypointPainter;
import org.jxmapviewer.viewer.WaypointRenderer;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;

public class MapView extends JPanel {

    private final JXMapKit mapKit;
    private final Set<FireWaypoint> waypoints;
    private final WaypointPainter<FireWaypoint> waypointPainter;

    public MapView() {
        waypoints = new HashSet<>();
        waypointPainter = new WaypointPainter<>();
        waypointPainter.setRenderer(new FireWaypointRenderer());
        waypointPainter.setWaypoints(waypoints);

        setLayout(new BorderLayout());

        mapKit = new JXMapKit();
        mapKit.setZoomSliderVisible(false);
        mapKit.setZoomButtonsVisible(true);

        // Use OpenStreetMap tiles
        OSMTileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapKit.setTileFactory(tileFactory);

        // Attach waypoint painter to the main map
        JXMapViewer map = mapKit.getMainMap();

        // Continuous zoom effect (Google Earth-style)
        map.addMouseWheelListener(e -> {
            double factor = (e.getWheelRotation() < 0) ? 1.1 : 0.9;
            map.setZoom(map.getZoom());
            map.repaint();
            map.getGraphics().translate(
                    (int)((1 - factor) * map.getWidth() / 2),
                    (int)((1 - factor) * map.getHeight() / 2)
            );
        });

        map.setOverlayPainter(waypointPainter);

        Dimension buttonSize = new Dimension(40, 40);
        JButton zoomIn = mapKit.getZoomInButton();
        JButton zoomOut = mapKit.getZoomOutButton();
        zoomIn.setPreferredSize(buttonSize);
        zoomOut.setPreferredSize(buttonSize);

        GeoPosition toronto = new GeoPosition(43.6532, -79.3832);
        mapKit.setAddressLocation(toronto);
        mapKit.setZoom(12);

        // Zoom and map boundary limits
        final int MIN_ZOOM = 7;
        final int MAX_ZOOM = 15;
        final double MIN_LAT = 25.0;
        final double MAX_LAT = 83.0;
        final double MIN_LON = -170.0;
        final double MAX_LON = -50.0;

        map.addPropertyChangeListener("zoom", evt -> {
            int before = (Integer) evt.getOldValue();
            int after = (Integer) evt.getNewValue();

            if (after < MIN_ZOOM || after > MAX_ZOOM) {
                map.setZoom(before);
                return;
            }

            GeoPosition pos = map.getCenterPosition();
            double lat = pos.getLatitude();
            double lon = pos.getLongitude();

            double clampedLat = Math.max(MIN_LAT, Math.min(MAX_LAT, lat));
            double clampedLon = Math.max(MIN_LON, Math.min(MAX_LON, lon));

            if (lat != clampedLat || lon != clampedLon) {
                map.setCenterPosition(new GeoPosition(clampedLat, clampedLon));
            }
        });

        int initial = map.getZoom();
        if (initial < MIN_ZOOM) map.setZoom(MIN_ZOOM);
        if (initial > MAX_ZOOM) map.setZoom(MAX_ZOOM);

        for (ActionListener al : zoomIn.getActionListeners()) {
            zoomIn.removeActionListener(al);
        }
        for (ActionListener al : zoomOut.getActionListeners()) {
            zoomOut.removeActionListener(al);
        }

        // Button-based zoom control
        zoomIn.addActionListener(ev -> {
            int current = map.getZoom();
            if (current < MAX_ZOOM) {
                smoothSetZoom(map, current + 1);
            }
        });

        zoomOut.addActionListener(ev -> {
            int current = map.getZoom();
            if (current > MIN_ZOOM) {
                smoothSetZoom(map, current - 1);
            }
        });

        map.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseDragged(java.awt.event.MouseEvent e) {
                GeoPosition pos = map.getCenterPosition();
                double lat = pos.getLatitude();
                double lon = pos.getLongitude();

                double clampedLat = Math.max(MIN_LAT, Math.min(MAX_LAT, lat));
                double clampedLon = Math.max(MIN_LON, Math.min(MAX_LON, lon));

                if (lat != clampedLat || lon != clampedLon) {
                    map.setCenterPosition(new GeoPosition(clampedLat, clampedLon));
                }
            }
        });

        add(mapKit, BorderLayout.CENTER);
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
            if (radius < 5) {
                radius = 5;
            }

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

    // Simple stepped zoom change
    private void smoothSetZoom(JXMapViewer map, int targetZoom) {
        int z = map.getZoom();
        if (z == targetZoom) return;
        map.setZoom(targetZoom);
        map.repaint();
    }
}
