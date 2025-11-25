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
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jetbrains.annotations.NotNull;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import data_access.BoundariesDataAccess;

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
    private static final int SPACING_LARGE = 30;

    private final JButton loadFiresButton = new JButton("Load Fires");
    private final JButton nationalButton = new JButton("National Overview");

    // Initialize province selector dynamically using the source of truth
    private final JComboBox<String> provinceSelector;

    private final JComboBox<String> dayRangeSelector = new JComboBox<>(
            new String[]{"All", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"}
    );

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

        final Dimension maxFieldSize = new Dimension(Integer.MAX_VALUE, FIELD_HEIGHT);

        datePicker = getDatePicker();
        datePicker.setMaximumSize(maxFieldSize);
        datePicker.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Initialize GraphPanel
        graphPanel = new GraphPanel();
        graphPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

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
        provinces.addAll(Arrays.asList(BoundariesDataAccess.PROVINCES));

        // Sort alphabetically for better UX (skipping "All" at index 0)
        Collections.sort(provinces.subList(1, provinces.size()));

        return new JComboBox<>(provinces.toArray(new String[0]));
    }

    private void addComponents(Dimension maxFieldSize) {
        // Province
        final JLabel provinceLabel = new JLabel("Province:");
        provinceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(provinceLabel);

        provinceSelector.setMaximumSize(maxFieldSize);
        provinceSelector.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(provinceSelector);

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

        add(Box.createVerticalStrut(SPACING_LARGE));

        // Buttons
        addButtonPanel();

        add(Box.createVerticalStrut(SPACING_MEDIUM));

        // Graph at the bottom
        add(new JLabel("Trend Analysis:"));
        add(Box.createVerticalStrut(BOX_SPACE));
        add(graphPanel);

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

        add(buttonPanel);
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

    public JComboBox<String> getProvinceSelector() {
        return provinceSelector;
    }

    public JComboBox<String> getDayRangeSelector() {
        return dayRangeSelector;
    }

    public DatePicker getDatePickerComponent() {
        return datePicker;
    }

    public GraphPanel getGraphPanel() {
        return graphPanel;
    }
}
