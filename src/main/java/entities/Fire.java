package entities;

import java.util.List;

/**
 * A simple entity representing a fire. Fires have a radius, a Coordinate object representing its center, and
 * points which constitute it.
 */

public class Fire {
    private final double radius;
    private final Coordinate center;
    private final List<Coordinate> coordinates;

    /**
     * Creates a new user with the given center point, points that constitute it, and radius.
     * @param radius the radius of the fire
     * @param center the center coordinates of the fire
     * @param coordinates the constituting points of the fire
     * @throws IllegalArgumentException if the fire radius is not greater than 0
     */

    public Fire(double radius, Coordinate center, List<Coordinate> coordinates) {
        if (radius <= 0) {
            throw new IllegalArgumentException("Fire must have radius greater than 0.");
        }
        this.radius = radius;
        this.center = center;
        this.coordinates = coordinates;
    }

    public double getRadius() {
        return radius;
    }

    public Coordinate getCenter() {
        return center;
    }

    public List<Coordinate> getCoordinates() {
        return coordinates;
    }

    public int getCoordinatesSize() {
        return coordinates.size();
    }

    public String getDate(){
        return center.getDate();
    }

    public double getFrp(){
        return center.getFrp();
    }
}
