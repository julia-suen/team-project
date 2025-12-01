package usecase.national_overview;

import entities.Coordinate;
import java.util.List;

/**
 * Data Access Interface for fetching fire data required by the "National Overview" use case.
 */
public interface NationalOverviewFireDataAccess {

    /**
     * Fetches raw fire coordinates within a specific bounding box.
     * @param dateRange   the number of days to look back
     * @param date        the start date in YYYY-MM-DD format
     * @param boundingBox the geographical bounds to limit the search (e.g., world bounds)
     * @return a list of raw Coordinate objects
     * @throws Exception if data fetching fails
     */
    List<Coordinate> getFireData(int dateRange, String date, String boundingBox) throws Exception;
}
