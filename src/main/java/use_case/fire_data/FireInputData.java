package use_case.fire_data;

/**
 * The input data for the fire analytics use case.
 * Represents a request that a user makes to the program by giving it a date and a day range
 */
public class FireInputData {

    private final String date;
    private final int dateRange;
    private final boolean isNationalOverview;

    public FireInputData(String date, int dateRange, boolean isNationalOverview) {
        this.date = date;
        this.dateRange = dateRange;
        this.isNationalOverview = isNationalOverview;
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