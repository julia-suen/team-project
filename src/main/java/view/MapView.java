package view;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.*;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.JXMapKit;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;

public class MapView extends JPanel {

	private final JXMapKit mapKit;
	private final Set<FireWaypoint> waypoints;
	private final WaypointPainter<FireWaypoint> waypointPainter;

	public MapView() {
		// Initialize waypoint fields
		waypoints = new HashSet<>();
		waypointPainter = new WaypointPainter<>();
		waypointPainter.setRenderer(new FireWaypointRenderer());
		waypointPainter.setWaypoints(waypoints);

		setLayout(new BorderLayout());

		mapKit = new JXMapKit();

		// Use OpenStreetMap tiles
		OSMTileFactoryInfo info = new OSMTileFactoryInfo();
		DefaultTileFactory tileFactory = new DefaultTileFactory(info);
		mapKit.setTileFactory(tileFactory);

		// Set the painter as an overlay
		mapKit.getMainMap().setOverlayPainter(waypointPainter);

		// Adjust buttons size for zoom in/out
		Dimension buttonSize = new Dimension(40, 40);
		JButton zoomIn = mapKit.getZoomInButton();
		JButton zoomOut = mapKit.getZoomOutButton();
		zoomIn.setPreferredSize(buttonSize);
		zoomOut.setPreferredSize(buttonSize);

		// Center the map on Canada
		GeoPosition canada = new GeoPosition(56.1304, -106.3468);
		mapKit.setAddressLocation(canada);
		mapKit.setZoom(4);

		// Add mapKit to this JPanel
		add(mapKit, BorderLayout.CENTER);
	}

	public void addFireMarker(GeoPosition location, double radiusInMeters) {
		// Create the custom waypoint with the radius
		waypoints.add(new FireWaypoint(location, radiusInMeters));

		// Update the painter and repaint the map
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

	/**
	 * Custom Renderer to draw the Red Circle
	 */
	public class FireWaypointRenderer implements WaypointRenderer<FireWaypoint> {

		@Override
		public void paintWaypoint(Graphics2D g, JXMapViewer map, FireWaypoint wp) {
			// Convert GeoPosition to a point on the screen
			Point2D centerPoint = map.getTileFactory().geoToPixel(wp.getPosition(), map.getZoom());

			double distanceDegrees = wp.getRadius() / 111111.0;     // Approximation: 1 degree latitude ~ 111111 meters

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
			Point2D localPoint = map.getTileFactory().geoToPixel(wp.getPosition(), map.getZoom());

			int x = (int) (localPoint.getX() - radius);
			int y = (int) (localPoint.getY() - radius);

			g.setColor(new Color(255, 0, 0, 100)); // Red with transparency (Alpha 100)
			g.fillOval(x, y, diameter, diameter);

			g.setColor(Color.RED); // Solid red border
			g.setStroke(new BasicStroke(2));
			g.drawOval(x, y, diameter, diameter);
		}
	}
}
