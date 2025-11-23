
package use_case.fire_data;

import entities.Fire;
import entities.Coordinate;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Use Case: Compare wildfire activity between two provinces.
 * Compares:
 *  - Fire cluster count
 *  - Daily active fire trends
 */
public class CompareProvinceFiresInteractor {

    /**
     * Result structure returned by compare()
     */
    public static class Result {
        public final int fireCountA;
        public final int fireCountB;
        public final Map<LocalDate, Integer> trendA;
        public final Map<LocalDate, Integer> trendB;

        public Result(int fireCountA, int fireCountB,
                      Map<LocalDate, Integer> trendA,
                      Map<LocalDate, Integer> trendB) {

            this.fireCountA = fireCountA;
            this.fireCountB = fireCountB;
            this.trendA = trendA;
            this.trendB = trendB;
        }
    }

    /**
     * Compare two lists of fires and compute:
     *  - total fire count for each region
     *  - trend maps (date → number of fire clusters)
     */
    public Result compare(List<Fire> firesA, List<Fire> firesB) {

        int countA = firesA.size();
        int countB = firesB.size();

        Map<LocalDate, Integer> trendA = computeTrend(firesA);
        Map<LocalDate, Integer> trendB = computeTrend(firesB);

        return new Result(countA, countB, trendA, trendB);
    }

    /**
     * Compute trend: For each date, count how many coordinates inside Fire clusters appear.
     */
    private Map<LocalDate, Integer> computeTrend(List<Fire> fires) {
        Map<LocalDate, Integer> trend = new HashMap<>();

        for (Fire fire : fires) {
            for (Coordinate coord : fire.getCoordinates()) {
                String dateString = coord.getDateDayConfidence()[0];  // "YYYY-MM-DD"
                LocalDate date = LocalDate.parse(dateString);
                trend.merge(date, 1, Integer::sum);
            }
        }
        return trend;
    }
}
