package entities;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.jetbrains.annotations.NotNull;

/**
 * Methods to generate a list of Fires from an unsorted list of coordinates contained in a FireFactory object.
 */

public class FireFactory {

    private static final double THRESHOLD = 0.001;
    private static final double MIN_RADIUS_THRESHOLD = 0.0001;
    private static final double DEFAULT_SAFETY_RADIUS = 0.01;
    private static final double MED_SEVERITY_THRESHOLD = 3;
    private static final double HIGH_SEVERITY_THRESHOLD = 7;
    private static final String INVALID_DATA = "n/a";
    private final List<Coordinate> dataPoints;

    /**
     * Creates a FireFactory using a set of unparsed data points which can be parsed using the methods contained in
     * this class.
     *
     * @param dataPoints the dataPoints to be sorted into Fire objects
     */

    public FireFactory(List<Coordinate> dataPoints) {
        this.dataPoints = dataPoints;
    }

    /**
     * Sorts Coordinate objects into bundles based off of differences in their latitude and longitude which either
     * make them part of a single fire, or separate ones.
     *
     * @param dataPoints the dataPoints to be sorted into bundles of data points
     * @return a List of Coordinate Lists, with each Coordinate List representing the data points of a single fire
     */

    public static List<List<Coordinate>> bundleDataPoints(List<Coordinate> dataPoints) {
        // If dataPoints is null or empty, return empty list
        final List<List<Coordinate>> ptBundles = new ArrayList<>();
        if (dataPoints == null || dataPoints.isEmpty()) {
            return ptBundles;
        }

        List<Coordinate> constitutingPts = new ArrayList<>();

        // sorts by both lat and lon simultaneously
        dataPoints.sort(Comparator.comparingDouble(Coordinate::getLat)
                .thenComparingDouble(Coordinate::getLon));

        Coordinate previous = null;
        for (Coordinate coord : dataPoints) {
            if (previous == null) {
                constitutingPts.add(coord);
            }
            else {
                if (Math.abs(previous.getLat() - coord.getLat()) < THRESHOLD
                        &&
                        Math.abs(previous.getLon() - coord.getLon()) < THRESHOLD) {
                    constitutingPts.add(coord);
                }
                // lat/lon diff was above threshold
                else {
                    ptBundles.add(constitutingPts);
                    constitutingPts = new ArrayList<>();
                    constitutingPts.add(coord);
                }
            }
            previous = coord;
        }
        ptBundles.add(constitutingPts);

        return ptBundles;
    }

    /**
     * Sorts bundles of coordinates into Fire objects by calculating their centerpoint and radius.
     *
     * @param ptBundles the List containing bundles of coordinates to be sorted into Fire objects
     * @return a List of Fires recognized using the bundles of data points given
     */

    public static List<Fire> makeFireList(List<List<Coordinate>> ptBundles) {

        final List<Fire> fires = new ArrayList<>();

        for (List<Coordinate> bundle : ptBundles) {
            final Coordinate center = getAvgCoordinate(bundle);

            bundle.sort(Comparator.comparingDouble(Coordinate::getLat));
            final double latDiff = Math.abs(bundle.get(0).getLat() - bundle.get(bundle.size() - 1).getLat());
            final double lonDiff = Math.abs(bundle.get(0).getLon() - bundle.get(bundle.size() - 1).getLon());
            final double avgDiameter = latDiff + lonDiff / 2;
            double radius = avgDiameter / 2;

            // Prevent crash for radius 0
            if (radius <= MIN_RADIUS_THRESHOLD) {
                radius = DEFAULT_SAFETY_RADIUS;
            }

            fires.add(new Fire(radius, center, bundle));

        }
        return fires;
    }

    /**
     * Sorts bundles of coordinates into Fire objects with greater FRP than
     * @param ptBundles an unfiltered collection of fires
     * @return a filtered fire list based off of their severity as determined by FRP
     */
    public static List<Fire> makeFilteredFireList(List<List<Coordinate>> ptBundles, boolean medSev, boolean highSev) {
        final List<Fire> fires = new ArrayList<>();
        double severity;
        double radius;

        if (medSev) {
            severity = MED_SEVERITY_THRESHOLD;
        }
        else {
            severity = HIGH_SEVERITY_THRESHOLD;
        }

        for (List<Coordinate> bundle : ptBundles) {
            final Coordinate center = getAvgCoordinate(bundle);

            if (center.getFrp() >= severity) {
                bundle.sort(Comparator.comparingDouble(Coordinate::getLat));
                final double latDiff = Math.abs(bundle.get(0).getLat() - bundle.get(bundle.size() - 1).getLat());
                final double lonDiff = Math.abs(bundle.get(0).getLon() - bundle.get(bundle.size() - 1).getLon());
                final double avgDiameter = latDiff + lonDiff / 2;
                radius = avgDiameter / 2;

                // Prevent crash for radius 0
                if (radius <= MIN_RADIUS_THRESHOLD) {
                    radius = DEFAULT_SAFETY_RADIUS;
                }

                fires.add(new Fire(radius, center, bundle));

            }
        }
        return fires;
    }

    /**
     * Helper method for makeFireList.
     * Calculates the center/average point from bundle, a list of coordinates given and instantiates a new
     * Coordinate with the values. The date_day_confidence attribute of the returned coordinate contains invalid
     * data, and the brightness attribute of the returned point is the average brightnesses across all points in
     * the given bundle.
     *
     * @param bundle the bundle of points for which to calculate average stats for
     * @return a Coordinate object containing the average data of the bundle
     */

    @NotNull
    private static Coordinate getAvgCoordinate(List<Coordinate> bundle) {

        final int numFires = bundle.size();
        double sumLats = 0;
        double sumLons = 0;
        double bright4 = 0;
        double bright5 = 0;
        double frp = 0;

        for (Coordinate pt : bundle) {
            sumLats += pt.getLat();
            sumLons += pt.getLon();
            bright4 += pt.getBrightness()[0];
            bright5 += pt.getBrightness()[1];
            frp += pt.getFrp();
        }

        return new Coordinate(sumLats / numFires, sumLons / numFires,
                new String[]{INVALID_DATA, INVALID_DATA, INVALID_DATA},
                new double[]{bright4 / numFires, bright5 / numFires},
                frp / numFires);
    }

    public static List<Fire> filterFires(List<List<Coordinate>> bundles, SeverityFilter severityFilter) {
        return switch (severityFilter) {
            case MEDIUM -> FireFactory.makeFilteredFireList(bundles, true, false);
            case HIGH -> FireFactory.makeFilteredFireList(bundles, false, true);
            default -> FireFactory.makeFireList(bundles);
        };
    }

    public List<Coordinate> getDataPoints() {
        return dataPoints;
    }
}
