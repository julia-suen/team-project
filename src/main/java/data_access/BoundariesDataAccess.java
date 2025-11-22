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
    private static final String[] PROVINCES = {
            "Ontario", "Quebec", "British Columbia", "Alberta", "Manitoba", "Saskatchewan",
            "Nova Scotia", "New Brunswick", "Prince Edward Island", "Newfoundland and Labrador",
            "Yukon", "Nunavut", "Northwest Territories"
    };

    private static final Map<String, String> provincesToApi = new HashMap<>() {{
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
        // When program starts, this method is used to fetch the boundaries for all 13 provinces
        // Data is then stored in the HashMap provincesToRegionMap
        // When we need to which province the fire belongs, use the HashMap instead of calling API again
        for(String prov : PROVINCES) {
            List<List<GeoPosition>> boundary = getBoundariesData(prov);
            Region region = new Region(prov, boundary);
            provincesToRegionMap.put(prov, region);
        }
    }

    public List<List<GeoPosition>> getBoundariesData(String provinceName) throws getData.InvalidDataException{
        // fetch provinces' boundaries data from Nominatim API
        // returns boundaries/polygon(s) for one province
        String provinceNameAPI =  provincesToApi.get(provinceName);
        Request request = new Request.Builder().url("https://nominatim.openstreetmap.org/search?q="+provinceNameAPI+"+canada&format=json&polygon_geojson=1&polygon_threshold=0.1").build();
        try (Response response = client.newCall(request).execute()) {
            JSONArray responseArray = new JSONArray(response.body().string());
            // parse JSON response and return Java ArrayList
            List<List<GeoPosition>> boundaries = new ArrayList<>();
            JSONObject geoJsonObj = responseArray.getJSONObject(0).getJSONObject("geojson");
            String polygonType = geoJsonObj.getString("type");
            JSONArray coords = geoJsonObj.getJSONArray("coordinates");
            if (polygonType.equals("Polygon")){
                JSONArray outerRing = coords.getJSONArray(0);
                boundaries.add(parsePolygon(outerRing));
            }else if(polygonType.equals("MultiPolygon")){
                // MultiPolygon: the boundaries are made up of more than one polygon
                // Example: BC, Nunavut
                for (int i = 0; i < coords.length(); i++) {
                    JSONArray Jsonpolygon = coords.getJSONArray(i);
                    List<GeoPosition> polygon = parsePolygon(Jsonpolygon.getJSONArray(0));
                    boundaries.add(polygon);
                }
            }
            return boundaries;
        // throw InvalidDataException if any error occurs when fetching boundaries data from API
        }catch (Exception e) {
            throw new getData.InvalidDataException();
        }

    }

    private static List<GeoPosition> parsePolygon(JSONArray ringArray){
        // helper of getBoundariesData to parse JSON
        List<GeoPosition> polygon = new ArrayList<>();
        for(int i = 0; i < ringArray.length(); i++){
            double lon = ringArray.getJSONArray(i).getDouble(0);
            double lat = ringArray.getJSONArray(i).getDouble(1);
            GeoPosition coord = new GeoPosition(lat, lon);
            polygon.add(coord);
        }
        return polygon;
    }
}