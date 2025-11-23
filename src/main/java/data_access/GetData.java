package data_access;

import entities.Coordinate;

import java.util.List;

/**
 *  DAO Interface getting all high confidence fire data points for a given date and time range.
 *  Allows for flexibility in database used.
 */

public interface GetData {

    /**
     * Fetch fire data.
     * @throws InvalidDataException if an issue occurs when parsing invalid data
     */
    List<Coordinate> getFireData(int dateRange, String date) throws InvalidDataException;

    class InvalidDataException extends Exception {
        public InvalidDataException() {
            super("Invalid data is unable to be processed.");
        }

    }
}

