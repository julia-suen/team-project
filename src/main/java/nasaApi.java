////package nasaApi;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//
//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.io.IOException;
//
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//import java.io.IOException;
//import java.util.*;
//
//public class nasaApi {
//
//    /**
//     * BreedFetcher implementation that relies on the dog.ceo API.
//     * Note that all failures get reported as BreedNotFoundException
//     * exceptions to align with the requirements of the BreedFetcher interface.
//     */
//
//    private static final String MAP_KEY = "2f1f3b83b749cc2829c806c0e8e8959c";
//    private static final String SOURCE = "VIIRS_SNPP_NRT";
//    private static final String REGION = "world";
//
//    /**
//     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
//     *
//     * @param dateRange an integer 1-10 specifying the number of days to fetch data for
//     * @param date a start date from which to fetch data for
//     * @return dictionary with keys mapped to values needed (???)
//
//     */
//
//    public List<String> getFireData(int dateRange, String date) {
//        //consider returning a dictionary with keys diff attributes and associated values?
//        final OkHttpClient client = new OkHttpClient();
//        final String request_url = "https://firms.modaps.eosdis.nasa.gov/usfs/api/area/csv/" + MAP_KEY + "/" + SOURCE +
//                "/" + REGION + dateRange + "/" + date;
//        final Request request = new Request.Builder().url(request_url).build();
//
//        // extract data from csv:
//        // double latitude, double longitude, double brightTemp,
//        return [];
//    }
//}
