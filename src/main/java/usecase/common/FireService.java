package usecase.common;

import entities.Coordinate;
import entities.Fire;
import entities.FireFactory;
import entities.Region;
import entities.SeverityFilter;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Service containing shared business logic for processing fire data.
 * Adheres to the principle that raw points should be bundled into Fire entities immediately.
 */
public class FireService {
    private static final double MED_SEVERITY_THRESHOLD = 3.0;
    private static final double HIGH_SEVERITY_THRESHOLD = 7.0;

    public FireService() {
        // Empty constructor
    }

    /**
     * Converts raw coordinates into Fire objects.
     * This is the standard preprocessing step for all fire data.
     * @param points the raw list of coordinates from the API
     * @return a list of Fire objects
     */
    public List<Fire> createFiresFromPoints(List<Coordinate> points) {
        if (points == null || points.isEmpty()) {
            return new ArrayList<>();
        }
        final FireFactory fireFactory = new FireFactory(points);
        final List<List<Coordinate>> bundles = FireFactory.bundleDataPoints(fireFactory.getDataPoints());
        return FireFactory.makeFireList(bundles);
    }

    /**
     * Filters a list of Fire objects based on the Severity Filter.
     * Operates directly on the Fire entities without rebuilding them.
     * * @param fires the list of fires to filter
     * @param filter the severity filter (RESET, MEDIUM, HIGH)
     * @return a filtered list of fires
     */
    public List<Fire> filterFiresBySeverity(List<Fire> fires, SeverityFilter filter) {
        if (fires == null || fires.isEmpty() || filter == SeverityFilter.RESET) {
            return fires; // Return all if reset or empty
        }

        List<Fire> filtered = new ArrayList<>();
        double threshold = (filter == SeverityFilter.HIGH) ? HIGH_SEVERITY_THRESHOLD : MED_SEVERITY_THRESHOLD;

        for (Fire fire : fires) {
            // Check the FRP of the fire's center
            if (fire.getCenter().getFrp() >= threshold) {
                filtered.add(fire);
            }
        }
        return filtered;
    }

    /**
     * Filters a list of Fire objects to include only those located within a specific region.
     * Checks if the center of the fire lies within the region boundaries.
     * @param fires the list of fires to filter
     * @param region the region to filter by (can be null, resulting in empty list)
     * @return a filtered list of Fire objects
     */
    public List<Fire> filterFiresByRegion(List<Fire> fires, Region region) {
        if (fires == null || fires.isEmpty()) {
            return new ArrayList<>();
        }
        if (region == null || region.getBoundary() == null) {
            // If we are trying to filter by a region that doesn't exist, return empty
            return new ArrayList<>();
        }

        List<Fire> filtered = new ArrayList<>();
        for (Fire fire : fires) {
            if (isPointInRegion(fire.getCenter(), region.getBoundary())) {
                filtered.add(fire);
            }
        }
        return filtered;
    }

    /**
     * Checks if a coordinate is inside a complex region boundary.
     */
    private boolean isPointInRegion(Coordinate point, List<List<GeoPosition>> boundaries) {
        for (List<GeoPosition> polygon : boundaries) {
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

            if (path.contains(point.getLon(), point.getLat())) {
                return true;
            }
        }
        return false;
    }
}
