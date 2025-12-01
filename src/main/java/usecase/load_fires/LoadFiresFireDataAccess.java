package usecase.load_fires;

import entities.Coordinate;
import java.util.List;

/**
 * Data Access Interface for fetching fire data required by the "Load Fires" use case.
 */
public interface LoadFiresFireDataAccess {

    /**
     * Fetches raw fire coordinates for a specific date and range.
     * @param dateRange the number of days to look back
     * @param date      the start date in YYYY-MM-DD format
     * @return a list of raw Coordinate objects
     * @throws Exception if data fetching fails
     */
    List<Coordinate> getFireData(int dateRange, String date) throws Exception;
}
