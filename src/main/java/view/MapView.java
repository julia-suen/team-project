package view;

import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.JXMapKit;
import org.jxmapviewer.viewer.DefaultTileFactory;

import javax.swing.*;
import java.awt.*;

public class MapView extends JFrame {

  public MapView() {
    super("JXMapKit - Canada");

    // Create JXMapKit
    JXMapKit mapKit = new JXMapKit();

    // Use OpenStreetMap tiles
    OSMTileFactoryInfo info = new OSMTileFactoryInfo();
    DefaultTileFactory tileFactory = new DefaultTileFactory(info);
    mapKit.setTileFactory(tileFactory);

    // Adjust buttons size for zoom in/out
    Dimension buttonSize = new Dimension(40, 40);

    JButton zoomIn = mapKit.getZoomInButton();
    JButton zoomOut = mapKit.getZoomOutButton();

    zoomIn.setPreferredSize(buttonSize);
    zoomOut.setPreferredSize(buttonSize);

    // Center the map on Canada
    GeoPosition canada = new GeoPosition(56.1304, -106.3468);
    mapKit.setAddressLocation(canada);

    // Zoom level (0=world)
    mapKit.setZoom(4);

    // Add to window
    add(mapKit, BorderLayout.CENTER);

    setSize(900, 700);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    setVisible(true);
  }
}
