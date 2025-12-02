package usecase.compare;

import java.util.List;

public class CompareInputData {
    private final List<String> provinces;
    private final String date;
    private final int dateRange;

    public CompareInputData(List<String> provinces, String date, int dateRange) {
        this.provinces = provinces;
        this.date = date;
        this.dateRange = dateRange;
    }

    public List<String> getProvinces() {
        return provinces;
    }

    public String getDate() {
        return date;
    }

    public int getDateRange() {
        return dateRange;
    }
}
