package use_case.national_overview;

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

public class NationalOverviewInteractor implements NationalOverviewInputBoundary {

    private static final int API_AVAILABLE_MONTHS = 3;
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String LABEL_FORMAT = "MMM";
    private static final String WORLD_BOUNDS = "-180,-90,180,90";

    private final NationalOverviewFireDataAccess fireAccess;
    private final NationalOverviewBoundaryDataAccess boundaryAccess;
    private final NationalOverviewOutputBoundary presenter;
    private final FireService fireService;

    public NationalOverviewInteractor(NationalOverviewFireDataAccess fireAccess,
                                      NationalOverviewBoundaryDataAccess boundaryAccess,
                                      NationalOverviewOutputBoundary presenter,
                                      FireService fireService) {
        this.fireAccess = fireAccess;
        this.boundaryAccess = boundaryAccess;
        this.presenter = presenter;
        this.fireService = fireService;
    }

    @Override
    public void execute(NationalOverviewInputData inputData) {
        try {
            final List<Fire> allFires = new ArrayList<>();
            final Map<String, Integer> trendData = new LinkedHashMap<>();

            final LocalDate inputDate = LocalDate.parse(inputData.getDate(), DateTimeFormatter.ofPattern(DATE_FORMAT));
            final Region canadaRegion = boundaryAccess.getRegion("Canada");

            // Loop backwards (e.g. Month -2, Month -1, Current Month)
            for (int i = API_AVAILABLE_MONTHS - 1; i >= 0; i--) {
                final LocalDate targetDate = inputDate.minusMonths(i);
                final String targetDateStr = targetDate.format(DateTimeFormatter.ofPattern(DATE_FORMAT));
                final String label = targetDate.format(DateTimeFormatter.ofPattern(LABEL_FORMAT));

                // Fetch Raw Data
                List<Coordinate> points = fireAccess.getFireData(inputData.getRange(), targetDateStr, WORLD_BOUNDS);
                if (points == null) {
                    points = new ArrayList<>();
                }

                // Preprocessing
                final List<Fire> monthFires = fireService.createFiresFromPoints(points);

                // Filter to Canada
                final List<Fire> canadaFires;
                if (canadaRegion != null) {
                    canadaFires = fireService.filterFiresByRegion(monthFires, canadaRegion);
                }
                else {
                    canadaFires = new ArrayList<>();
                }

                // Accumulate Results
                allFires.addAll(canadaFires);

                // Calculate Metrics (Hotspot count)
                int hotspotCount = 0;
                for (Fire f : canadaFires) {
                    hotspotCount += f.getCoordinates().size();
                }
                trendData.put(label, hotspotCount);
            }

            final NationalOverviewOutputData output = new NationalOverviewOutputData(allFires, trendData);
            presenter.prepareSuccessView(output);

        }
        catch (Exception e) {
            presenter.prepareFailView("Error processing national overview: " + e.getMessage());
        }
    }
}
