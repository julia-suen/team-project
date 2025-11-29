package view;

import java.awt.Color;
import java.awt.Font;
import org.jxmapviewer.viewer.GeoPosition;

/**
 * A record to hold static configuration values for the MapView.
 */
public final class MapViewConfig {

    public static final int INITIAL_ZOOM = 12;
    public static final int LABEL_WIDTH = 250;
    public static final int LABEL_HEIGHT = 40;
    public static final int LABEL_X_OFFSET = 20;
    public static final int LABEL_Y_OFFSET = 20;
    public static final int LABEL_FONT_SIZE = 14;
    public static final int BUTTON_SIZE = 40;
    public static final int INITIAL_WIDTH = 800;
    public static final int INITIAL_HEIGHT = 600;
    public static final float BOUNDARY_STROKE_WIDTH = 3.0f;
    public static final float FIRE_STROKE_WIDTH = 2.0f;
    public static final int MIN_FIRE_RADIUS = 5;
    public static final double CENTER_POSITION_TOLERANCE = 1.0;
    public static final int MIN_POLYGON_POINTS = 3;

    public static final double MIN_LAT = 25.0;
    public static final double MAX_LAT = 75.0;
    public static final double MIN_LON = -170.0;
    public static final double MAX_LON = -50.0;
    public static final GeoPosition INITIAL_POSITION = new GeoPosition(43.6532, -79.3832);

    public static final Color MAP_BACKGROUND_COLOR = new Color(181, 208, 208);
    public static final Color LABEL_BACKGROUND_COLOR = new Color(255, 255, 255, 200);
    public static final Color BOUNDARY_COLOR = new Color(0, 102, 204, 200);
    public static final Color FIRE_FILL_COLOR = new Color(255, 0, 0, 100);

    public static final Font LABEL_FONT = new Font("SansSerif", Font.BOLD, LABEL_FONT_SIZE);

    private MapViewConfig() {
        // Private constructor to prevent instantiation.
    }
}
