package model;

import java.time.LocalDate;

public class FilterSettings {

    private String province;
    private LocalDate startDate;
    private LocalDate endDate;

    public FilterSettings() {
        // Set default values
        this.province = "All";
        this.startDate = LocalDate.now().minusYears(1);
        this.endDate = LocalDate.now();
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
