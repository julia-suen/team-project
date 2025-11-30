package use_case.load_fires;

import entities.Coordinate;
import java.util.List;

public interface LoadFiresFireDataAccess {
    List<Coordinate> getFireData(int dateRange, String date) throws Exception;
}
