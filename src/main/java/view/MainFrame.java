package view;

import interface_adapter.marker.MarkerViewModel;
import interface_adapter.region.RegionRepository;

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
     * @param regionRepository The repository containing loaded region data.
     */
    public MainFrame(RegionRepository regionRepository) {
        super("Canada Wildfire Data Viewer");
        MarkerViewModel markerViewModel = new MarkerViewModel();
        mapView = new MapView(regionRepository, markerViewModel);
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
