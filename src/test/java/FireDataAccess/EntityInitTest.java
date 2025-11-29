package FireDataAccess;

import entities.Coordinate;
import entities.Fire;
import entities.FireFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

class EntityInitTest {

    /**
     * Tests if submitted invalid latitude/longitude coordinates throws an InvalidArgumentException in the
     * Coordinate class.
     */
    @Test
    void testInvalidCoordinate() {

        assertThrows(IllegalArgumentException.class, () -> {
            new Coordinate(100, 100,
                    new String[]{"2025-11-23", "N", "n"}, new double[]{338.18, 270.05}, 4.69);
        });
    }

    /**
     * Tests if submitted invalid fire radius throws an InvalidArgumentException in the
     * Fire class.
     */
    @Test
    void testInvalidFire() {
        Coordinate coord = new Coordinate(59.13208, 37.7947,
                new String[]{"2025-11-23", "N", "n"}, new double[]{338.18, 270.05}, 4.69);

        assertThrows(IllegalArgumentException.class, () -> {
            new Fire(0, coord, new ArrayList<>(List.of(coord)));
        });
    }


    /**
     * Tests if a Coordinate is initialized properly with Brightness, and whether the getBrightness method works.
     */
    @Test
    void testBrightness() {
        Coordinate coord = new Coordinate(59.13208, 37.7947,
                new String[]{"2025-11-23", "N", "n"}, new double[]{338.18, 270.05}, 4.69);

        final double[] brightness = new double[]{338.18, 270.05};

        assertEquals(brightness[0], coord.getBrightness()[0]);
        assertEquals(brightness[1], coord.getBrightness()[1]);
    }

    /**
     * Tests if a Coordinate is initialized properly with FRP, and whether the getFrp method works.
     */
    @Test
    void testFrp() {
        Coordinate coord = new Coordinate(59.13208, 37.7947,
                new String[]{"2025-11-23", "N", "n"}, new double[]{338.18, 270.05}, 4.69);

        assertEquals(4.69, coord.getFrp());
    }

    /**
     * Tests if method GetRadius properly returns a Fire's radius.
     */
    @Test
    void testGetRadius() {

        Coordinate coord = new Coordinate(59.13208, 37.7947,
                new String[]{"2025-11-23", "N", "n"}, new double[]{338.18, 270.05}, 4.69);

        List<Coordinate> coordinates =  new ArrayList<>();
        coordinates.add(coord);

        final Fire testFire = new Fire(2.56, coord, coordinates);

        assertEquals(2.56, testFire.getRadius());
    }

    /**
     * Tests if method GetCenter properly returns a Fire's center point.
     */
    @Test
    void testGetCenter() {

        Coordinate coord = new Coordinate(59.13208, 37.7947,
                new String[]{"2025-11-23", "N", "n"}, new double[]{338.18, 270.05}, 4.69);

        List<Coordinate> coordinates =  new ArrayList<>();
        coordinates.add(coord);

        final Fire testFire = new Fire(2.56, coord, coordinates);

        assertEquals(coord, testFire.getCenter());
    }

    /**
     * Tests if method GetCoordinates properly returns a Fire's constituting points.
     */
    @Test
    void testGetCoordinates() {

        Coordinate coord = new Coordinate(59.13208, 37.7947,
                new String[]{"2025-11-23", "N", "n"}, new double[]{338.18, 270.05}, 4.69);

        List<Coordinate> coordinates =  new ArrayList<>();
        coordinates.add(coord);

        final Fire testFire = new Fire(2.56, coord, coordinates);

        assertEquals(coordinates, testFire.getCoordinates());
    }

}



