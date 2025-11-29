package FireDataAccess;

import entities.Coordinate;
import entities.FireFactory;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FireDataBundlingTest {

    /**
     * Tests if real data is bundled correctly into fires based on threshold.
     */
    @Test
    void correctlyBundlesReal() {

        List<Coordinate> testPoints = new ArrayList<>();

        loadRealDataPoints(testPoints);

        FireFactory fireFactory = new FireFactory(testPoints);
        List<List<Coordinate>> testFires = FireFactory.bundleDataPoints(fireFactory.getDataPoints());

        assertEquals(6, testFires.size());

    }

    /**
     * Tests if fake data is bundled correctly into fires based on threshold.
     */
    @Test
    void correctlyBundlesFake() {

        List<Coordinate> testPoints = new ArrayList<>();

        loadFakeDataPoints(testPoints);

        FireFactory fireFactory = new FireFactory(testPoints);
        List<List<Coordinate>> testFires = FireFactory.bundleDataPoints(fireFactory.getDataPoints());

        assertEquals(4,testFires.size());

    }

    /**
     * Loads a set of valid coordinates from the NASA Wildfire API with the date 2025-11-23 and day range 1.
     * @param testFires the list to load the coordinates in
     */

    private static void loadRealDataPoints(List<Coordinate> testFires) {

        testFires.add(new Coordinate(59.13208,37.7947,
                new String[] {"2025-11-23","N","n"}, new double[]{338.18, 270.05}, 4.69));
        testFires.add(new Coordinate(59.52758,34.14426,
                new String[] {"2025-11-23","N","n"}, new double[]{304.53, 267.37}, 1.73));
        testFires.add(new Coordinate(53.58578,31.95225,
                new String[] {"2025-11-23","N","n"}, new double[]{299.01, 270.69}, 0.51));
        testFires.add(new Coordinate(57.68446,28.2958,
                new String[] {"2025-11-23","N","n"}, new double[]{296.92, 268.13}, 0.58));
        testFires.add(new Coordinate(61.5803,1.54177,
                new String[] {"2025-11-23","N","n"}, new double[]{305.99, 273.69}, 0.96));
        testFires.add(new Coordinate(61.58565,1.54227,
                new String[] {"2025-11-23","N","n"}, new double[]{320.02, 276.62}, 0.96));
    }

    /**
     * Loads a set of fake coordinates for testing accurate parsing.
     * @param testFires the list to load the coordinates in
     */

    private static void loadFakeDataPoints(List<Coordinate> testFires) {

        testFires.add(new Coordinate(59.13208,37.7947,
                new String[] {"2025-11-23","N","n"}, new double[]{338.18, 270.05}, 4.69));
        testFires.add(new Coordinate(59.13258,37.7946,
                new String[] {"2025-11-23","N","n"}, new double[]{304.53, 267.37}, 1.73));
        testFires.add(new Coordinate(53.58578,31.95225,
                new String[] {"2025-11-23","N","n"}, new double[]{299.01, 270.69}, 0.51));
        testFires.add(new Coordinate(57.68446,28.2958,
                new String[] {"2025-11-23","N","n"}, new double[]{296.92, 268.13}, 0.58));
        testFires.add(new Coordinate(61.58503,1.54177,
                new String[] {"2025-11-23","N","n"}, new double[]{305.99, 273.69}, 0.96));
        testFires.add(new Coordinate(61.58565,1.54127,
                new String[] {"2025-11-23","N","n"}, new double[]{320.02, 276.62}, 0.96));
    }
}