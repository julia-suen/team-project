package view;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.optionalusertools.DateVetoPolicy;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

public class SidePanelView extends JPanel {

    private static final int MIN_YEAR = 2018;
    private static final int MIN_MONTH = 4;
    private static final int MIN_DAY = 1;
    private final JButton loadFiresButton = new JButton("Load Fires");
    private final JButton nationalButton = new JButton("National Overview (4 Years)");
    private final JComboBox<String> provinceSelector = new JComboBox<>(
            new String[]{"All", "Alberta", "British Columbia", "Ontario"}
    );

    private final JComboBox<String> dayRangeSelector = new JComboBox<>(
            new String[]{"All", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"}
    );

    private final DatePicker datePicker;


    public SidePanelView() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(250, 0));
        setBorder(BorderFactory.createTitledBorder("Filters"));

        Dimension maxFieldSize = new Dimension(Integer.MAX_VALUE, 30);

        // Province
        JLabel provinceLabel = new JLabel("Province:");
        provinceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(provinceLabel);

        provinceSelector.setMaximumSize(maxFieldSize);
        provinceSelector.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(provinceSelector);

        add(Box.createVerticalStrut(15));

        // Date
        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(dateLabel);

        datePicker = getDatePicker();
        datePicker.setMaximumSize(maxFieldSize);
        datePicker.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(datePicker);

        add(Box.createVerticalStrut(15));

        // Day Range
        JLabel rangeLabel = new JLabel("Day Range (Standard Only):");
        rangeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(rangeLabel);

        dayRangeSelector.setMaximumSize(maxFieldSize);
        dayRangeSelector.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(dayRangeSelector);

        add(Box.createVerticalStrut(30));

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        loadFiresButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadFiresButton.setMaximumSize(new Dimension(200, 35));

        nationalButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        nationalButton.setMaximumSize(new Dimension(200, 35));

        buttonPanel.add(loadFiresButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(nationalButton);

        add(buttonPanel);
        add(Box.createVerticalGlue());
    }

    /**
     * Create calendar for user to pick date from, excluding dates before the minimum date as specified in the private
     * static variables of this class, and dates after the current date.
     *
     * @return the date picker
     */
    @NotNull
    private static DatePicker getDatePicker() {
        DatePicker datePicker;
        DatePickerSettings settings = new DatePickerSettings();

        settings.setFormatForDatesCommonEra(DateTimeFormatter.ofPattern("uuuu-MM-dd"));
        settings.setFormatsForParsing(new ArrayList<>(Arrays.asList(
                DateTimeFormatter.ofPattern("uuuu-MM-dd"),
                DateTimeFormatter.ofPattern("d/M/uuuu")
        )));

        datePicker = new DatePicker(settings);

        // exclude invalid dates/dates w/ data that isn't available
        LocalDate minDate = LocalDate.of(MIN_YEAR, MIN_MONTH, MIN_DAY);
        LocalDate maxDate = LocalDate.now();
        settings.setVetoPolicy(date -> (!date.isBefore(minDate)) && !date.isAfter(maxDate));

        datePicker.addDateChangeListener(event -> {
            LocalDate date = datePicker.getDate();
            System.out.println("Date: " + date);
        });
        return datePicker;
    }

    public JButton getLoadFiresButton() {
        return loadFiresButton;
    }

    public JButton getNationalButton() { return nationalButton; }

    public JComboBox<String> getProvinceSelector() {
        return provinceSelector;
    }

    public JComboBox<String> getdateSelector() {
        return dayRangeSelector;
    }

    public JComboBox<String> getDayRangeSelector() { return dayRangeSelector; }

    public DatePicker getDatePickerComponent() { return datePicker; }
}