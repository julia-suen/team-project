package view;

import javax.swing.JFrame;
import java.awt.BorderLayout;

public class MainFrame extends JFrame {

  private final MapView mapView;
  private final SidePanelView sidePanelView;

  public MainFrame() {
    super("Canada Wildfire Data Viewer");

    mapView = new MapView();
    sidePanelView = new SidePanelView();

    setLayout(new BorderLayout());

    add(mapView, BorderLayout.CENTER);
    add(sidePanelView, BorderLayout.EAST);

    setSize(900, 700);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
  }

  public MapView getMapView() {
    return mapView;
  }

  public SidePanelView getSidePanelView() {
    return sidePanelView;
  }
}
