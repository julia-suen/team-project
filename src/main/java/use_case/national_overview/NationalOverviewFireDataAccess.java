package use_case.national_overview;

import entities.Coordinate;
import java.util.List;

public interface NationalOverviewFireDataAccess {
    List<Coordinate> getFireData(int dateRange, String date, String boundingBox) throws Exception;
}
