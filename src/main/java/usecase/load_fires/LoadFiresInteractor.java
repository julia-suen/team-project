package usecase.load_fires;

import data_access.GetFireData;
import entities.Coordinate;
import entities.Fire;
import entities.MultiRegionFireStats;
import entities.Region;
import kotlin.Pair;
import usecase.common.FireService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.lang.Integer.max;
import static java.lang.Integer.min;

/**
 * Interactor for the "Load Fires" use case.
 * This class orchestrates the retrieval of fire data for a specific date and range,
 * bundles raw coordinates into {@link Fire} entities, and filters them based on the
 * selected province (or returns all if "All" is selected).
 */
public class LoadFiresInteractor implements LoadFiresInputBoundary {

    private static final String LABEL_FORMAT = "MMM";
    private static final DateTimeFormatter API_DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final LoadFiresFireDataAccess fireAccess;
    private final LoadFiresBoundaryDataAccess boundaryAccess;
    private final LoadFiresOutputBoundary presenter;
    private final FireService fireService;

    /**
     * Constructs a LoadFiresInteractor.
     * @param fireAccess     the interface to retrieve fire data from an external API
     * @param boundaryAccess the interface to retrieve region boundary data
     * @param presenter      the output boundary to present the results
     * @param fireService    the shared service for bundling and filtering logic
     */
    public LoadFiresInteractor(LoadFiresFireDataAccess fireAccess,
                               LoadFiresBoundaryDataAccess boundaryAccess,
                               LoadFiresOutputBoundary presenter,
                               FireService fireService) {
        this.fireAccess = fireAccess;
        this.boundaryAccess = boundaryAccess;
        this.presenter = presenter;
        this.fireService = fireService;
    }

    /**
     * Executes the "Load Fires" use case.
     * Fetches data, bundles it into entities, filters by the requested province,
     * and passes the result to the presenter.
     * @param inputData the input data containing province, date, and range
     */
    @Override
    public void execute(LoadFiresInputData inputData) {
        try {
            // Fetch Raw Data
            List<Coordinate> points = fireAccess.getFireData(inputData.getDateRange(), inputData.getDate());
            if (points == null) {
                points = new ArrayList<>();
            }

            // Preprocessing
            final List<Fire> allFires = fireService.createFiresFromPoints(points);

            // Prepare Trend Label
            final LocalDate date = LocalDate.parse(inputData.getDate());
            final String label = date.format(DateTimeFormatter.ofPattern(LABEL_FORMAT));
            final Map<String, Integer> trendData = new LinkedHashMap<>();

            final List<Fire> resultFires;

            // Filter logic
            final String province = inputData.getProvinces().get(0);
            if ("All".equalsIgnoreCase(province)) {
                resultFires = allFires;
            }
            else {
                final Region region = boundaryAccess.getRegion(province);
                if (region == null) {
                    System.err.println("Warning: Boundary data for " + province + " not found.");
                    resultFires = new ArrayList<>();
                }
                else {
                    resultFires = fireService.filterFiresByRegion(allFires, region);
                }
            }

            // Calculate Metrics
            int hotspotCount = 0;
            for (Fire f : resultFires) {
                hotspotCount += f.getCoordinates().size();
            }
            trendData.put(label, hotspotCount);

            final LoadFiresOutputData output = new LoadFiresOutputData(resultFires, trendData);
            presenter.prepareSuccessView(output);

        }
        catch (Exception e) {
            presenter.prepareFailView("Error fetching data: " + e.getMessage());
        }
    }

    @Override
    public MultiRegionFireStats fetchStats(LoadFiresInputData fireInputData) {
        Map<String, List<Pair<String, Integer>>> statsMap = new LinkedHashMap<>();

        LocalDate userDate = LocalDate.parse(fireInputData.getDate(), API_DATE_FMT);
        int range = fireInputData.getDateRange();

        //Define expanded time window: [selectDate - Range] to [selectDate + 2*Range].
        LocalDate grandStartDate = userDate.minusDays(range);
        int totalDaysToFetch = range * 3;

        List<Coordinate> allRawPoints = fetchAllRawPoints(grandStartDate, totalDaysToFetch);


        // Retrieve the selected province
        for (String provinceName : fireInputData.getProvinces()) {
            List<Coordinate> regionPoints;

            if ("All".equalsIgnoreCase(provinceName)) {
                regionPoints = allRawPoints;
            } else {
                Region region = boundaryAccess.getRegion(provinceName);
                if (region == null) {
                    regionPoints = new ArrayList<>();
                } else {

                    List<Fire> fires = fireService.createFiresFromPoints(allRawPoints);
                    fires = fireService.filterFiresByRegion(fires, region);

                    regionPoints = new ArrayList<>();
                    for (Fire f : fires) {
                        regionPoints.addAll(f.getCoordinates());
                    }
                }
            }

            Map<String, Integer> dailyCounts = new TreeMap<>();

            for (int i = 0; i < totalDaysToFetch; i++) {
                String dayStr = grandStartDate.plusDays(i).format(API_DATE_FMT);
                dailyCounts.put(dayStr, 0);
            }

            //Count actual fire points for each date.
            for (Coordinate p : regionPoints) {
                String pDate = p.getDate();
                if (dailyCounts.containsKey(pDate)) {
                    dailyCounts.put(pDate, dailyCounts.get(pDate) + 1);
                }
            }

            List<Pair<String, Integer>> pointsList = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : dailyCounts.entrySet()) {
                pointsList.add(new Pair<>(entry.getKey(), entry.getValue()));
            }

            statsMap.put(provinceName, pointsList);
        }

        return new MultiRegionFireStats(statsMap);
    }

    private List<Coordinate> fetchAllRawPoints(LocalDate startDate, int totalDays) {
        List<Coordinate> accumulator = new ArrayList<>();
        int daysFetched = 0;

        //Loop to fetch data in chunks because API limits requests to 10 days max.
        while (daysFetched < totalDays) {
            LocalDate batchStart = startDate.plusDays(daysFetched);
            int batchSize = Math.min(10, totalDays - daysFetched);

            try {
                List<Coordinate> batch = fireAccess.getFireData(batchSize, batchStart.toString());
                if (batch != null) {
                    accumulator.addAll(batch);
                }
            } catch (Exception e) {
                System.err.println("Failed to fetch batch starting " + batchStart + ": " + e.getMessage());
            }

            daysFetched += batchSize;
        }
        return accumulator;
    }
}

