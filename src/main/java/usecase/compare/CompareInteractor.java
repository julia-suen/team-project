package usecase.compare;

import entities.Coordinate;
import entities.Fire;
import entities.MultiRegionFireStats;
import entities.Region;
import kotlin.Pair;
import usecase.common.FireService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Interactor for the "Compare" use case.
 * This class orchestrates the retrieval of fire data for multiple regions over a time range,
 * bundles raw coordinates into stats, and aggregates fires to be displayed on the map.
 */
public class CompareInteractor implements CompareInputBoundary {

    private static final DateTimeFormatter API_DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final CompareFireDataAccess fireAccess;
    private final CompareBoundaryDataAccess boundaryAccess;
    private final FireService fireService;

    /**
     * Constructs a CompareInteractor.
     * @param fireAccess     the interface to retrieve fire data from an external API
     * @param boundaryAccess the interface to retrieve region boundary data
     * @param fireService    the shared service for bundling and filtering logic
     */
    public CompareInteractor(CompareFireDataAccess fireAccess,
                             CompareBoundaryDataAccess boundaryAccess,
                             FireService fireService) {
        this.fireAccess = fireAccess;
        this.boundaryAccess = boundaryAccess;
        this.fireService = fireService;
    }

    @Override
    public CompareOutputData execute(CompareInputData inputData) {
        Map<String, List<Pair<String, Integer>>> statsMap = new LinkedHashMap<>();
        Set<Fire> aggregatedFires = new LinkedHashSet<>(); // Use Set to avoid duplicates if regions overlap or "All" is used

        LocalDate userDate = LocalDate.parse(inputData.getDate(), API_DATE_FMT);
        int range = inputData.getDateRange();

        // Define expanded time window: [selectDate - Range] to [selectDate + 2*Range].
        LocalDate grandStartDate = userDate.minusDays(range);
        int totalDaysToFetch = range * 3;

        List<Coordinate> allRawPoints = fetchAllRawPoints(grandStartDate, totalDaysToFetch);

        // Optimization: Bundle all raw points into Fire objects once
        List<Fire> allGlobalFires = fireService.createFiresFromPoints(allRawPoints);

        // Retrieve the selected province
        for (String provinceName : inputData.getProvinces()) {
            List<Coordinate> regionPoints = new ArrayList<>();
            List<Fire> regionFires = new ArrayList<>();

            if ("All".equalsIgnoreCase(provinceName)) {
                regionPoints = allRawPoints;
                regionFires = allGlobalFires;
            } else {
                Region region = boundaryAccess.getRegion(provinceName);
                if (region != null) {
                    regionFires = fireService.filterFiresByRegion(allGlobalFires, region);

                    for (Fire f : regionFires) {
                        regionPoints.addAll(f.getCoordinates());
                    }
                }
            }

            // Accumulate fires for display
            aggregatedFires.addAll(regionFires);

            Map<String, Integer> dailyCounts = new TreeMap<>();

            for (int i = 0; i < totalDaysToFetch; i++) {
                String dayStr = grandStartDate.plusDays(i).format(API_DATE_FMT);
                dailyCounts.put(dayStr, 0);
            }

            // Count actual fire points for each date.
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

        return new CompareOutputData(new MultiRegionFireStats(statsMap), new ArrayList<>(aggregatedFires));
    }

    private List<Coordinate> fetchAllRawPoints(LocalDate startDate, int totalDays) {
        List<Coordinate> accumulator = new ArrayList<>();
        int daysFetched = 0;

        // Loop to fetch data in chunks because API limits requests to 10 days max.
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
