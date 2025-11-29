package controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Objects;

import javax.swing.*;

import entities.Fire;
import entities.SeverityFilter;
import interface_adapter.fire_data.FireController;
import interface_adapter.fire_data.FireState;
import interface_adapter.fire_data.FireViewModel;
import view.MainFrame;

public class MapController implements PropertyChangeListener {

    private static final int DEFAULT_RANGE = 1;
    private static final int MAX_RANGE_FOR_ALL = 10;

    private final MainFrame mainFrame;
    private final FireController fireController;
    private final FireViewModel fireViewModel;

    public MapController(MainFrame mainFrame, FireController fireController, FireViewModel fireViewModel) {
        this.mainFrame = mainFrame;
        this.fireController = fireController;
        this.fireViewModel = fireViewModel;

        this.fireViewModel.addPropertyChangeListener(this);

        addListeners();
    }

    private void addListeners() {
        // Standard Load
        mainFrame.getSidePanelView().getLoadFiresButton().addActionListener(evt ->
                loadFires(false, SeverityFilter.RESET));

        // National Overview
        mainFrame.getSidePanelView().getNationalButton().addActionListener(evt ->
                loadFires(true, SeverityFilter.RESET));

        // Reset
        mainFrame.getSidePanelView().getResetButton().addActionListener(evt ->
                loadFires(false, SeverityFilter.RESET));

        // Medium Severity
        mainFrame.getSidePanelView().getMedSeverityButton().addActionListener(evt ->
                loadFires(false, SeverityFilter.MEDIUM));

        // High Severity
        mainFrame.getSidePanelView().getHighSeverityButton().addActionListener(evt ->
                loadFires(false, SeverityFilter.HIGH));
    }

    private void loadFires(boolean isNational, SeverityFilter severityFilter) {
        final String date = mainFrame.getSidePanelView().getDatePickerComponent().getDateStringOrEmptyString();
        final Object selectedRange = mainFrame.getSidePanelView().getDayRangeSelector().getSelectedItem();

        if (date.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "Please select a date.");
        }
        else {
            int range;
            try {
                assert selectedRange != null;
                if ("All".equalsIgnoreCase(selectedRange.toString())) {
                    range = MAX_RANGE_FOR_ALL;
                }
                else if (!Objects.equals(selectedRange.toString(), "All")) {
                    range = Integer.parseInt(selectedRange.toString());
                }
                else {
                    range = DEFAULT_RANGE;
                }
            }
            catch (NumberFormatException ex) {
                // Fallback default
                range = DEFAULT_RANGE;
            }

            toggleButtons(false);

            // Get the selected province
            String province = (String) mainFrame.getSidePanelView().getProvinceSelector().getSelectedItem();
            if (province == null) {
                province = "All";
            }

            // Pass it to the controller
            fireController.execute(province, date, range, isNational, severityFilter);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // These enable buttons immediately, which is generally safe, but safer inside invokeLater
        SwingUtilities.invokeLater(() -> {
            toggleButtons(true);

            if ("state".equals(evt.getPropertyName())) {
                final FireState state = (FireState) evt.getNewValue();

                if (state.getError() != null) {
                    JOptionPane.showMessageDialog(mainFrame, state.getError(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                else {
                    // Pass the whole list of fires to be displayed at once
                    final List<Fire> fires = state.getFires();

                    // Update MapView on the EDT to avoid ConcurrentModificationException
                    mainFrame.getMapView().displayFires(fires);

                    // Update Graph
                    mainFrame.getSidePanelView().getGraphPanel().setData(state.getGraphData());
                }
            }
        });
    }

    private void toggleButtons(Boolean enabled) {
        mainFrame.getSidePanelView().getLoadFiresButton().setEnabled(enabled);
        mainFrame.getSidePanelView().getNationalButton().setEnabled(enabled);
        mainFrame.getSidePanelView().getResetButton().setEnabled(enabled);
        mainFrame.getSidePanelView().getMedSeverityButton().setEnabled(enabled);
        mainFrame.getSidePanelView().getHighSeverityButton().setEnabled(enabled);
    }
}