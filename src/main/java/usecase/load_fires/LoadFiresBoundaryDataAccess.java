package usecase.load_fires;

import entities.Region;

/**
 * Data Access Interface for fetching boundary data required by the "Load Fires" use case.
 */
public interface LoadFiresBoundaryDataAccess {

    /**
     * Retrieves the geographical boundary for a given province.
     * @param provinceName the name of the province
     * @return the Region object containing boundary polygons
     */
    Region getRegion(String provinceName);
}
