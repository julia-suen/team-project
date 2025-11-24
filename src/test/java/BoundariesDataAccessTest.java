import org.junit.jupiter.api.Test;
import data_access.BoundariesDataAccess;
import java.util.List;
import org.jxmapviewer.viewer.GeoPosition;

public class BoundariesDataAccessTest {

    @Test
    // Test if boundaries parsing is correct
    public void testLoadOneProvince() throws Exception {
        BoundariesDataAccess dao = new BoundariesDataAccess();
        // Polygon Test
        System.out.println(dao.getBoundariesData("Ontario"));
        // MultiPolygon Test
        List<List<GeoPosition>> bcBoundaries = dao.getBoundariesData("British Columbia");
        System.out.println("BC");
        System.out.println(bcBoundaries);
        System.out.println(bcBoundaries.size());
    }
}