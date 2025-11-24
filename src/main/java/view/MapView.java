package view;

import interface_adapter.region.RegionRepository;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
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
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.WaypointPainter;
import org.jxmapviewer.viewer.WaypointRenderer;
import entities.Fire;

/**
 * The main map view component for the application.
 * Handles rendering the map, fire data, and region boundaries,
 * as well as user interactions like panning, zooming, and selecting provinces.
 */
public class MapView extends JPanel {

    @Serial
    private static final long serialVersionUID = 1L;

    private final transient RegionRepository regionRepo = new RegionRepository(
            new data_access.BoundariesDataAccess()
    );

    private final JLabel provinceLabel = new JLabel("Province: None");
    private final JXMapKit mapKit;
    private final transient Set<FireWaypoint> waypoints;
    private final transient WaypointPainter<FireWaypoint> waypointPainter;
    private final transient RegionSelectionHandler regionSelectionHandler;

    /**
     * Constructs the MapView panel.
     */
    public MapView() {
        this.waypoints = new HashSet<>();
        this.regionSelectionHandler = new RegionSelectionHandler(this.regionRepo, this.provinceLabel, this);

        this.waypointPainter = new WaypointPainter<>();
        this.waypointPainter.setRenderer(new FireWaypointRenderer());
        this.waypointPainter.setWaypoints(this.waypoints);

        this.setLayout(new BorderLayout());

        final JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);
        this.add(layeredPane, BorderLayout.CENTER);

        this.setupProvinceLabel(layeredPane);

        this.mapKit = new JXMapKit();
        this.setupMapKit(layeredPane);

        this.regionRepo.addOnLoadCallback(this::repaint);

        // Schedule the initial layout update to fix component positions on startup.
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
        painters.add(this.regionSelectionHandler.getRegionPainter());
        painters.add(this.waypointPainter);

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
        mapKit.setBounds(0, 0, w, h);
        provinceLabel.setBounds(
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
                    regionSelectionHandler.handleRegionSelection(e.getPoint(), map);
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

    /**
     * Adds a fire marker to the map.
     * @param location The geographical location of the fire.
     * @param radius The radius of the fire marker.
     */
    public void addFireMarker(final GeoPosition location, final double radius) {
        this.waypoints.add(new FireWaypoint(location, radius));
        this.waypointPainter.setWaypoints(this.waypoints);
        this.mapKit.getMainMap().repaint();
    }

    /**
     * Clears all fire markers from the map.
     */
    public void clearFires() {
        this.waypoints.clear();
        this.waypointPainter.setWaypoints(this.waypoints);
        this.mapKit.getMainMap().repaint();
    }

    /**
     * Displays a list of fires on the map, clearing any previous fires.
     * @param fires A list of {@link Fire} objects to display.
     */
    public void displayFires(final List<Fire> fires) {
        this.clearFires();
        for (final Fire fire : fires) {
            if (fire.getCoordinates() != null && !fire.getCoordinates().isEmpty()) {
                final GeoPosition geo = new GeoPosition(
                        fire.getCoordinates().get(0).getLatitude(),
                        fire.getCoordinates().get(0).getLongitude()
                );
                this.addFireMarker(geo, 0.1); // Assuming a default radius
            }
        }
    }

    /**
     * A custom waypoint representing a fire, with a specific radius.
     */
    public static class FireWaypoint extends DefaultWaypoint {
        private final double radius;

        /**
         * Constructs a FireWaypoint.
         * @param coord The geographical coordinate.
         * @param radius The radius of the fire.
         */
        public FireWaypoint(final GeoPosition coord, final double radius) {
            super(coord);
            this.radius = radius;
        }

        /**
         * Gets the radius of the fire.
         * @return the radius.
         */
        public double getRadius() {
            return this.radius;
        }
    }

    /**
     * A renderer for painting FireWaypoint objects on the map.
     */
    public static class FireWaypointRenderer implements WaypointRenderer<FireWaypoint> {
        @Override
        public void paintWaypoint(final Graphics2D g, final JXMapViewer map, final FireWaypoint wp) {
            final Point2D centerPoint = map.getTileFactory().geoToPixel(wp.getPosition(), map.getZoom());
            final GeoPosition northPoint = new GeoPosition(
                    wp.getPosition().getLatitude() + wp.getRadius(),
                    wp.getPosition().getLongitude()
            );
            final Point2D radiusPoint = map.getTileFactory().geoToPixel(northPoint, map.getZoom());

            final double pixelRadius = Math.abs(centerPoint.getY() - radiusPoint.getY());
            int radius = (int) pixelRadius;
            if (radius < MapViewConfig.MIN_FIRE_RADIUS) {
                radius = MapViewConfig.MIN_FIRE_RADIUS;
            }
            final int diameter = radius * 2;
            final int x = (int) (centerPoint.getX() - radius);
            final int y = (int) (centerPoint.getY() - radius);

            g.setColor(MapViewConfig.FIRE_FILL_COLOR);
            g.fillOval(x, y, diameter, diameter);
            g.setColor(Color.RED);
            g.setStroke(new BasicStroke(MapViewConfig.FIRE_STROKE_WIDTH));
            g.drawOval(x, y, diameter, diameter);
        }
    }
}
