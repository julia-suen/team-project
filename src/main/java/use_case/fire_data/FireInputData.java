package use_case.fire_data;

import java.util.List;

/**
 * The input data for the fire analytics use case.
 * Represents a request that a user makes to the program by giving it a date and a day range
 */
public class FireInputData {
    private final List<String> provinces;
    private final String date;
    private final int dateRange;
    private final boolean isNationalOverview;

    public FireInputData(List<String> provinces, String date, int dateRange, boolean isNationalOverview) {
        this.date = date;
        this.dateRange = dateRange;
        this.isNationalOverview = isNationalOverview;
        this.provinces = provinces;
    }

    // FireInputData used for multi-region analysis, isNationalOverview and severityFilter not needed.
    public FireInputData(List<String> provinces, String date, int dateRange) {
        this(provinces, date, dateRange, false);
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

    public boolean isNationalOverview() {
        return isNationalOverview;
    }

}