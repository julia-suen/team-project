package view;

import entities.Fire;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;

/**
 * A custom waypoint representing a fire, with a specific radius.
 */
public class FireWaypoint extends DefaultWaypoint {
    private final double radius;
    private final Fire fire;

    /**
     * Constructs a FireWaypoint.
     * @param coord The geographical coordinate.
     * @param radius The radius of the fire.
     */
    public FireWaypoint(final GeoPosition coord, final double radius, final Fire fire) {
        super(coord);
        this.radius = radius;
        this.fire = fire;
    }

    /**
     * Gets the radius of the fire.
     * @return the radius.
     */
    public double getRadius() {
        return this.radius;
    }

    /**
     * Gets the fire.
     * @return the fire.
     */
    public Fire getFire() {
        return this.fire;
    }
}
