package data_access;

import entities.Region;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jxmapviewer.viewer.GeoPosition;
import java.util.ArrayList;

/**
 * Data access object for fetching geographical boundary data from the Nominatim API.
 */
public class BoundariesDataAccess {
    private static final String API_URL_TEMPLATE =
            "https://nominatim.openstreetmap.org/search?q=%s+canada&format=json&polygon_geojson=1&polygon_threshold=0.1";
    private static final String GEOJSON_KEY = "geojson";
    private static final String TYPE_KEY = "type";
    private static final String COORDINATES_KEY = "coordinates";
    private static final String POLYGON_TYPE = "Polygon";
    private static final String MULTIPOLYGON_TYPE = "MultiPolygon";

    public static final String[] PROVINCES = {
            "Ontario", "Quebec", "British Columbia", "Alberta", "Manitoba", "Saskatchewan",
            "Nova Scotia", "New Brunswick", "Prince Edward Island", "Newfoundland and Labrador",
            "Yukon", "Nunavut", "Northwest Territories", "Canada"
    };

    private static final Map<String, String> PROVINCES_TO_API = new HashMap<>() { {
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
        put("Northwest Territories", "northwest+territories");
        put("Canada", "canada");
    } };


    private final Map<String, Region> provincesToRegionMap = new HashMap<>();
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Retrieves a cached {@link Region} object by its province name.
     * This method does not fetch new data; it only accesses previously loaded regions.
     *
     * @param provinceName The name of the province.
     * @return The cached {@link Region}, or null if not found.
     */
    public Region getRegion(final String provinceName) {
        return this.provincesToRegionMap.get(provinceName);
    }

    /**
     * Loads the boundary data for all Canadian provinces and territories.
     * This method iterates through a predefined list of provinces, fetches the
     * boundary data for each, and stores it in a local cache.
     *
     * @throws GetData.InvalidDataException if an error occurs during the API request or data parsing.
     */
    public void loadProvinces() throws GetData.InvalidDataException {
        for (String prov : PROVINCES) {
            List<List<GeoPosition>> boundary = getBoundariesData(prov);
            Region region = new Region(prov, boundary);
            this.provincesToRegionMap.put(prov, region);
        }
    }

    /**
     * Fetches and parses the boundaries data for a single province from the Nominatim API.
     *
     * @param provinceName The name of the province to fetch.
     * @return A list of polygons, where each polygon is a list of {@link GeoPosition} points.
     * @throws GetData.InvalidDataException if the API call fails or the response is invalid.
     */
    public List<List<GeoPosition>> getBoundariesData(final String provinceName) throws GetData.InvalidDataException {
        String provinceNameAPI = PROVINCES_TO_API.get(provinceName);
        String url = String.format(API_URL_TEMPLATE, provinceNameAPI);
        Request request = new Request.Builder().url(url).build();

        try (Response response = this.client.newCall(request).execute()) {
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                throw new GetData.InvalidDataException("Response body was null.");
            }
            JSONArray responseArray = new JSONArray(responseBody.string());
            return parseResponse(responseArray);
        } catch (IOException e) {
            throw new GetData.InvalidDataException("API call failed: " + e.getMessage());
        }
    }

    /**
     * Parses the JSON response from the Nominatim API into a list of polygons.
     *
     * @param responseArray The JSON array received from the API.
     * @return A list of polygons representing the region's boundaries.
     */
    private List<List<GeoPosition>> parseResponse(final JSONArray responseArray) {
        List<List<GeoPosition>> boundaries = new ArrayList<>();
        if (responseArray.isEmpty()) {
            return boundaries;
        }

        JSONObject geoJsonObj = responseArray.getJSONObject(0).getJSONObject(GEOJSON_KEY);
        String polygonType = geoJsonObj.getString(TYPE_KEY);
        JSONArray coords = geoJsonObj.getJSONArray(COORDINATES_KEY);

        if (POLYGON_TYPE.equals(polygonType)) {
            JSONArray outerRing = coords.getJSONArray(0);
            boundaries.add(parsePolygon(outerRing));
        } else if (MULTIPOLYGON_TYPE.equals(polygonType)) {
            for (int i = 0; i < coords.length(); i++) {
                JSONArray jsonPolygon = coords.getJSONArray(i);
                List<GeoPosition> polygon = parsePolygon(jsonPolygon.getJSONArray(0));
                boundaries.add(polygon);
            }
        }
        return boundaries;
    }

    /**
     * A helper method to parse a JSON array of coordinates into a list of {@link GeoPosition} objects.
     *
     * @param ringArray The JSON array representing a single polygon ring.
     * @return A list of {@link GeoPosition} points.
     */
    private static List<GeoPosition> parsePolygon(final JSONArray ringArray) {
        List<GeoPosition> polygon = new ArrayList<>();
        for (int i = 0; i < ringArray.length(); i++) {
            JSONArray point = ringArray.getJSONArray(i);
            double lon = point.getDouble(0);
            double lat = point.getDouble(1);
            polygon.add(new GeoPosition(lat, lon));
        }
        return polygon;
    }
}
