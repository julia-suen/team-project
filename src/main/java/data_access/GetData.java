package data_access;

import entities.Coordinate;
import java.util.List;

import entities.Coordinate;

/**
 * Defines the contract for a data access object (DAO) that retrieves fire data points.
 * This interface allows for flexibility in the data source implementation (e.g., API, database).
 */
public interface GetData {

    /**
     * Fetches a list of fire data coordinates for a given date and time range.
     *
     * @param dateRange The number of days to include in the data retrieval, counting back from the specified date.
     * @param date The end date for the data retrieval, typically in a 'YYYY-MM-DD' format.
     * @return A {@link List} of {@link Coordinate} objects representing high-confidence fire data points.
     * @throws InvalidDataException if an issue occurs during data fetching or parsing.
     */
    List<Coordinate> getFireData(int dateRange, String date) throws InvalidDataException;

    /**
     * An exception thrown when there is an issue with fetching or processing data.
     * This could be due to an invalid API response, a network error, or corrupted data.
     */
    class InvalidDataException extends Exception {
        /**
         * Constructs an InvalidDataException with a default error message.
         */
        public InvalidDataException() {
            super("Invalid data could not be processed due to a fetch or parse error.");
        }

        /**
         * Constructs an InvalidDataException with a custom error message.
         * @param message A detailed message explaining the error.
         */
        public InvalidDataException(final String message) {
            super(message);
        }
    }
}
