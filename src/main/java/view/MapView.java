package view;

import entities.Fire;
import interface_adapter.marker.MarkerPresenter;
import interface_adapter.marker.MarkerViewModel;
import interface_adapter.region.RegionRepository;
import interface_adapter.select_region.MapCoordinateConverter;
import interface_adapter.select_region.SelectRegionController;
import interface_adapter.select_region.SelectRegionPresenter;
import interface_adapter.select_region.SelectRegionViewModel;
import interface_adapter.marker.MarkerController;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serial;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import org.jxmapviewer.JXMapKit;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.WaypointPainter;
import use_case.marker.MarkerInputBoundary;
import use_case.marker.MarkerInteractor;
import use_case.marker.MarkerOutputBoundary;
import use_case.select_region.CoordinateConverter;
import use_case.select_region.SelectRegionInteractor;
import usecase.select_region.CoordinateConverter;
import usecase.select_region.SelectRegionInteractor;

/**
 * The main map view component for the application.
 */
public class MapView extends JPanel implements PropertyChangeListener {

    @Serial
    private static final long serialVersionUID = 1L;

    private final transient RegionRepository regionRepo;
    private final JLabel provinceLabel = new JLabel("Province: None");
    private final JXMapKit mapKit;
    private final transient Set<FireWaypoint> waypoints;
    private final transient Set<FireWaypoint> markers;
    private final transient WaypointPainter<FireWaypoint> waypointPainter;
    private final transient WaypointPainter<FireWaypoint> fireMarkerPainter;
    private final transient SelectRegionController selectRegionController;
    private final transient RegionBoundaryPainter regionBoundaryPainter;
    private final transient MarkerController markerController;
    private final transient MarkerViewModel markerViewModel;
    private final MarkerInfoPanel markerInfoPanel;
    private boolean firesDisplayed = false;

    /**
     * Constructs the MapView panel.
     * @param regionRepository The repository containing loaded region data.
     */
    public MapView(RegionRepository regionRepository) {
        this.waypoints = new HashSet<>();
        this.markers = new HashSet<>();
      
        // Decoupling: Use the injected repository instead of creating a new one
        this.regionRepo = regionRepository;

        this.waypointPainter = new WaypointPainter<>();
        this.waypointPainter.setRenderer(new FireWaypointRenderer());
        this.waypointPainter.setWaypoints(this.waypoints);

        this.fireMarkerPainter = new WaypointPainter<>();
        this.fireMarkerPainter.setRenderer(new FireMarkerRenderer());
        this.fireMarkerPainter.setWaypoints(this.markers);

        this.mapKit = new JXMapKit();
        final JXMapViewer map = this.mapKit.getMainMap();

        final SelectRegionViewModel selectRegionViewModel = new SelectRegionViewModel();
        selectRegionViewModel.addPropertyChangeListener(this);
        final SelectRegionPresenter selectRegionPresenter = new SelectRegionPresenter(selectRegionViewModel);
        final CoordinateConverter coordinateConverter = new MapCoordinateConverter(map);
        final SelectRegionInteractor selectRegionInteractor = new SelectRegionInteractor(
                this.regionRepo, selectRegionPresenter, coordinateConverter
        );
        this.selectRegionController = new SelectRegionController(selectRegionInteractor);

        // Build Marker Use Case
        this.markerViewModel = markerViewModel;
        markerViewModel.addPropertyChangeListener(this);
        final MarkerOutputBoundary markerOutputBoundary = new MarkerPresenter(markerViewModel);
        final MarkerInputBoundary markerInteractor = new MarkerInteractor(markerOutputBoundary);
        this.markerController = new MarkerController(markerInteractor);

        markerInfoPanel = new MarkerInfoPanel();
        markerInfoPanel.setBounds(560, 20, 160, 70);
        this.add(markerInfoPanel);

        this.regionBoundaryPainter = new RegionBoundaryPainter();

        this.setLayout(new BorderLayout());

        final JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);
        this.add(layeredPane, BorderLayout.CENTER);

        this.setupProvinceLabel(layeredPane);
        this.setupMapKit(layeredPane);

        this.regionRepo.addOnLoadCallback(this::repaint);

        SwingUtilities.invokeLater(this::updateChildBounds);
    }

    private void setupProvinceLabel(final JLayeredPane layeredPane) {
        this.provinceLabel.setOpaque(true);
        this.provinceLabel.setBackground(MapViewConfig.LABEL_BACKGROUND_COLOR);
        this.provinceLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        this.provinceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.provinceLabel.setFont(MapViewConfig.LABEL_FONT);
        layeredPane.add(this.provinceLabel, JLayeredPane.PALETTE_LAYER);
    }

    private void setupMapKit(final JLayeredPane layeredPane) {
        this.mapKit.setZoomSliderVisible(false);
        this.mapKit.setZoomButtonsVisible(true);

        final OSMTileFactoryInfo info = new OSMTileFactoryInfo();
        final DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        this.mapKit.setTileFactory(tileFactory);

        final JXMapViewer map = this.mapKit.getMainMap();
        map.setBackground(MapViewConfig.MAP_BACKGROUND_COLOR);
        map.setFocusable(true);
        map.requestFocusInWindow();

        this.addMouseListeners(map);

        final List<Painter<JXMapViewer>> painters = new ArrayList<>();
        painters.add(this.regionBoundaryPainter);
        painters.add(this.waypointPainter);
        painters.add(this.fireMarkerPainter);

        final CompoundPainter<JXMapViewer> compoundPainter = new CompoundPainter<>(painters);
        map.setOverlayPainter(compoundPainter);

        this.mapKit.setAddressLocation(MapViewConfig.INITIAL_POSITION);
        this.mapKit.setZoom(MapViewConfig.INITIAL_ZOOM);

        new MapBoundsEnforcer(map);
        this.setupZoomButtons(this.mapKit);

        layeredPane.add(this.mapKit, JLayeredPane.DEFAULT_LAYER);

        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(final java.awt.event.ComponentEvent e) {
                updateChildBounds();
            }
        });
    }

    private void updateChildBounds() {
        final int w = getWidth();
        final int h = getHeight();
        this.mapKit.setBounds(0, 0, w, h);
        this.provinceLabel.setBounds(
                MapViewConfig.LABEL_X_OFFSET, MapViewConfig.LABEL_Y_OFFSET,
                MapViewConfig.LABEL_WIDTH, MapViewConfig.LABEL_HEIGHT
        );
    }

    private void addMouseListeners(final JXMapViewer map) {
        map.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                if (!regionRepo.isLoaded()) {
                    return;
                }

                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) {
                    final GeoPosition clickPos = map.convertPointToGeoPosition(e.getPoint());
                    selectRegionController.execute(clickPos);
                } else if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e) && map.getZoom() > 0) {
                    map.setZoom(map.getZoom() - 1);
                    map.requestFocusInWindow();
                }
            }
        });

        final PanKeyListener panListener = new PanKeyListener(map);
        map.addKeyListener(panListener);

        final PanMouseInputListener mouseListener = new PanMouseInputListener(map);
        map.addMouseListener(mouseListener);
        map.addMouseMotionListener(mouseListener);

        map.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (firesDisplayed){
                    Fire fire = fireAtPoint(map, e.getX(), e.getY());
                    if (fire != null) {
                        markerController.execute(fire);
                        markerInfoPanel.setVisible(true);
                    }else{
                        markerInfoPanel.setVisible(false);
                    }
                }
            }
        });
    }

    private void setupZoomButtons(final JXMapKit kit) {
        final JButton zoomIn = kit.getZoomInButton();
        final JButton zoomOut = kit.getZoomOutButton();
        final Dimension buttonSize = new Dimension(MapViewConfig.BUTTON_SIZE, MapViewConfig.BUTTON_SIZE);
        zoomIn.setPreferredSize(buttonSize);
        zoomOut.setPreferredSize(buttonSize);
        zoomIn.addActionListener(event -> kit.getMainMap().requestFocusInWindow());
        zoomOut.addActionListener(event -> kit.getMainMap().requestFocusInWindow());
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if ("selectedProvince".equals(evt.getPropertyName())) {
            final String newProvince = (String) evt.getNewValue();
            this.provinceLabel.setText(SelectRegionViewModel.PROVINCE_LABEL + newProvince);
            final entities.Region selectedRegion = this.regionRepo.getRegion(newProvince);
            this.regionBoundaryPainter.setRegion(selectedRegion);
            this.repaint();
        }else if ("markerHover".equals(evt.getPropertyName())){
            displayMarkerDetails();
            this.repaint();
        }
    }

    /**
     * Adds a fire marker to the map.
     * @param location The geographical location of the fire.
     * @param radius The radius of the fire marker.
     */
    public void addFireMarker(final GeoPosition location, final double radius) {
        this.waypoints.add(new FireWaypoint(location, radius));
        this.markers.add(new FireWaypoint(location, radius));
        this.waypointPainter.setWaypoints(this.waypoints);
        this.fireMarkerPainter.setWaypoints(this.markers);
        this.mapKit.getMainMap().repaint();
    }

    public void clearFires() {
        this.waypoints.clear();
        this.waypointPainter.setWaypoints(this.waypoints);
        this.mapKit.getMainMap().repaint();
        this.firesDisplayed = false;
    }

    public void displayFires(final List<Fire> fires) {
        this.clearFires();
        for (final Fire fire : fires) {
            if (fire.getCoordinates() != null && !fire.getCoordinates().isEmpty()) {
                final GeoPosition geo = new GeoPosition(
                        fire.getCoordinates().get(0).getLat(),
                        fire.getCoordinates().get(0).getLon()
                );
                this.addFireMarker(geo, fire.getRadius(), fire);
            }
        }
        this.firesDisplayed = true;
    }

    private Fire fireAtPoint(JXMapViewer map, final double x, final double y) {
        for (FireWaypoint fireWaypoint: this.markers){
            Point2D marker = map.convertGeoPositionToPoint(fireWaypoint.getPosition());
            double dx = marker.getX() - x;
            double dy = marker.getY() - y;

            if (dx*dx + dy*dy < 8*8){
                return fireWaypoint.getFire();
            }
        }
        return null;
    }

    private void displayMarkerDetails() {
        this.markerInfoPanel.update(
                this.markerViewModel.getLat(),
                this.markerViewModel.getLon(),
                this.markerViewModel.getSize(),
                this.markerViewModel.getFrp()
        );
    }
}
