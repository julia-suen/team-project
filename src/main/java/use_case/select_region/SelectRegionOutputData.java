package use_case.select_region;

/**
 * A data class to hold the output of the SelectRegion use case,
 * specifically the name of the province that was selected.
 */
public class SelectRegionOutputData {

    private final String provinceName;

    /**
     * Constructs a SelectRegionOutputData object.
     * @param provinceName The name of the selected province. Can be "None".
     */
    public SelectRegionOutputData(final String provinceName) {
        this.provinceName = provinceName;
    }

    /**
     * Gets the name of the selected province.
     * @return The province name.
     */
    public String getProvinceName() {
        return this.provinceName;
    }
}
