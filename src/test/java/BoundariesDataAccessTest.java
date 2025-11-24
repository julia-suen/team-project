// import data_access.BoundariesDataAccess;
// import data_access.GetData;
// import entities.Region;
// import org.junit.jupiter.api.Test;
//
// import java.util.List;
//
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
//
// public class BoundariesDataAccessTest {
//
//     @Test
//     public void testLoadOneProvince() throws Exception {
//         BoundariesDataAccess dao = new BoundariesDataAccess();
//         List<List<org.jxmapviewer.viewer.GeoPosition>> boundaries = dao.getBoundariesData("Ontario");
//         assertNotNull(boundaries);
//
//         Region region = new Region("Ontario", boundaries);
//         assertEquals("Ontario", region.getProvinceName());
//         assertEquals(boundaries, region.getBoundary());
//     }
// }
