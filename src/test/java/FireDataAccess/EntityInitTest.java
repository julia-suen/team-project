package FireDataAccess;

import entities.Coordinate;
import entities.Fire;
import org.junit.jupiter.api.Test;
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
                    new String[] {"2025-11-23","N","n"}, new double[]{338.18, 270.05}, 4.69);
        });
    }

    /**
     * Tests if submitted invalid fire radius throws an InvalidArgumentException in the
     * Fire class.
     */
    @Test
    void testInvalidFire() {
        Coordinate coord = new Coordinate(59.13208,37.7947,
                new String[] {"2025-11-23","N","n"}, new double[]{338.18, 270.05}, 4.69);

        assertThrows(IllegalArgumentException.class, () -> {
            new Fire(0, coord, new ArrayList<>(List.of(coord)));
        });
    }
}