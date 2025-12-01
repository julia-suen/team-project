package usecase.national_overview;

/**
 * Data structure representing the input for the "National Overview" use case.
 */
public class NationalOverviewInputData {
    private final String date;
    private final int range;

    /**
     * Constructs a NationalOverviewInputData object.
     * @param date  the reference date string
     * @param range the day range for data fetching
     */
    public NationalOverviewInputData(String date, int range) {
        this.date = date;
        this.range = range;
    }

    /**
     * Gets the reference date.
     * @return the date string
     */
    public String getDate() {
        return date;
    }

    /**
     * Gets the range of days.
     * @return the day range
     */
    public int getRange() {
        return range;
    }
}
