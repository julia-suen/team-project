package usecase.compare;

import entities.Region;

/**
 * Data Access Interface for fetching boundary data required by the "Compare" use case.
 */
public interface CompareBoundaryDataAccess {

    /**
     * Retrieves the geographical boundary for a given province.
     * @param provinceName the name of the province
     * @return the Region object containing boundary polygons
     */
    Region getRegion(String provinceName);
}

