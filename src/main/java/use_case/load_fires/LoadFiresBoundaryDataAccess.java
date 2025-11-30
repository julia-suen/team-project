package use_case.load_fires;

import entities.Region;

public interface LoadFiresBoundaryDataAccess {
    Region getRegion(String provinceName);
}
