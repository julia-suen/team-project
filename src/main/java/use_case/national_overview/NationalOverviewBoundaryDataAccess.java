package use_case.national_overview;

import entities.Region;

public interface NationalOverviewBoundaryDataAccess {
    Region getRegion(String name);
}
