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
    private static final int MIN_DAY= 1;
	private final JButton loadFiresButton = new JButton("Load Fires");
	private final JComboBox<String> provinceSelector = new JComboBox<>(
			new String[]{"All", "Alberta", "British Columbia", "Ontario"}
	);

    private final JComboBox<String> dayRangeSelector = new JComboBox<>(
            new String[]{"All", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"}
    );

	public SidePanelView() {
		setLayout(new FlowLayout());
		setPreferredSize(new Dimension(250, 0));
		setBorder(BorderFactory.createTitledBorder("Filters"));

		add(new JLabel("Province:"));
		add(provinceSelector);

        add(new JLabel("Date:"));
        DatePicker datePicker = getDatePicker();
        add(datePicker);


        add(new JLabel("Day Range:"));
        add(dayRangeSelector);
        add(loadFiresButton);
	}

    /**
     * Create calendar for user to pick date from, excluding dates before the minimum date as specified in the private
     * static variables of this class, and dates after the current date.
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

	public JComboBox<String> getProvinceSelector() {
		return provinceSelector;
	}

    public JComboBox<String> getdateSelector() {
        return dayRangeSelector;
    }

    public JComboBox<String> getDayRangeSelectorSelector() {
        return dayRangeSelector;
    }
}
