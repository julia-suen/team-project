package fireapi;

import java.util.List;

/**
 *  DAO Interface getting all high confidence fire data points for a given date and time range.
 *  Allows for flexibility in database used.
 */

public interface GetData {
    /**
     * Fetch fire data.
     * @return a list of coordinates and their given data
     * @throws InvalidDataException if an issue occurs when parsing invalid data
     */
    List<String> dataAccess() throws InvalidDataException;

    class InvalidDataException extends Exception {
        public InvalidDataException() {
            super("Invalid data is unable to be processed.");
        }

    }
}

