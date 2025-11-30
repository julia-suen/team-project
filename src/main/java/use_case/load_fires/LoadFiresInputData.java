package use_case.load_fires;

public class LoadFiresInputData {
    private final String province;
    private final String date;
    private final int dateRange;

    public LoadFiresInputData(String province, String date, int dateRange) {
        this.province = province;
        this.date = date;
        this.dateRange = dateRange;
    }

    public String getProvince() {
        return province;
    }

    public String getDate() {
        return date;
    }

    public int getDateRange() {
        return dateRange;
    }
}
