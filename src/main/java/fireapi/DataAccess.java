package fireapi;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import entities.Coordinate;
import entities.FireFactory;

public class DataAccess implements GetData {

    /**
     * DataAccess implementation that relies on the NRT VIIRS active fire API.
     * Note that all failures get reported as InvalidDataException
     * exceptions to align with the requirements of the fireapi.dataAccess interface.
     */

    private static final String MAP_KEY = "2f1f3b83b749cc2829c806c0e8e8959c";
    private static final String SOURCE = "VIIRS_SNPP_NRT";
    private static final String REGION = "world";
    private static final String SL = "/";
    private static final int BRIGHT4_INDEX = 2;
    private static final int DATE_INDEX = 5;
    private static final int CONFIDENCE_INDEX = 9;
    private static final int BRIGHT5_INDEX = 11;
    private static final int DAYNIGHT_INDEX = 13;

    private static final int API_THRESHOLD = 10;

    private final FireFactory fireFactory;

    public DataAccess(FireFactory fireFactory) {
        this.fireFactory = fireFactory;
    }

    /**
     * Fetch the data for the given date and date range from the NRT VIIRS API.
     *
     * @param dateRange an integer 1-10 specifying the number of days to fetch data for
     * @param date a start date from which to fetch data for
     * @return dataPoints a hashmap mapping each coordinate pair entry to values needed
     */

    @Override
    public List<Coordinate> getFireData(int dateRange, String date) throws GetData.InvalidDataException {
        return getFireData(dateRange, date, REGION);
    }

    @Override
    public List<Coordinate> getFireData(int dateRange, String date, String boundingBox) throws InvalidDataException {
        if (dateRange > API_THRESHOLD) {
            dateRange = API_THRESHOLD;
        }

        final String requestUrl = "https://firms.modaps.eosdis.nasa.gov/usfs/api/area/csv/" + MAP_KEY + SL + SOURCE
                + SL + boundingBox + SL + dateRange + SL + date;

        final List<Coordinate> dataPoints = new ArrayList<>();

        // extract data from url:
        try {
            final URL url = new URL(requestUrl);
            final InputStream in = url.openStream();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                final String[] values = line.split(",");

                // only take vals that have nominal or high confidence (confidence == "n" or confidence == "h")
                if (values[CONFIDENCE_INDEX].equals("n") || values[CONFIDENCE_INDEX].equals("h")) {
                    final Coordinate coord = new Coordinate(Float.parseFloat(values[0]), Float.parseFloat(values[1]),
                            new String[]{values[DATE_INDEX], values[DAYNIGHT_INDEX], values[CONFIDENCE_INDEX]},
                            new double[]{Float.parseFloat(values[BRIGHT4_INDEX]),
                                    Float.parseFloat(values[BRIGHT5_INDEX])});
                    dataPoints.add(coord);
                }
            }
            reader.close();
        }

        catch (Exception exception) {
            throw new GetData.InvalidDataException();
        }
        return dataPoints;
    }
}
