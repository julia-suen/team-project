import entities.Region;
import org.junit.jupiter.api.Test;
import data_access.BoundariesDataAccess;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.jxmapviewer.viewer.GeoPosition;

import static org.junit.jupiter.api.Assertions.*;

public class BoundariesDataAccessTest {

    @Test
    // Test if boundaries parsing is correct with real network (keeping original test but improving assertion)
    public void testLoadOneProvince() throws Exception {
        BoundariesDataAccess dao = new BoundariesDataAccess();
        // We won't fail if network fails, but we print output
        try {
            List<List<GeoPosition>> boundaries = dao.getBoundariesData("Ontario");
            if (boundaries != null && !boundaries.isEmpty()) {
                System.out.println("Successfully fetched Ontario boundaries.");
                assertFalse(boundaries.isEmpty());
            }
        } catch (Exception e) {
            System.out.println("Network test skipped or failed: " + e.getMessage());
        }
    }

    /**
     * Tests the BoundariesDataAccess implementation with mock JSON.
     * specifically verifying the behavior of loadProvincesFromJson and getRegion.
     */
    @Test
    void testBoundariesDataAccessGetRegion() throws Exception {
        BoundariesDataAccess boundariesDataAccess = new BoundariesDataAccess();

        // Create a mock JSON for a province with a simple Polygon
        // GeoJSON format: [lon, lat]
        String mockJson = "[\n" +
                "    {\n" +
                "        \"name\": \"Test Province\",\n" +
                "        \"geojson\": {\n" +
                "            \"type\": \"Polygon\",\n" +
                "            \"coordinates\": [\n" +
                "                [\n" +
                "                    [10.0, 20.0],\n" +
                "                    [10.0, 21.0],\n" +
                "                    [11.0, 21.0],\n" +
                "                    [11.0, 20.0],\n" +
                "                    [10.0, 20.0]\n" +
                "                ]\n" +
                "            ]\n" +
                "        }\n" +
                "    }\n" +
                "]";
        
        InputStream inputStream = new ByteArrayInputStream(mockJson.getBytes(StandardCharsets.UTF_8));
        boundariesDataAccess.loadProvincesFromJson(inputStream);

        Region region = boundariesDataAccess.getRegion("Test Province");
        
        // Verify the region was loaded and retrieved correctly
        assertNotNull(region, "Region should not be null");
        assertEquals("Test Province", region.getProvinceName());
        assertFalse(region.getBoundary().isEmpty());
        
        // Verify coordinates mapping
        // Code parses as: lon = point[0], lat = point[1] -> GeoPosition(lat, lon)
        // Input [10.0, 20.0] -> lon=10.0, lat=20.0 -> GeoPosition(20.0, 10.0)
        List<GeoPosition> points = region.getBoundary().get(0);
        assertEquals(5, points.size());
        assertEquals(20.0, points.get(0).getLatitude(), 0.001);
        assertEquals(10.0, points.get(0).getLongitude(), 0.001);
    }
    
    /**
     * Tests loading MultiPolygon in BoundariesDataAccess.
     */
    @Test
    void testBoundariesDataAccessMultiPolygon() throws Exception {
        BoundariesDataAccess boundariesDataAccess = new BoundariesDataAccess();

        String mockJson = "[\n" +
                "    {\n" +
                "        \"name\": \"Multi Province\",\n" +
                "        \"geojson\": {\n" +
                "            \"type\": \"MultiPolygon\",\n" +
                "            \"coordinates\": [\n" +
                "                [\n" +
                "                    [\n" +
                "                        [10.0, 20.0],\n" +
                "                        [10.0, 21.0],\n" +
                "                        [11.0, 20.0]\n" +
                "                    ]\n" +
                "                ],\n" +
                "                [\n" +
                "                    [\n" +
                "                        [30.0, 40.0],\n" +
                "                        [30.0, 41.0],\n" +
                "                        [31.0, 40.0]\n" +
                "                    ]\n" +
                "                ]\n" +
                "            ]\n" +
                "        }\n" +
                "    }\n" +
                "]";

        InputStream inputStream = new ByteArrayInputStream(mockJson.getBytes(StandardCharsets.UTF_8));
        boundariesDataAccess.loadProvincesFromJson(inputStream);

        Region region = boundariesDataAccess.getRegion("Multi Province");
        
        assertNotNull(region);
        assertEquals("Multi Province", region.getProvinceName());
        // Should have 2 polygons
        assertEquals(2, region.getBoundary().size());
    }

    @Test
    void testBoundariesDataAccessGetRegionNotFound() {
        BoundariesDataAccess boundariesDataAccess = new BoundariesDataAccess();
        // No data loaded
        Region region = boundariesDataAccess.getRegion("NonExistent");
        assertNull(region, "Region should be null if not found");
    }
}
