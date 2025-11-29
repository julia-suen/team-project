package view;

import data_access.BoundariesDataAccess;

import java.awt.BorderLayout;

import javax.swing.JFrame;

/**
 * The Main Window Frame.
 */
public class MainFrame extends JFrame {

    private static final int FRAME_WIDTH = 1000;
    private static final int FRAME_HEIGHT = 800;
    private final MapView mapView;
    private final SidePanelView sidePanelView;

    /**
     * Constructs the Main Frame.
     */
    public MainFrame(BoundariesDataAccess boundariesDataAccess) {
        super("Canada Wildfire Data Viewer");

        mapView = new MapView(boundariesDataAccess);
        sidePanelView = new SidePanelView();

        setLayout(new BorderLayout());

        add(mapView, BorderLayout.CENTER);
        add(sidePanelView, BorderLayout.EAST);

        setSize(FRAME_WIDTH, FRAME_HEIGHT);
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
