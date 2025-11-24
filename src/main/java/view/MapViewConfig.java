package view;

import java.awt.Color;
import java.awt.Font;
import org.jxmapviewer.viewer.GeoPosition;

/**
 * A record to hold static configuration values for the MapView.
 */
public final class MapViewConfig {

    /** Initial zoom level of the map. */
    public static final int INITIAL_ZOOM = 12;
    /** Width of the province display label. */
    public static final int LABEL_WIDTH = 250;
    /** Height of the province display label. */
    public static final int LABEL_HEIGHT = 40;
    /** X-offset for the province display label. */
    public static final int LABEL_X_OFFSET = 20;
    /** Y-offset for the province display label. */
    public static final int LABEL_Y_OFFSET = 20;
    /** Font size for the province display label. */
    public static final int LABEL_FONT_SIZE = 14;
    /** Size of the zoom buttons. */
    public static final int BUTTON_SIZE = 40;
    /** Initial width of the map component. */
    public static final int INITIAL_WIDTH = 800;
    /** Initial height of the map component. */
    public static final int INITIAL_HEIGHT = 600;
    /** Stroke width for drawing region boundaries. */
    public static final float BOUNDARY_STROKE_WIDTH = 3.0f;
    /** Stroke width for drawing fire markers. */
    public static final float FIRE_STROKE_WIDTH = 2.0f;
    /** Minimum pixel radius for a fire marker. */
    public static final int MIN_FIRE_RADIUS = 5;
    /** Tolerance for map center position changes to trigger a reposition. */
    public static final double CENTER_POSITION_TOLERANCE = 1.0;
    /** Minimum number of points required to form a valid polygon. */
    public static final int MIN_POLYGON_POINTS = 3;

    /** Minimum latitude for map bounds. */
    public static final double MIN_LAT = 25.0;
    /** Maximum latitude for map bounds. */
    public static final double MAX_LAT = 75.0;
    /** Minimum longitude for map bounds. */
    public static final double MIN_LON = -170.0;
    /** Maximum longitude for map bounds. */
    public static final double MAX_LON = -50.0;
    /** Initial geographical position of the map center. */
    public static final GeoPosition INITIAL_POSITION = new GeoPosition(43.6532, -79.3832);

    /** Background color of the map. */
    public static final Color MAP_BACKGROUND_COLOR = new Color(181, 208, 208);
    /** Background color of the province display label. */
    public static final Color LABEL_BACKGROUND_COLOR = new Color(255, 255, 255, 200);
    /** Color of the selected region boundary. */
    public static final Color BOUNDARY_COLOR = new Color(0, 102, 204, 200);
    /** Fill color for fire markers. */
    public static final Color FIRE_FILL_COLOR = new Color(255, 0, 0, 100);

    /** Font for the province display label. */
    public static final Font LABEL_FONT = new Font("SansSerif", Font.BOLD, LABEL_FONT_SIZE);

    private MapViewConfig() {
        // Private constructor to prevent instantiation.
    }
}
