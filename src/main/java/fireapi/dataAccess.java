package fireapi;

import java.util.HashMap;

import java.io.BufferedReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class dataAccess {

    /**
     * fireapi.dataAccess implementation that relies on the NRT VIIRS active fire API.
     * Note that all failures get reported as InvalidDataException
     * exceptions to align with the requirements of the fireapi.dataAccess interface.
     */

    private static final String MAP_KEY = "2f1f3b83b749cc2829c806c0e8e8959c";
    private static final String SOURCE = "VIIRS_SNPP_NRT";
    private static final String REGION = "world";

    /**
     * Fetch the data for the given date and date range from the NRT VIIRS API.
     *
     * @param dateRange an integer 1-10 specifying the number of days to fetch data for
     * @param date a start date from which to fetch data for
     * @return dataPoints a hashmap mapping each coordinate pair entry to values needed

     */

    public static HashMap<float[], Object[]> getFireData(int dateRange, String date)
            throws getData.InvalidDataException {
        // the keys are an array of lat, long and the values are in order: brightTemp, confidence and daynight

        final String request_url = "https://firms.modaps.eosdis.nasa.gov/usfs/api/area/csv/" + MAP_KEY + "/" + SOURCE +
                "/" + REGION + "/" + Integer.toString(dateRange) + "/" + date;

        HashMap<float[], Object[]> dataPoints = new HashMap<>();

        // extract data from csv:
        try {
            URL url = new URL(request_url);
            InputStream in = url.openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");

                // process vals to array of floats for coords and array of obj for associated data
                // only take vals that have nominal or high confidence (confidence == "n" or confidence == "h")
                // String date, float brightTemp4, float brightTemp5, String confidence, String daynight

                if (values[9].equals("n") || values[9].equals("h")) {
                    float[] coords = {Float.parseFloat(values[0]), Float.parseFloat(values[1])};
                    Object[] details = {values[5], Float.parseFloat(values[2]), Float.parseFloat(values[11]),
                            values[9], values[13]};
                    dataPoints.put(coords, details);
                }

            }
            reader.close();
        } catch (Exception e) {
            throw new getData.InvalidDataException();
        }
        return dataPoints;
    }
}
