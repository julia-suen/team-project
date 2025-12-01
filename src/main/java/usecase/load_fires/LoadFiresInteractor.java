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
        String inputDateStr = fireInputData.getDate();
        if (inputDateStr == null || inputDateStr.isEmpty()) {
            inputDateStr = LocalDate.now().toString();
        }
        final LocalDate inputDate = LocalDate.parse(inputDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        int range = fireInputData.getDateRange();

        // Retrieve the selected province
        List<String> provinces = fireInputData.getProvinces();
        MultiRegionFireStats stats = processMultiRegionFires(inputDate, range, provinces);
        System.out.println("Stats have been fetched! Stats: " + stats.getData().toString());
        return stats;
    }

    private MultiRegionFireStats processMultiRegionFires(LocalDate inputDate,
                                                         int range, List<String> provinces) {

        Map<String, List<Pair<String, Integer>>> stats = new HashMap<>();
        MultiRegionFireStats trendData = new MultiRegionFireStats(stats);

        if (provinces.isEmpty()) {
            return trendData;
        }

        try {
            // Initialize empty lists for each province
            for (String province : provinces) {
                trendData.put(province, new ArrayList<>());
            }

            final LocalDate minDate = LocalDate.of(2025, 8, 1);
            final LocalDate twoMonthsAgo = inputDate.minusMonths(2);
            final LocalDate oneMonthAgo = inputDate.minusMonths(1);

            List<LocalDate> startDates = new ArrayList<>();

            if (!twoMonthsAgo.isBefore(minDate)) {
                startDates.add(twoMonthsAgo);
            } else {
                startDates.add(minDate);
            }

            if (!oneMonthAgo.isBefore(minDate)) {
                startDates.add(oneMonthAgo);
            }

            // Always include the selected input date
            startDates.add(inputDate);

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            for (LocalDate startDate : startDates) {
                // Fetch data for this date range
                List<Coordinate> points = fireAccess.getFireData(range, fmt.format(startDate));
                final List<Fire> fires = fireService.createFiresFromPoints(points);
                if (!points.isEmpty()) {
                    LocalDate endDate = startDate.plusDays(range);
                    String label = fmt.format(startDate) + " to " + fmt.format(endDate);

                    System.out.println("Number of datapoints fetched for analysis: " + points.size() + " for the date range " + label);

                    for (String province : provinces) {
                        final Region region = boundaryAccess.getRegion(province);
                        // Filter points for this province
                        List<Fire> firesInProvince = fireService.filterFiresByRegion(fires, region);

                        // Add to that province's list in trendData
                        trendData.getData().get(province).add(new Pair<>(label, firesInProvince.size()));
                    }
                }
            }

            return trendData;

        } catch (GetFireData.InvalidDataException ex) {
            return trendData;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
