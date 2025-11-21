package data_access;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class BoundariesDataAccess {
    private static final OkHttpClient client = new OkHttpClient();

    // should the class be static?? - then client also need to be static

    public static List<List<Double>> getBoundariesData(String provinceName) throws getData.InvalidDataException{
        Request request = new Request.Builder().url("https://nominatim.openstreetmap.org/search?q="+provinceName+"+canada&format=json&polygon_geojson=1&polygon_threshold=0.1").build();
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
    public static List<List<Double>> parseJsonCoordinates(JSONArray coordinatesArray){
        List<List<Double>> polygon = new ArrayList<>();
        for(int i = 0; i < coordinatesArray.length(); i++){
            double lat = coordinatesArray.getJSONArray(i).getDouble(0);
            double lon = coordinatesArray.getJSONArray(i).getDouble(1);
            List<Double> coordinatePair = new ArrayList<Double>();
            coordinatePair.add(lat);
            coordinatePair.add(lon);
            polygon.add(coordinatePair);
        }
        return polygon;
    }
}
