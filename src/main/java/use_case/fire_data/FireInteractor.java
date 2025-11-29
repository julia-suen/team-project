package use_case.fire_data;

import java.awt.geom.Path2D;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import entities.*;

import org.jxmapviewer.viewer.GeoPosition;

import data_access.BoundariesDataAccess;
import data_access.GetData;

/**
 * Interactor for the Fire Data Use Case.
 * Orchestrates data fetching, aggregation, filtering by province, and preparation for the presenter.
 */
public class FireInteractor implements FireInputBoundary {

    private static final int API_MAX_DAY_RANGE = 10;
    private static final int API_MIN_DAY_RANGE = 1;
    private static final int API_AVAILABLE_MONTHS = 3;
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String LABEL_FORMAT = "MMM";
    // e.g., "Aug", "Sep"
    private static final String WORLD_BOUNDS = "-180,-90,180,90";
    private final GetData dataAccessInterface;
    private final FireOutputBoundary firePresenter;
    private final BoundariesDataAccess boundariesDataAccess;

    /**
     * Constructs a FireInteractor.
     * @param dataAccessInterface the data access object for fire data
     * @param fireOutputBoundary the presenter
     * @param boundariesDataAccess the data access object for province boundaries
     */
    public FireInteractor(GetData dataAccessInterface, FireOutputBoundary fireOutputBoundary,
                          BoundariesDataAccess boundariesDataAccess) {
        this.dataAccessInterface = dataAccessInterface;
        this.firePresenter = fireOutputBoundary;
        this.boundariesDataAccess = boundariesDataAccess;
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
            if (range > API_MAX_DAY_RANGE) {
                range = API_MAX_DAY_RANGE;
            }
            if (range < API_MIN_DAY_RANGE) {
                range = API_MIN_DAY_RANGE;
            }

            // Retrieve the selected province
            String province = fireInputData.getProvince();

            if (fireInputData.isNationalOverview()) {
                processNationalOverview(inputDate, range, allFires, trendData);

            }
            else {
                processStandardView(inputDateStr, inputDate, range, allFires, trendData, province);
            }

            final FireOutputData fireOutputData = new FireOutputData(allFires, trendData);
            firePresenter.prepareSuccessView(fireOutputData);

        }
        catch (GetData.InvalidDataException ex) {
            firePresenter.prepareFailView("Error fetching data: " + ex.getMessage());
        }
        catch (Exception error) {
            firePresenter.prepareFailView("Unexpected error: " + error.getMessage());
        }
    }

    private void processNationalOverview(LocalDate inputDate, int range,
                                         List<Fire> allFires, Map<String, Integer> trendData)
            throws GetData.InvalidDataException {

        // For National Overview, we fetch world data but FILTER for Canada using boundaries
        final Region canadaRegion = boundariesDataAccess.getRegion("Canada");

        // Loop backwards (e.g. Month -2, Month -1, Current Month)
        for (int i = API_AVAILABLE_MONTHS - 1; i >= 0; i--) {
            final LocalDate targetDate = inputDate.minusMonths(i);
            final String targetDateStr = targetDate.format(DateTimeFormatter.ofPattern(DATE_FORMAT));
            final String label = targetDate.format(DateTimeFormatter.ofPattern(LABEL_FORMAT));

            // Fetch data
            List<Coordinate> points = dataAccessInterface.getFireData(range, targetDateStr, WORLD_BOUNDS);

            if (points == null) {
                points = new ArrayList<>();
            }

            points = filterPointsByProvince(points, "Canada");

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
                                     List<Fire> allFires, Map<String, Integer> trendData, String province)
            throws GetData.InvalidDataException {

        // Fetch data for the whole world
        List<Coordinate> points = dataAccessInterface.getFireData(range, inputDateStr);
        final String label = inputDate.format(DateTimeFormatter.ofPattern(LABEL_FORMAT));

        if (points == null) {
            points = new ArrayList<>();
        }

        // Filter by Province if applicable
        if (!"All".equalsIgnoreCase(province)) {
            points = filterPointsByProvince(points, province);
        }

        // Process points
        if (!points.isEmpty()) {
            final FireFactory fireFactory = new FireFactory(points);
            final List<List<Coordinate>> bundles = FireFactory.bundleDataPoints(fireFactory.getDataPoints());

            final List<Fire> monthFires = FireFactory.makeFireList(bundles);
            allFires.addAll(monthFires);
            trendData.put(label, points.size());
        }
        else {
            trendData.put(label, 0);
        }
    }

    /**
     * Filters a list of coordinates to include only those within the specified province.
     * @param points The raw list of fire coordinates.
     * @param provinceName The name of the province to filter by.
     * @return A new list containing only points inside the province.
     */
    private List<Coordinate> filterPointsByProvince(List<Coordinate> points, String provinceName) {
        final Region region = boundariesDataAccess.getRegion(provinceName);

        if (region == null) {
            // If data is not loaded yet, we cannot filter securely.
            // Returning empty list prevents showing global data when local was requested.
            System.err.println("Warning: Boundary data for " + provinceName + " not found/loaded.");
            return new ArrayList<>();
        }

        return filterPointsByBoundary(points, region.getBoundary());
    }

    /**
     * Generic helper to filter points by a specific boundary list.
     * @param points the points to filter
     * @param boundaries the boundary to use to filter the points given
     * @return filtered coordinates within the boundary
     */
    private List<Coordinate> filterPointsByBoundary(List<Coordinate> points, List<List<GeoPosition>> boundaries) {
        final List<Coordinate> filtered = new ArrayList<>();
        for (Coordinate point : points) {
            if (isPointInRegion(point, boundaries)) {
                filtered.add(point);
            }
        }
        return filtered;
    }

    /**
     * Checks if a specific coordinate lies within any of the polygons defining a region.
     * @param point The fire coordinate to check.
     * @param boundaries The list of polygons defining the region.
     * @return true if the point is inside the region, false otherwise.
     */
    private boolean isPointInRegion(Coordinate point, List<List<GeoPosition>> boundaries) {
        if (boundaries == null) {
            return false;
        }

        for (List<GeoPosition> polygon : boundaries) {
            // Use Path2D for polygon inclusion test
            final Path2D path = new Path2D.Double();
            boolean first = true;
            for (GeoPosition gp : polygon) {
                if (first) {
                    path.moveTo(gp.getLongitude(), gp.getLatitude());
                    first = false;
                }
                else {
                    path.lineTo(gp.getLongitude(), gp.getLatitude());
                }
            }
            path.closePath();

            // Check if the point is inside this specific polygon
            if (path.contains(point.getLon(), point.getLat())) {
                return true;
            }
        }
        return false;
    }
}
