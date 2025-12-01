package usecase.load_fires;

import java.util.List;

/**
 * Data structure representing the input for the "Load Fires" use case.
 */
public class LoadFiresInputData {
    private final List<String> provinces;
    private final String date;
    private final int dateRange;

    /**
     * Constructs a LoadFiresInputData object
     * @param provinces  the name of the selected province (or "All")
     * @param date      the date string in YYYY-MM-DD format
     * @param dateRange the range of days to fetch data for
     */
    public LoadFiresInputData(List<String> provinces, String date, int dateRange) {
        this.provinces = provinces;
        this.date = date;
        this.dateRange = dateRange;
    }

    /**
     * Gets the selected province.
     * @return the province name
     */
    public List<String> getProvinces() {
        return provinces;
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
