package view;

import javax.swing.JFrame;
import java.awt.BorderLayout;

public class MainFrame extends JFrame {

    private final MapView mapView;
    private final SidePanelView sidePanelView;
    private final GraphPanel graphPanel;

    public MainFrame() {
        super("Canada Wildfire Data Viewer");

        mapView = new MapView();
        sidePanelView = new SidePanelView();
        graphPanel = new GraphPanel();

        setLayout(new BorderLayout());

        add(mapView, BorderLayout.CENTER);
        add(sidePanelView, BorderLayout.EAST);
        add(graphPanel, BorderLayout.SOUTH); // Add Graph to bottom

        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public MapView getMapView() {
        return mapView;
    }

    public SidePanelView getSidePanelView() {
        return sidePanelView;
    }

    public GraphPanel getGraphPanel() {
        return graphPanel;
    }
}
