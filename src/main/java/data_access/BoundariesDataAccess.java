package data_access;

import entities.Province;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entities.Region;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jxmapviewer.viewer.GeoPosition;
import usecase.compare.CompareBoundaryDataAccess;
import usecase.load_fires.LoadFiresBoundaryDataAccess;
import usecase.national_overview.NationalOverviewBoundaryDataAccess;

/**
 * Data access object for fetching geographical boundary data from the Nominatim API.
 */
public class BoundariesDataAccess implements LoadFiresBoundaryDataAccess, NationalOverviewBoundaryDataAccess, CompareBoundaryDataAccess {
    private static final String API_URL_TEMPLATE =
            "https://nominatim.openstreetmap.org/search?q=%s+canada&format=json&polygon_geojson=1&polygon_threshold=0.1";
    private static final String NAME_KEY = "name";
    private static final String GEOJSON_KEY = "geojson";
    private static final String TYPE_KEY = "type";
    private static final String COORDINATES_KEY = "coordinates";
    private static final String POLYGON_TYPE = "Polygon";
    private static final String MULTIPOLYGON_TYPE = "MultiPolygon";

    private static final Map<Province, String> PROVINCES_TO_API = new HashMap<>() {
        {
            put(Province.ONTARIO, "ontario");
            put(Province.QUEBEC, "quebec");
            put(Province.BRITISH_COLUMBIA, "british+columbia");
            put(Province.ALBERTA, "alberta");
            put(Province.MANITOBA, "manitoba");
            put(Province.SASKATCHEWAN, "saskatchewan");
            put(Province.NOVA_SCOTIA, "nova+scotia");
            put(Province.NEW_BRUNSWICK, "new+brunswick");
            put(Province.PRINCE_EDWARD_ISLAND, "prince+edward+island");
            put(Province.NEWFOUNDLAND_AND_LABRADOR, "newfoundland+and+labrador");
            put(Province.YUKON, "yukon");
            put(Province.NUNAVUT, "nunavut");
            put(Province.NORTHWEST_TERRITORIES, "northwest+territories");
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
     * This method first tries to load from boundaries.json file in resources.
     * If the file is not found or fails to load, it falls back to fetching from the API.
     *
     * @throws GetFireData.InvalidDataException if an error occurs during loading or parsing.
     */
    public void loadProvinces() throws GetFireData.InvalidDataException {
        // Locate the local file in resources
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("boundaries.json");

        if (inputStream != null) {
            System.out.println("JSON file exists");
            loadProvincesFromJson(inputStream);
        } else {
            System.out.println("JSON file not found or failed to load. Falling back to API.");
            for (Province prov : Province.values()) {
                System.out.println(String.format("Started fetching boundaries data for %s.", prov.getDisplayName()));
                final List<List<GeoPosition>> boundary = getBoundariesData(prov);
                final Region region = new Region(prov.getDisplayName(), boundary);
                this.provincesToRegionMap.put(prov.getDisplayName(), region);
                System.out.println(String.format("Finished fetching boundaries data for %s.", prov.getDisplayName()));
            }
        }

        // For the entire Canada
        final Region allCanada = new Region("Canada", getBoundariesData("canada"));
        this.provincesToRegionMap.put("Canada", allCanada);
    }

    /**
     * Loads province boundary data from a JSON file input stream.
     *
     * @param inputStream The input stream containing the JSON file content.
     * @throws GetFireData.InvalidDataException if an error occurs during parsing.
     */
    public void loadProvincesFromJson(InputStream inputStream) throws GetFireData.InvalidDataException {
        String content;
        try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8)) {
            content = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";

            // Parse JSON
            JSONArray jsonArray = new JSONArray(content);
            System.out.println("Number of objects in JSON array: " + jsonArray.length());

            // Iterate through provinces and territories
            for (int i = 0; i < jsonArray.length(); i++) {
                final List<List<GeoPosition>> boundaries = new ArrayList<>();

                String name = jsonArray.getJSONObject(i).getString(NAME_KEY);
                JSONObject geoJsonObj = jsonArray.getJSONObject(i).getJSONObject(GEOJSON_KEY);
                final String polygonType = geoJsonObj.getString(TYPE_KEY);
                final JSONArray coords = geoJsonObj.getJSONArray(COORDINATES_KEY);

                if (POLYGON_TYPE.equals(polygonType)) {
                    final JSONArray outerRing = coords.getJSONArray(0);
                    boundaries.add(parsePolygon(outerRing));
                } else if (MULTIPOLYGON_TYPE.equals(polygonType)) {
                    for (int j = 0; j < coords.length(); j++) {
                        final JSONArray jsonPolygon = coords.getJSONArray(j);
                        final List<GeoPosition> polygon = parsePolygon(jsonPolygon.getJSONArray(0));
                        boundaries.add(polygon);
                    }
                }

                final Region region = new Region(name, boundaries);
                this.provincesToRegionMap.put(name, region);
                System.out.println("Finished loading " + name + " from JSON file");
            }
        } catch (Exception e) {
            System.err.println("Exception occurred when reading the boundaries file: " + e.getMessage());
            throw new GetFireData.InvalidDataException("Failed to load boundaries from JSON file: " + e.getMessage());
        }
    }

    /**
     * Wrapper method of getBoundariesData, enforcing the enum class Province while allowing fetching for "Canada".
     *
     * @param provinceName Name of the province from Province enum class
     * @return
     * @throws GetFireData.InvalidDataException
     */
    public List<List<GeoPosition>> getBoundariesData(final Province provinceName) throws GetFireData.InvalidDataException {
        final String provinceNameAPI = PROVINCES_TO_API.get(provinceName);
        return getBoundariesData(provinceNameAPI);

    }

    /**
     * Fetches and parses the boundaries data for a single province from the Nominatim API.
     *
     * @param provinceNameAPI The query parameter name of the province to fetch.
     * @return A list of polygons, where each polygon is a list of {@link GeoPosition} points.
     * @throws GetFireData.InvalidDataException if the API call fails or the response is invalid.
     */
    public List<List<GeoPosition>> getBoundariesData(final String provinceNameAPI) throws GetFireData.InvalidDataException {
        final String url = String.format(API_URL_TEMPLATE, provinceNameAPI);
        final Request request = new Request.Builder().url(url).build();
        try (Response response = this.client.newCall(request).execute()) {
            final ResponseBody responseBody = response.body();
            if (responseBody == null) {
                throw new GetFireData.InvalidDataException("Response body was null.");
            }
            final JSONArray responseArray = new JSONArray(responseBody.string());
            List<List<GeoPosition>> parsedResponse = parseResponse(responseArray);
            System.out.println(String.format("Finished parsing response for %s.", provinceNameAPI));
            return parsedResponse;
        }
        catch (IOException error) {
            throw new GetFireData.InvalidDataException("API call failed: " + error.getMessage());
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
}
