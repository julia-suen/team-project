package data_access;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import entities.Region;
import org.json.JSONArray;
import org.json.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jxmapviewer.viewer.GeoPosition;


public class BoundariesDataAccess {
    private static String[] PROVINCES = {
            "Ontario", "Quebec", "British Columbia", "Alberta", "Manitoba", "Saskatchewan",
            "Nova Scotia", "New Brunswick", "Prince Edward Island", "Newfoundland and Labrador",
            "Yukon", "Nunavut", "Northwest Territories"
    };

    private static Map<String, String> provincesToApi = new HashMap<String, String>() {{
        put("Ontario", "ontario");
        put("Quebec", "quebec");
        put("British Columbia", "british+columbia");
        put("Alberta", "alberta");
        put("Manitoba", "manitoba");
        put("Saskatchewan", "saskatchewan");
        put("Nova Scotia", "nova+scotia");
        put("New Brunswick", "new+brunswick");
        put("Prince Edward Island", "prince+edward+island");
        put("Newfoundland and Labrador", "newfoundland+and+labrador");
        put("Yukon", "yukon");
        put("Nunavut", "nunavut");
        put("Northwest Territories",  "northwest+territories");
    }};
    private final Map<String, Region> provincesToRegionMap = new HashMap<>();

    private final OkHttpClient client = new OkHttpClient();

    public Region getRegion(String provinceName) {
        return provincesToRegionMap.get(provinceName);
    }

    public void loadProvinces() throws getData.InvalidDataException {
        for(String prov : PROVINCES) {
            List<GeoPosition> boundary = getBoundariesData(prov);
            Region region = new Region(prov, boundary);
            provincesToRegionMap.put(prov, region);
        }
    }

    public List<GeoPosition> getBoundariesData(String provinceName) throws getData.InvalidDataException{
        String provinceNameAPI =  provincesToApi.get(provinceName);
        Request request = new Request.Builder().url("https://nominatim.openstreetmap.org/search?q="+provinceNameAPI+"+canada&format=json&polygon_geojson=1&polygon_threshold=0.1").build();
        try (Response response = client.newCall(request).execute()) {
            JSONArray responseArray = new JSONArray(response.body().string());
            // parse JSON response and return Java ArrayList
            JSONObject polygonObj = responseArray.getJSONObject(0).getJSONObject("geojson");
            JSONArray coordinatesArray = polygonObj.getJSONArray("coordinates").getJSONArray(0);
            return parseJsonCoordinates(coordinatesArray);
        // throw InvalidDataException if any error occurs when fetching boundaries data from API
        }catch (Exception e) {
            throw new getData.InvalidDataException();
        }

    }

    // helper of getBoundariesData to parse JSON
    public static List<GeoPosition> parseJsonCoordinates(JSONArray coordinatesArray){
        List<GeoPosition> polygon = new ArrayList<>();
        for(int i = 0; i < coordinatesArray.length(); i++){
            double lon = coordinatesArray.getJSONArray(i).getDouble(0);
            double lat = coordinatesArray.getJSONArray(i).getDouble(1);
            GeoPosition coord = new GeoPosition(lat, lon);
            polygon.add(coord);
        }
        return polygon;
    }
}
