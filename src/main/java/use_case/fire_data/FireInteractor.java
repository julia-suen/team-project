package use_case.fire_data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import entities.Coordinate;
import entities.Fire;
import entities.FireFactory;
import fireapi.GetData;

/**
 * Interactor for the Fire Data Use Case.
 * Orchestrates data fetching, aggregation, and preparation for the presenter.
 */
public class FireInteractor implements FireInputBoundary {

    private static final int MAX_RANGE = 10;
    private static final int MIN_RANGE = 1;
    private static final int MONTHS_HISTORY = 3;
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String LABEL_FORMAT = "MMM"; // e.g., "Aug", "Sep"
//    private static final String CANADA_BOUNDS = "-141,41,-52,83";
    private static final String WORLD_BOUNDS = "-180,-90,180,90";

    private final GetData dataAccessInterface;
    private final FireOutputBoundary firePresenter;

    /**
     * Constructs a FireInteractor.
     * @param dataAccessInterface the data access object
     * @param fireOutputBoundary the presenter
     */
    public FireInteractor(GetData dataAccessInterface, FireOutputBoundary fireOutputBoundary) {
        this.dataAccessInterface = dataAccessInterface;
        this.firePresenter = fireOutputBoundary;
    }

    @Override
    public void execute(FireInputData fireInputData) {

        try {
            final List<Fire> allFires = new ArrayList<>();
            // Use LinkedHashMap to preserve order (Month 1, Month 2, Month 3)
            final Map<String, Integer> trendData = new LinkedHashMap<>();

            String inputDateStr = fireInputData.getDate();
            if (inputDateStr == null || inputDateStr.isEmpty()) {
                inputDateStr = LocalDate.now().toString();
            }
            final LocalDate inputDate = LocalDate.parse(inputDateStr, DateTimeFormatter.ofPattern(DATE_FORMAT));

            int range = fireInputData.getDateRange();
            if (range > MAX_RANGE) {
                range = MAX_RANGE;
            }
            if (range < MIN_RANGE) {
                range = MIN_RANGE;
            }

            if (fireInputData.isNationalOverview()) {
                processNationalOverview(inputDate, range, allFires, trendData);
            } else {
                processStandardView(inputDateStr, inputDate, range, allFires, trendData);
            }

            final FireOutputData fireOutputData = new FireOutputData(allFires, trendData);
            firePresenter.prepareSuccessView(fireOutputData);

        } catch (GetData.InvalidDataException ex) {
            firePresenter.prepareFailView("Error fetching data: " + ex.getMessage());
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            firePresenter.prepareFailView("Unexpected error: " + ex.getMessage());
        }
    }

    private void processNationalOverview(LocalDate inputDate, int range,
                                         List<Fire> allFires, Map<String, Integer> trendData)
            throws GetData.InvalidDataException {
        final String canadaBoundingBox = getBoundariesForCountry("Canada");

        // Loop 0 to MONTHS_HISTORY - 1.
        // We iterate backwards (i=2 to 0) to insert into Map in chronological order (Past -> Present)
        for (int i = MONTHS_HISTORY - 1; i >= 0; i--) {
            final LocalDate targetDate = inputDate.minusMonths(i);
            final String targetDateStr = targetDate.format(DateTimeFormatter.ofPattern(DATE_FORMAT));
            final String label = targetDate.format(DateTimeFormatter.ofPattern(LABEL_FORMAT));

            // Fetch data
            List<Coordinate> points = dataAccessInterface.getFireData(range, targetDateStr, canadaBoundingBox);

            if (points == null) {
                points = new ArrayList<>();
            }

            // Add to trend map (e.g., "Aug" -> 150)
            trendData.put(label, points.size());

            // Accumulate fires for the map
            if (!points.isEmpty()) {
                final FireFactory fireFactory = new FireFactory(points);
                final List<List<Coordinate>> bundles = FireFactory.bundleDataPoints(fireFactory.getDataPoints());
                final List<Fire> monthFires = FireFactory.makeFireList(bundles);
                allFires.addAll(monthFires);
            }
        }
    }

    private void processStandardView(String inputDateStr, LocalDate inputDate, int range,
                                     List<Fire> allFires, Map<String, Integer> trendData)
            throws GetData.InvalidDataException {

        final List<Coordinate> points = dataAccessInterface.getFireData(range, inputDateStr);
        final String label = inputDate.format(DateTimeFormatter.ofPattern(LABEL_FORMAT));

        if (points != null && !points.isEmpty()) {
            final FireFactory fireFactory = new FireFactory(points);
            final List<List<Coordinate>> bundles = FireFactory.bundleDataPoints(fireFactory.getDataPoints());
            final List<Fire> fires = FireFactory.makeFireList(bundles);
            allFires.addAll(fires);
            trendData.put(label, points.size());
        } else {
            trendData.put(label, 0);
        }
    }

    // TODO: Will be replaced
    private String getBoundariesForCountry(String countryName) {
        final String bounds;
        if ("Canada".equalsIgnoreCase(countryName)) {
            bounds = WORLD_BOUNDS;
        } else {
            bounds = WORLD_BOUNDS;
        }
        return bounds;
    }
}
