package view;

import java.awt.BorderLayout;

import javax.swing.JFrame;

/**
 * The Main Window Frame.
 */
public class MainFrame extends JFrame {

    private final MapView mapView;
    private final SidePanelView sidePanelView;

    /**
     * Constructs the Main Frame.
     */
    public MainFrame() {
        super("Canada Wildfire Data Viewer");

        mapView = new MapView();
        sidePanelView = new SidePanelView();

        setLayout(new BorderLayout());

        add(mapView, BorderLayout.CENTER);
        add(sidePanelView, BorderLayout.EAST);

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
}