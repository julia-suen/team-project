import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import entities.Region;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactory;
import view.RegionBoundaryPainter;

class RegionBoundaryPainterTest {

    private JXMapViewer mapViewer;
    private RegionBoundaryPainter regionBoundaryPainter;
    private Region region;

    @BeforeEach
    void setUp() {
        // Mock JXMapViewer and its dependencies
        mapViewer = mock(JXMapViewer.class);
        TileFactory tileFactory = mock(TileFactory.class);

        // Set up the map viewer mock
        when(mapViewer.getViewportBounds()).thenReturn(new Rectangle(0, 0, 1000, 1000));
        when(mapViewer.getTileFactory()).thenReturn(tileFactory);
        when(mapViewer.getZoom()).thenReturn(10);

        // Define a sample region for testing
        List<GeoPosition> boundary = List.of(
                new GeoPosition(50, 10),
                new GeoPosition(50, 20),
                new GeoPosition(60, 20),
                new GeoPosition(60, 10)
        );
        region = new Region("Test Region", List.of(boundary));

        // Initialize the painter with the test region
        regionBoundaryPainter = new RegionBoundaryPainter(region);

        // Mock the geo-to-pixel conversion
        when(tileFactory.geoToPixel(new GeoPosition(50, 10), 10)).thenReturn(new Point2D.Double(100, 500));
        when(tileFactory.geoToPixel(new GeoPosition(50, 20), 10)).thenReturn(new Point2D.Double(200, 500));
        when(tileFactory.geoToPixel(new GeoPosition(60, 20), 10)).thenReturn(new Point2D.Double(200, 400));
        when(tileFactory.geoToPixel(new GeoPosition(60, 10), 10)).thenReturn(new Point2D.Double(100, 400));
    }

    @Test
    void isPointInside_WhenPointIsInside_ShouldReturnTrue() {
        // Test a point that is clearly inside the region
        Point pointInside = new Point(150, 450);
        assertTrue(regionBoundaryPainter.isPointInside(pointInside, mapViewer),
                "The point should be inside the region.");
    }

    @Test
    void isPointInside_WhenPointIsOutside_ShouldReturnFalse() {
        // Test a point that is clearly outside the region
        Point pointOutside = new Point(50, 50);
        assertFalse(regionBoundaryPainter.isPointInside(pointOutside, mapViewer),
                "The point should be outside the region.");
    }

    @Test
    void isPointInside_WhenPointIsOnBoundary_ShouldReturnTrue() {
        // Test a point that is on the boundary of the region
        Point pointOnBoundary = new Point(100, 450);
        assertTrue(regionBoundaryPainter.isPointInside(pointOnBoundary, mapViewer),
                "The point on the boundary should be considered inside.");
    }

    @Test
    void isPointInside_WhenNoRegionIsSet_ShouldReturnFalse() {
        // Test when no region is set in the painter
        regionBoundaryPainter.setRegion(null);
        Point anyPoint = new Point(150, 450);
        assertFalse(regionBoundaryPainter.isPointInside(anyPoint, mapViewer),
                "Should return false when no region is set.");
    }
}
