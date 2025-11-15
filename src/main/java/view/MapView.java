package view;

import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.JXMapKit;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class MapView extends JPanel {

  private final JXMapKit mapKit;
  private final Set<Waypoint> waypoints;
  private final WaypointPainter<Waypoint> waypointPainter;

  public MapView() {
    // Initialize waypoint fields
    waypoints = new HashSet<>();
    waypointPainter = new WaypointPainter<>();
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

  public void addFireMarker(GeoPosition location) {
    waypoints.add(new DefaultWaypoint(location));
    waypointPainter.setWaypoints(waypoints);
    mapKit.getMainMap().repaint();
  }

  public JXMapKit getMapKit() {
    return mapKit;
  }
}
