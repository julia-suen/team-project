package entities;

import java.util.Arrays;

/**
 * Enum class of all 13 Canadian provinces and territories.
 */
public enum Province {
    ALBERTA("Alberta"),
    BRITISH_COLUMBIA("British Columbia"),
    MANITOBA("Manitoba"),
    NEW_BRUNSWICK("New Brunswick"),
    NEWFOUNDLAND_AND_LABRADOR("Newfoundland and Labrador"),
    NOVA_SCOTIA("Nova Scotia"),
    ONTARIO("Ontario"),
    PRINCE_EDWARD_ISLAND("Prince Edward Island"),
    QUEBEC("Quebec"),
    SASKATCHEWAN("Saskatchewan"),
    NORTHWEST_TERRITORIES("Northwest Territories"),
    NUNAVUT("Nunavut"),
    YUKON("Yukon");

    private final String displayName;

    Province(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    // A constant list of all provinces and territories
    public static final String[] ALL_PROVINCES =
            Arrays.stream(values())
                    .map(Province::getDisplayName)
                    .toArray(String[]::new);
}