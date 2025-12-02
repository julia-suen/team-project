package usecase.load_fires;

import entities.Coordinate;
import entities.Fire;
import entities.Region;
import usecase.common.FireService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
}
