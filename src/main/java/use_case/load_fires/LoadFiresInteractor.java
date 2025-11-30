package use_case.load_fires;

import entities.Coordinate;
import entities.Fire;
import entities.Region;
import use_case.common.FireService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LoadFiresInteractor implements LoadFiresInputBoundary {

    private final LoadFiresFireDataAccess fireAccess;
    private final LoadFiresBoundaryDataAccess boundaryAccess;
    private final LoadFiresOutputBoundary presenter;
    private final FireService fireService;

    private static final String LABEL_FORMAT = "MMM";

    public LoadFiresInteractor(LoadFiresFireDataAccess fireAccess,
                               LoadFiresBoundaryDataAccess boundaryAccess,
                               LoadFiresOutputBoundary presenter,
                               FireService fireService) {
        this.fireAccess = fireAccess;
        this.boundaryAccess = boundaryAccess;
        this.presenter = presenter;
        this.fireService = fireService;
    }

    @Override
    public void execute(LoadFiresInputData inputData) {
        try {
            // Fetch Raw Data
            List<Coordinate> points = fireAccess.getFireData(inputData.getDateRange(), inputData.getDate());
            if (points == null) {
                points = new ArrayList<>();
            }

            // Preprocessing
            List<Fire> allFires = fireService.createFiresFromPoints(points);

            // Prepare Trend Label
            LocalDate date = LocalDate.parse(inputData.getDate());
            String label = date.format(DateTimeFormatter.ofPattern(LABEL_FORMAT));
            Map<String, Integer> trendData = new LinkedHashMap<>();

            List<Fire> resultFires;

            // Filter logic
            String province = inputData.getProvince();
            if ("All".equalsIgnoreCase(province)) {
                resultFires = allFires;
            } else {
                Region region = boundaryAccess.getRegion(province);
                if (region == null) {
                    System.err.println("Warning: Boundary data for " + province + " not found.");
                    resultFires = new ArrayList<>();
                } else {
                    resultFires = fireService.filterFiresByRegion(allFires, region);
                }
            }

            // Calculate Metrics
            int hotspotCount = 0;
            for (Fire f : resultFires) {
                hotspotCount += f.getCoordinates().size();
            }
            trendData.put(label, hotspotCount);

            LoadFiresOutputData output = new LoadFiresOutputData(resultFires, trendData);
            presenter.prepareSuccessView(output);

        } catch (Exception e) {
            presenter.prepareFailView("Error fetching data: " + e.getMessage());
        }
    }
}
