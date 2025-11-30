package use_case.national_overview;

public class NationalOverviewInputData {
    private final String date;
    private final int range;

    public NationalOverviewInputData(String date, int range) {
        this.date = date;
        this.range = range;
    }

    public String getDate() {
        return date;
    }

    public int getRange() {
        return range;
    }
}
