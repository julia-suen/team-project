package entities;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.jetbrains.annotations.NotNull;

/**
 * Methods to generate a list of Fires from an unsorted list of coordinates.
 */

public class FireFactory {

    /**
     * TO REPLACE WITH ACTUAL IMPLEMENTATION IF THE BELOW IS A GOOD IDEA ...
     * idea to further categorize fire data: there are many fire points for one single wildfire.
     * group up points likely to be single fires by checking for small discrepancies in coords into Fire entities
     * then group all of those coords into one (set but not acc set) and set the center of the fire to the average
     * of the lats and longs of the coords
     * the radius of the fire is the number of the points sqrted??? or scaled somehow in terms of the
     * number of data points attributed to that fire.
     */

    private static final double THRESHOLD = 0.001;
    private static final double RADIUS_SCALE = 10;
    private static final String INVALID_DATA = "n/a";

    public static List<List<Coordinate>> bundleDataPoints(List<Coordinate> dataPoints) {

        final List<List<Coordinate>> ptBundles = new ArrayList<>();
        List<Coordinate> constitutingPts = new ArrayList<>();

        // sorts by both lat and lon simultaneously
        dataPoints.sort(Comparator.comparingDouble((Coordinate point) -> point.getLat())
                .thenComparingDouble(point -> point.getLon()));

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

    public static List<Fire> makeFireList(List<List<Coordinate>> pt_bundles) {
        // pt_bundles is now a list of bundled fire points
        // need to add logic for each bundle in pt_bundles, to calculate the centerpoint
        // coordinate and set it all to list of fire entities
        // marker will likely only be for the average values and the date day brightness will be invalid
        // THIS SHIT DOESNT SORT BY DATE IM GONNA KMS

        final List<Fire> fires = new ArrayList<>();

        for (List<Coordinate> bundle: pt_bundles) {
            final Coordinate center = getAvgCoordinate(bundle);

            bundle.sort(Comparator.comparingDouble(point -> point.getLat()));
            final double latDiff = Math.abs(bundle.get(0).getLat() - bundle.get(bundle.size() - 1).getLat());
            final double lonDiff = Math.abs(bundle.get(0).getLon() - bundle.get(bundle.size() - 1).getLon());
            final double avgDiameter = latDiff + lonDiff / 2;
            final double radius = (avgDiameter / 2) * RADIUS_SCALE;

            /**
             * ask what is an appropriate radius like prolly need to play
            *   around what if its a single fire and a huge dot
            */

            fires.add(new Fire(radius, center, bundle));

        }
        return fires;
    }

    @NotNull
    private static Coordinate getAvgCoordinate(List<Coordinate> bundle) {
        /**
         * Calculates the center/average point from bundle, a list of coordinates given and instantiates a new
         * Coordinate with the values. The date_day_confidence attribute of the returned coordinate contains invalid
         * data, and the brightness attribute of the returned point is the average brightnesses across all points in
         * the given bundle.
         */

        final int numFires = bundle.size();
        double sumLats = 0;
        double sumLons = 0;
        double bright4 = 0;
        double bright5 = 0;

        for (Coordinate pt: bundle) {
            sumLats += pt.getLat();
            sumLons += pt.getLon();
            bright4 += pt.getBrightness()[0];
            bright5 += pt.getBrightness()[1];
        }

        return new Coordinate(sumLats / numFires, sumLons / numFires,
                new String[] {INVALID_DATA, INVALID_DATA, INVALID_DATA},
                new double[] {bright4 / numFires, bright5 / numFires});
    }
}
