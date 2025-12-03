package usecase.marker;

import entities.Fire;
import java.util.List;

/**
 * Defines the contract for the Marker Interactor to retrieve data.
 */
public interface FireDisplayStateReader {
    /**
     * Retrieves the list of fires currently displayed on the map.
     */
    List<Fire> getDisplayedFires();
}