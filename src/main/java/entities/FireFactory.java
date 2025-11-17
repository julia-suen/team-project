package entities;
import fireapi.getData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Methods to generate a list of Fires from an unsorted list of coordinates.
 */

public class FireFactory {

    /** TO REPLACE WITH ACTUAL IMPLEMENTATION IF THE BELOW IS A GOOD IDEA ...
     * idea to further categorize fire data: there are many fire points for one single wildfire.
     * group up points likely to be single fires by checking for small discrepancies in coords into Fire entities
     *
     * then group all of those coords into one (set but not acc set) and set the center of the fire to the average
     * of the lats and longs of the coords
     *
     * the radius of the fire is the number of the points sqrted??? or scaled somehow in terms of the
     * number of data points attributed to that fire.
     */

    private static final double THRESHOLD = 0.001;
    private static final double RADIUS_SCALE = 10;
    private static final String INVALID_DATA = "n/a";

    public static List<List<Coordinate>> bundleDataPoints(List<Coordinate> data_points) {
        List<List<Coordinate>> pt_bundles = new ArrayList<>();
        List<Coordinate> constituting_pts = new ArrayList<>();

        //sorts by both lat and lon simultaneously
        data_points.sort(Comparator.comparingDouble((Coordinate p) -> p.lat)
                .thenComparingDouble(p -> p.lon));

        Coordinate previous = null;
        for (Coordinate coord : data_points) {
            if (previous == null) {
                constituting_pts.add(coord);
            }
            else {
                if (Math.abs(previous.lat - coord.lat) < THRESHOLD &&
                        Math.abs(previous.lon - coord.lon) < THRESHOLD) {
                    constituting_pts.add(coord);
                }
                else { // lat/lon diff was above threshold
                    pt_bundles.add(constituting_pts);
                    constituting_pts = new ArrayList<>();
                    constituting_pts.add(coord);
                }
            }
            previous = coord;
        }
        pt_bundles.add(constituting_pts);
        
        return pt_bundles;
    }

    public static List<Fire> makeFireList(List<List<Coordinate>> pt_bundles){
        // pt_bundles is now a list of bundled fire points
        //need to add logic for each bundle in pt_bundles, to calculate the centerpoint
        // coordinate and set it all to list of fire entities
        // marker will likely only be for the average values and the date day brightness will be invalid
        // THIS SHIT DOESNT SORT BY DATE IM GONNA KMS

        List<Fire> fires = new ArrayList<>();

        for (List<Coordinate> bundle: pt_bundles) {
            Coordinate center = getAvgCoordinate(bundle);

            bundle.sort(Comparator.comparingDouble(p -> p.lat));
            double lat_diff = Math.abs(bundle.get(0).lat - bundle.get(bundle.size() - 1).lat);
            double lon_diff = Math.abs(bundle.get(0).lon - bundle.get(bundle.size() - 1).lon);
            double avg_diameter = lat_diff + lon_diff/2;
            double radius = (avg_diameter/2) * RADIUS_SCALE; //replace with some appropriate scale

            /** ask what is an appropriate radius like prolly need to play
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

        int num_fires =  bundle.size();
        double sum_lats = 0;
        double sum_lons = 0;
        double bright_4 = 0;
        double bright_5 = 0;

        for (Coordinate pt: bundle) {
            sum_lats += pt.lat;
            sum_lons += pt.lon;
            bright_4 += pt.brightness[0];
            bright_5 += pt.brightness[1];
        }

        Coordinate center = new Coordinate(sum_lats/num_fires, sum_lons/num_fires,
                new String[] {INVALID_DATA, INVALID_DATA, INVALID_DATA},
                new double[] {bright_4/num_fires, bright_5/num_fires});

        return center;
    }
}
