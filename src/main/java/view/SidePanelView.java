package view;

import java.awt.Component;
import java.awt.Dimension;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.DefaultComboBoxModel;

import org.jetbrains.annotations.NotNull;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import entities.Province;
import view.components.JComboCheckBox;

/**
 * The Side Panel View for user input and filtering.
 */
public class SidePanelView extends JPanel {

    private static final int MIN_YEAR = 2025;
    private static final int MIN_MONTH = 8;
    private static final int MIN_DAY = 1;

    private static final int PANEL_WIDTH = 250;
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 35;
    private static final int FIELD_HEIGHT = 30;
    private static final int BOX_SPACE = 5;
    private static final int SPACING_SMALL = 10;
    private static final int SPACING_MEDIUM = 15;

    private final JButton loadFiresButton = new JButton("Load Fires");
    private final JButton nationalButton = new JButton("National Overview");
    private final JButton resetButton = new JButton("Reset");
    private final JButton medSeverityButton = new JButton("Medium");
    private final JButton highSeverityButton = new JButton("High");
    private final JButton addFavouriteButton = new JButton("+ Add Favourite");
    private final JButton removeFavouritesButton = new JButton("- Clear Favourites");
    private final JButton logoutButton = new JButton("Logout");

    // Initialize province selector dynamically using the source of truth
    private final JComboBox<String> provinceSelector;
    private final JComboCheckBox provinceComboCheckBox;

    private final JComboBox<String> dayRangeSelector = new JComboBox<>(
            new String[]{"All", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"}
    );

    private final JComboBox<String> favouriteSelector;

    private final String[] provinceOptions = {
        "Alberta",
        "BC",
        "Manitoba",
        "New Brunswick",
        "Newfoundland & Labrador",
        "Nova Scotia",
        "Nunavut",
        "NWT",
        "Ontario",
        "PEI",
        "Quebec",
        "Saskatchewan",
        "Yukon"};

    private final DefaultComboBoxModel<String> favouriteModel;

    private final DatePicker datePicker;
    private final GraphPanel graphPanel;

    /**
     * Constructs the SidePanelView.
     */
    public SidePanelView() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(PANEL_WIDTH, 0));
        setBorder(BorderFactory.createTitledBorder("Filters"));

        // Initialize the Province Dropdown dynamically
        this.provinceSelector = createProvinceComboBox();
        this.provinceComboCheckBox = createProvinceComboCheckBox();

        final Dimension maxFieldSize = new Dimension(Integer.MAX_VALUE, FIELD_HEIGHT);

        datePicker = getDatePicker();
        datePicker.setMaximumSize(maxFieldSize);
        datePicker.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Initialize GraphPanel
        graphPanel = new GraphPanel();
        graphPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Initialise FavouritePanel
        favouriteModel = new DefaultComboBoxModel<>();
        favouriteModel.addElement("No favourites added yet");
        favouriteSelector = new JComboBox<>(favouriteModel);

        addComponents(maxFieldSize);
    }

    /**
     * Creates the province combo box populated from BoundariesDataAccess.
     * @return all province combo box to be added in the GUI
     */
    private JComboBox<String> createProvinceComboBox() {
        final List<String> provinces = new ArrayList<>();
        provinces.add("All");

        // Fetch from source of truth
        provinces.addAll(Arrays.asList(Province.ALL_PROVINCES));

        // Sort alphabetically for better UX (skipping "All" at index 0)
        Collections.sort(provinces.subList(1, provinces.size()));

        return new JComboBox<>(provinces.toArray(new String[0]));
    }

    /**
     * Creates the province combo check box populated from BoundariesDataAccess.
     * @return all province combo check box to be added in the GUI
     */
    private JComboCheckBox createProvinceComboCheckBox() {
        final List<JCheckBox> checkBoxList = new ArrayList<>();

        final List<String> provinces = new ArrayList<>();
        provinces.add("All");

        // Fetch from source of truth
        provinces.addAll(Arrays.asList(Province.ALL_PROVINCES));

        // Sort alphabetically for better UX (skipping "All" at index 0)
        Collections.sort(provinces.subList(1, provinces.size()));

        for (String province: provinces) {
            final JCheckBox provinceCheckBox = new JCheckBox(province);
            provinceCheckBox.setSelected(false);

            checkBoxList.add(provinceCheckBox);
        }

        final JCheckBox[] checkBoxItems = checkBoxList.toArray(new JCheckBox[0]);

        return new JComboCheckBox(checkBoxItems);
    }

    private void addComponents(Dimension maxFieldSize) {
        // Province
        final JLabel provinceLabel = new JLabel("Province:");
        provinceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(provinceLabel);

        provinceSelector.setMaximumSize(maxFieldSize);
        provinceSelector.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(provinceSelector);

        provinceComboCheckBox.setMaximumSize(maxFieldSize);
        provinceComboCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(provinceComboCheckBox);

        add(Box.createVerticalStrut(SPACING_MEDIUM));

        // Date
        final JLabel dateLabel = new JLabel("Date:");
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(dateLabel);
        add(datePicker);

        add(Box.createVerticalStrut(SPACING_MEDIUM));

        // Day Range
        final JLabel rangeLabel = new JLabel("Day Range:");
        rangeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(rangeLabel);

        dayRangeSelector.setMaximumSize(maxFieldSize);
        dayRangeSelector.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(dayRangeSelector);

        add(Box.createVerticalStrut(SPACING_MEDIUM));

        // Load and National Buttons
        addButtonPanel();

        // Severity
        final JLabel severityLabel = new JLabel("Severity Filter:");
        severityLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(severityLabel);

        add(Box.createVerticalStrut(SPACING_MEDIUM));

        // Severity Buttons
        addSeverityPanel();

        add(Box.createVerticalStrut(SPACING_MEDIUM));

        // Favourites panel
        final JLabel favouritesLabel = new JLabel("Quick Access: ");
        favouritesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(favouritesLabel);

        favouriteSelector.setMaximumSize(maxFieldSize);
        favouriteSelector.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(favouriteSelector);

        add(Box.createVerticalStrut(BOX_SPACE));

        // Favourite Buttons
        addFavouritesPanel();

        // add(Box.createVerticalStrut(SPACING_MEDIUM));

        // Graph at the bottom
        add(new JLabel("Trend Analysis:"));
        add(Box.createVerticalStrut(BOX_SPACE));
        add(graphPanel);

        add(Box.createVerticalStrut(BOX_SPACE));

        add(logoutButton);

        add(Box.createVerticalGlue());
    }

    private void addButtonPanel() {
        final JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        loadFiresButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadFiresButton.setMaximumSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));

        nationalButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        nationalButton.setMaximumSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));

        buttonPanel.add(loadFiresButton);
        buttonPanel.add(Box.createVerticalStrut(SPACING_SMALL));
        buttonPanel.add(nationalButton);
        buttonPanel.add(Box.createVerticalStrut(SPACING_SMALL));

        add(buttonPanel);
    }

    private void addSeverityPanel() {
        final JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        resetButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        resetButton.setMaximumSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));

        medSeverityButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        medSeverityButton.setMaximumSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));

        highSeverityButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        highSeverityButton.setMaximumSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));

        buttonPanel.add(resetButton);
        buttonPanel.add(Box.createVerticalStrut(SPACING_SMALL));
        buttonPanel.add(medSeverityButton);
        buttonPanel.add(Box.createVerticalStrut(SPACING_SMALL));
        buttonPanel.add(highSeverityButton);

        add(buttonPanel);
    }

    /**
     * Adds favourites panel to UI.
     * Currently missing dropdown
     */
    private void addFavouritesPanel() {
        final JPanel favouritesPanel = new JPanel();
        favouritesPanel.setLayout(new BoxLayout(favouritesPanel, BoxLayout.Y_AXIS));
        favouritesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        addFavouriteButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addFavouriteButton.setMaximumSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));

        removeFavouritesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        removeFavouritesButton.setMaximumSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));

        favouritesPanel.add(addFavouriteButton);
        favouritesPanel.add(Box.createVerticalStrut(SPACING_SMALL));
        favouritesPanel.add(removeFavouritesButton);
        favouritesPanel.add(Box.createVerticalStrut(SPACING_SMALL));

        add(favouritesPanel);
    }

    /**
     * Create calendar for user to pick date from.
     * Excluding dates before the minimum date and dates after the current date.
     * @return the date picker
     */
    @NotNull
    private static DatePicker getDatePicker() {
        final DatePickerSettings settings = new DatePickerSettings();

        settings.setFormatForDatesCommonEra(DateTimeFormatter.ofPattern("uuuu-MM-dd"));
        settings.setFormatsForParsing(new ArrayList<>(Arrays.asList(
                DateTimeFormatter.ofPattern("uuuu-MM-dd"),
                DateTimeFormatter.ofPattern("d/M/uuuu")
        )));

        final DatePicker datePicker = new DatePicker(settings);

        // exclude invalid dates/dates w/ data that isn't available
        final LocalDate minDate = LocalDate.of(MIN_YEAR, MIN_MONTH, MIN_DAY);
        final LocalDate maxDate = LocalDate.now();
        settings.setVetoPolicy(date -> !date.isBefore(minDate) && !date.isAfter(maxDate));

        datePicker.addDateChangeListener(event -> {
            final LocalDate date = datePicker.getDate();
            System.out.println("Date: " + date);
        });
        return datePicker;
    }

    public JButton getLoadFiresButton() {
        return loadFiresButton;
    }

    public JButton getNationalButton() {
        return nationalButton;
    }

    public JButton getResetButton() {
        return resetButton;
    }

    public JButton getMedSeverityButton() {
        return medSeverityButton;
    }

    public JButton getHighSeverityButton() {
        return highSeverityButton;
    }

    public JButton getFavouritesButton() {
        return addFavouriteButton;
    }

    public JButton getRemoveFavouritesButton() {
        return removeFavouritesButton;
    }

    public JButton getLogoutButton() {
        return logoutButton;
    }

    public JComboBox<String> getProvinceSelector() {
        return provinceSelector;
    }

    public JComboCheckBox getProvinceComboCheckBox() {
        return provinceComboCheckBox;
    }

    public JComboBox<String> getDayRangeSelector() {
        return dayRangeSelector;
    }

    public JComboBox<String> getFavouriteSelector() {
        return favouriteSelector;
    }

    public DatePicker getDatePickerComponent() {
        return datePicker;
    }

    public GraphPanel getGraphPanel() {
        return graphPanel;
    }
}