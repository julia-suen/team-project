package data_access;

import java.util.List;

import entities.Coordinate;

/**
 * DAO Interface getting all high confidence fire data points for a given date and time range.
 * Allows for flexibility in database used.
 */
public interface GetData {

    /**
     * Fetch fire data.
     * @param dateRange the range of dates to fetch data for
     * @param date the start date for the data fetch
     * @return a list of coordinates representing the fire data
     * @throws InvalidDataException if an issue occurs when parsing invalid data
     */
    List<Coordinate> getFireData(int dateRange, String date) throws InvalidDataException;

    /**
     * Fetch fire data for a specific bounding box.
     * @param dateRange the range of dates to fetch data for
     * @param date the start date for the data fetch
     * @param boundingBox Format: "minLon,minLat,maxLon,maxLat"
     * @return a list of coordinates representing the fire data inside the bounding box
     * @throws InvalidDataException if an issue occurs when parsing invalid data
     */
    List<Coordinate> getFireData(int dateRange, String date, String boundingBox) throws InvalidDataException;

    /**
     * Exception thrown when data is invalid.
     */
    class InvalidDataException extends Exception {
        /**
         * Constructs a new InvalidDataException.
         */
        public InvalidDataException() {
            super("Invalid data is unable to be processed.");
        }

    }
}
