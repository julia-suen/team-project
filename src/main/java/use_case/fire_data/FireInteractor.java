package use_case.fire_data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
    private static final int YEARS_HISTORY = 4;
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String CANADA_BOUNDS = "-141,41,-52,83";
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
            final Map<Integer, Integer> trendData = new TreeMap<>();

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
            }
            else {
                processStandardView(inputDateStr, inputDate, range, allFires, trendData);
            }

            final FireOutputData fireOutputData = new FireOutputData(allFires, trendData);
            firePresenter.prepareSuccessView(fireOutputData);

        }
        catch (GetData.InvalidDataException ex) {
            firePresenter.prepareFailView("Error fetching data: " + ex.getMessage());
        }
        catch (RuntimeException ex) {
            ex.printStackTrace();
            firePresenter.prepareFailView("Unexpected error: " + ex.getMessage());
        }

        // there's no return for this function! except should there be... NO it gets passed to presenter apparently
        // enter in julia data parse using the fire bundles to narrow down the list to a list of fire in the province IF
        // province is selected. otherwise, just use the fires variable on its own

        // and then insert a bunch of calls to like controllers and shi.... or like output views and like the output
        // boundaries to communicate the output info idk
    }

    private void processNationalOverview(LocalDate inputDate, int range,
                                         List<Fire> allFires, Map<Integer, Integer> trendData)
            throws GetData.InvalidDataException {
        // Get Coordinates for Canada
        final String canadaBoundingBox = getBoundariesForCountry("Canada");

        final int currentYear = inputDate.getYear();

        for (int i = 0; i < YEARS_HISTORY; i++) {
            final int targetYear = currentYear - i;
            final LocalDate targetDate = inputDate.withYear(targetYear);
            final String targetDateStr = targetDate.format(DateTimeFormatter.ofPattern(DATE_FORMAT));

            // Use the DataAccess method with the specific bounding box
            List<Coordinate> yearPoints = dataAccessInterface.getFireData(range, targetDateStr, canadaBoundingBox);

            if (yearPoints == null) {
                yearPoints = new ArrayList<>();
            }

            trendData.put(targetYear, yearPoints.size());

            if (!yearPoints.isEmpty()) {
                final FireFactory fireFactory = new FireFactory(yearPoints);
                final List<List<Coordinate>> bundles = FireFactory.bundleDataPoints(fireFactory.getDataPoints());
                final List<Fire> yearFires = FireFactory.makeFireList(bundles);
                allFires.addAll(yearFires);
            }
        }
    }

    private void processStandardView(String inputDateStr, LocalDate inputDate, int range,
                                     List<Fire> allFires, Map<Integer, Integer> trendData)
            throws GetData.InvalidDataException {
        // Standard View
        final List<Coordinate> points = dataAccessInterface.getFireData(range, inputDateStr);

        if (points != null && !points.isEmpty()) {
            final FireFactory fireFactory = new FireFactory(points);
            final List<List<Coordinate>> bundles = FireFactory.bundleDataPoints(fireFactory.getDataPoints());
            final List<Fire> yearFires = FireFactory.makeFireList(bundles);
            allFires.addAll(yearFires);
            trendData.put(inputDate.getYear(), points.size());
        }
        else {
            trendData.put(inputDate.getYear(), 0);
        }
    }

    /**
     * Placeholder method for retrieving country boundaries.
     * In a full implementation, this might query a database or external API.
     * @param countryName the name of the country
     * @return the bounding box string
     */
    private String getBoundariesForCountry(String countryName) {
        final String bounds;
        if ("Canada".equalsIgnoreCase(countryName)) {
            // Approx bounding box for Canada: minLon, minLat, maxLon, maxLat
            bounds = CANADA_BOUNDS;
        }
        else {
            // Default to world if unknown
            bounds = WORLD_BOUNDS;
        }
        return bounds;
    }
}
