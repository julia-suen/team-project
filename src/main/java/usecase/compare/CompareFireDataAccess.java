package usecase.compare;

import entities.Coordinate;
import java.util.List;

/**
 * Data Access Interface for fetching fire data required by the "Compare" use case.
 */
public interface CompareFireDataAccess {

    /**
     * Fetches raw fire coordinates for a specific date and range.
     * @param dateRange the number of days to look back
     * @param date      the start date in YYYY-MM-DD format
     * @return a list of raw Coordinate objects
     * @throws Exception if data fetching fails
     */
    List<Coordinate> getFireData(int dateRange, String date) throws Exception;
}

