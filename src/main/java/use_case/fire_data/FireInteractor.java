package use_case.fire_data;

import entities.Coordinate;
import entities.Fire;
import entities.FireFactory;
import fireapi.GetData;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static entities.FireFactory.bundleDataPoints;
import static entities.FireFactory.makeFireList;

public class FireInteractor implements FireInputBoundary {
    private final GetData dataAccessInterface;
    private final FireOutputBoundary firePresenter;

    public FireInteractor(GetData dataAccessInterface, FireOutputBoundary fireOutputBoundary) {
        this.dataAccessInterface = dataAccessInterface;
        this.firePresenter = fireOutputBoundary;
    }

    @Override
    public void execute(FireInputData fireInputData) {
        System.out.println("--- FireInteractor: Execute Started ---");

        try {
            List<Fire> allFires = new ArrayList<>();
            Map<Integer, Integer> trendData = new TreeMap<>();

            String inputDateStr = fireInputData.getDate();
            if (inputDateStr == null || inputDateStr.isEmpty()) {
                inputDateStr = LocalDate.now().toString();
            }
            LocalDate inputDate = LocalDate.parse(inputDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            int range = fireInputData.getDateRange();
            if (range > 10) range = 10;
            if (range < 1) range = 1;

            if (fireInputData.isNationalOverview()) {
                // Get Coordinates for Canada
                String canadaBoundingBox = getBoundariesForCountry("Canada");
                System.out.println("DEBUG: Using coordinates for Canada: " + canadaBoundingBox);

                int currentYear = inputDate.getYear();

                for (int i = 0; i < 4; i++) {
                    int targetYear = currentYear - i;
                    LocalDate targetDate = inputDate.withYear(targetYear);
                    String targetDateStr = targetDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                    // Use the DataAccess method with the specific bounding box
                    List<Coordinate> yearPoints = dataAccessInterface.getFireData(range, targetDateStr, canadaBoundingBox);

                    if (yearPoints == null) yearPoints = new ArrayList<>();

                    trendData.put(targetYear, yearPoints.size());

                    if (!yearPoints.isEmpty()) {
                        FireFactory fireFactory = new FireFactory(yearPoints);
                        List<List<Coordinate>> bundles = bundleDataPoints(fireFactory.getDataPoints());
                        List<Fire> yearFires = makeFireList(bundles);
                        allFires.addAll(yearFires);
                    }
                }

            } else {
                // Standard View
                List<Coordinate> points = dataAccessInterface.getFireData(range, inputDateStr);

                if (points != null && !points.isEmpty()) {
                    FireFactory fireFactory = new FireFactory(points);
                    List<List<Coordinate>> bundles = bundleDataPoints(fireFactory.getDataPoints());
                    allFires = makeFireList(bundles);
                    trendData.put(inputDate.getYear(), points.size());
                } else {
                    trendData.put(inputDate.getYear(), 0);
                }
            }

            final FireOutputData fireOutputData = new FireOutputData(allFires, trendData);
            firePresenter.prepareSuccessView(fireOutputData);

        } catch (GetData.InvalidDataException error) {
            firePresenter.prepareFailView("Error fetching data: " + error.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            firePresenter.prepareFailView("Unexpected error: " + e.getMessage());
        }
    }

    /**
     * Placeholder method for retrieving country boundaries.
     * In a full implementation, this might query a database or external API.
     */
    private String getBoundariesForCountry(String countryName) {
        // Placeholder code
        if ("Canada".equalsIgnoreCase(countryName)) {
            // Approx bounding box for Canada: minLon, minLat, maxLon, maxLat
            return "-141,41,-52,83";
        }
        // Default to world if unknown
        return "-180,-90,180,90";
    }


        // there's no return for this function! except should there be... NO it gets passed to presenter apparently
        // enter in julia data parse using the fire bundles to narrow down the list to a list of fire in the province IF
        // province is selected. otherwise, just use the fires variable on its own

        // and then insert a bunch of calls to like controllers and shi.... or like output views and like the output
        // boundaries to communicate the output info idk

}