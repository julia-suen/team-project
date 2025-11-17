package view;

import javax.swing.*;
import java.awt.*;

public class SidePanelView extends JPanel {

	private final JButton loadFiresButton = new JButton("Load Fires");
	private final JComboBox<String> provinceSelector = new JComboBox<>(
			new String[]{"All", "Alberta", "British Columbia", "Ontario"}
	);

	public SidePanelView() {
		setLayout(new FlowLayout());
		setPreferredSize(new Dimension(250, 0));
		setBorder(BorderFactory.createTitledBorder("Filters"));

		add(new JLabel("Province:"));
		add(provinceSelector);
		add(loadFiresButton);
	}

	public JButton getLoadFiresButton() {
		return loadFiresButton;
	}

	public JComboBox<String> getProvinceSelector() {
		return provinceSelector;
	}
}
