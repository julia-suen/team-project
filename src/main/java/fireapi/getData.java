package fireapi;

import java.util.List;

/**
 *  Interface getting all high confidence fire data points for a given date and time range.
 *  Allows for flexibility in database used.
 */

public interface getData {
    /**
     * Fetch fire data.
     * @return a hashmap of coordinates mapped to their given data
     * @throws InvalidDataException if an issue occurs when parsing invalid data
     */
    List<String> dataAccess() throws InvalidDataException;

    class InvalidDataException extends Exception {
        public InvalidDataException() {
            super("Invalid data is unable to be processed.");
        }

    }
}

