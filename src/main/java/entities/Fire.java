package entities;

import java.util.List;

/**
 * A simple entity representing a fire. Fires have a set of coordinates that act as its center,
 * points which constitute it, a radius, ... tbd
 */
public class Fire {

    private final float radius; // consider making int (?) depends on how u calc
    private final float[] center;
    private final float[][] coordinates;

    /**
     * Creates a new user with the given center point, points that constitute it, and radius.
     * @param radius the radius of the fire
     * @param center the center coordinates of the fire
     * @param coordinates the constituting points of the fire
     * @throws IllegalArgumentException if the password or name are empty
     */

    public Fire(float radius, float[] center, float[][] coordinates) {
        if (radius <= 0) {
            throw new IllegalArgumentException("Fire must have radius greater than 0.");
        }
        this.radius = radius;
        this.center = center;
        this.coordinates = coordinates;

    }

    public float getRadius() {
        return radius;
    }

    public float[] getCenter() {
        return center;
    }

    public float[][] getCoordinates() {
        return coordinates;
    }


}
