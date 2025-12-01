package usecase.national_overview;

import entities.Region;

/**
 * Data Access Interface for fetching boundary data required by the "National Overview" use case.
 */
public interface NationalOverviewBoundaryDataAccess {

    /**
     * Retrieves the region data for a specific area (e.g., "Canada").
     * @param name the name of the region
     * @return the Region object containing boundary polygons
     */
    Region getRegion(String name);
}
