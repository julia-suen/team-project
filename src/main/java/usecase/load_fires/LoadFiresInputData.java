package usecase.load_fires;

/**
 * Data structure representing the input for the "Load Fires" use case.
 */
public class LoadFiresInputData {
    private final String province;
    private final String date;
    private final int dateRange;

    /**
     * Constructs a LoadFiresInputData object
     * @param province  the name of the selected province (or "All")
     * @param date      the date string in YYYY-MM-DD format
     * @param dateRange the range of days to fetch data for
     */
    public LoadFiresInputData(String province, String date, int dateRange) {
        this.province = province;
        this.date = date;
        this.dateRange = dateRange;
    }

    /**
     * Gets the selected province.
     * @return the province name
     */
    public String getProvince() {
        return province;
    }

    /**
     * Gets the selected date.
     * @return the date string
     */
    public String getDate() {
        return date;
    }

    /**
     * Gets the date range.
     * @return the number of days
     */
    public int getDateRange() {
        return dateRange;
    }
}
