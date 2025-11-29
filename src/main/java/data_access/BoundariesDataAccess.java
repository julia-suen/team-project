package data_access;

import entities.Region;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jxmapviewer.viewer.GeoPosition;

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
        "Yukon", "Nunavut", "Northwest Territories", "Canada",
    };

    private static final Map<String, String> PROVINCES_TO_API = new HashMap<>() { 
        {
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
        } 
    };
    
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
            final List<List<GeoPosition>> boundary = getBoundariesData(prov);
            final Region region = new Region(prov, boundary);
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
        final String provinceNameAPI = PROVINCES_TO_API.get(provinceName);
        final String url = String.format(API_URL_TEMPLATE, provinceNameAPI);
        final Request request = new Request.Builder().url(url).build();

        try (Response response = this.client.newCall(request).execute()) {
            final ResponseBody responseBody = response.body();
            if (responseBody == null) {
                throw new GetData.InvalidDataException("Response body was null.");
            }
            final JSONArray responseArray = new JSONArray(responseBody.string());
            return parseResponse(responseArray);
        } 
        catch (IOException error) {
            throw new GetData.InvalidDataException("API call failed: " + error.getMessage());
        }
    }

    /**
     * Parses the JSON response from the Nominatim API into a list of polygons.
     *
     * @param responseArray The JSON array received from the API.
     * @return A list of polygons representing the region's boundaries.
     */
    private List<List<GeoPosition>> parseResponse(final JSONArray responseArray) {
        final List<List<GeoPosition>> boundaries = new ArrayList<>();
        if (responseArray.isEmpty()) {
            return boundaries;
        }

        final JSONObject geoJsonObj = responseArray.getJSONObject(0).getJSONObject(GEOJSON_KEY);
        final String polygonType = geoJsonObj.getString(TYPE_KEY);
        final JSONArray coords = geoJsonObj.getJSONArray(COORDINATES_KEY);

        if (POLYGON_TYPE.equals(polygonType)) {
            final JSONArray outerRing = coords.getJSONArray(0);
            boundaries.add(parsePolygon(outerRing));
        } 
        else if (MULTIPOLYGON_TYPE.equals(polygonType)) {
            for (int i = 0; i < coords.length(); i++) {
                final JSONArray jsonPolygon = coords.getJSONArray(i);
                final List<GeoPosition> polygon = parsePolygon(jsonPolygon.getJSONArray(0));
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
        final List<GeoPosition> polygon = new ArrayList<>();
        for (int i = 0; i < ringArray.length(); i++) {
            final JSONArray point = ringArray.getJSONArray(i);
            final double lon = point.getDouble(0);
            final double lat = point.getDouble(1);
            polygon.add(new GeoPosition(lat, lon));
        }
        return polygon;
    }

    private static final Map<String, GeoPosition> PROVINCE_CENTERS = new HashMap<>() {
        {
            put("Alberta", new GeoPosition(53.9333, -116.5765));
            put("British Columbia", new GeoPosition(53.7267, -127.6476));
            put("Manitoba", new GeoPosition(53.7609, -98.8139));
            put("New Brunswick", new GeoPosition(46.5653, -66.4619));
            put("Newfoundland and Labrador", new GeoPosition(53.1355, -57.6604));
            put("Northwest Territories", new GeoPosition(64.8255, -124.8457));
            put("Nova Scotia", new GeoPosition(44.6820, -63.7443));
            put("Nunavut", new GeoPosition(70.2998, -83.1076));
            put("Ontario", new GeoPosition(51.2538, -85.3232));
            put("Prince Edward Island", new GeoPosition(46.5107, -63.4168));
            put("Quebec", new GeoPosition(52.9399, -73.5491));
            put("Saskatchewan", new GeoPosition(52.9399, -106.4509));
            put("Yukon", new GeoPosition(64.2823, -135.0000));
            put("Canada", new GeoPosition(56.1304, -106.3468));
        }
    };

    /**
     * Get province centre for marking favourites.
     * @param provinceName for name of Province
     */
    public static GeoPosition getProvinceCentre(String provinceName) {
        return PROVINCE_CENTERS.get(provinceName);
    }

    /**
     * Get province name from GeoPosition.
     * @param position for geoposition
     * @return null if position not found
     */
    public static String getProvinceName(GeoPosition position) {
        final double tolerance = 0.0001;
        for (Map.Entry<String, GeoPosition> entry : PROVINCE_CENTERS.entrySet()) {
            final GeoPosition centre = entry.getValue();
            if (Math.abs(centre.getLatitude() - position.getLatitude()) < tolerance
                    && Math.abs(centre.getLongitude() - position.getLongitude()) < tolerance) {
                return entry.getKey();
            }
        }
        return null;
    }
}
