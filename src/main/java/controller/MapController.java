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
import interface_adapter.severity_filter.SeverityController;
import view.MainFrame;

public class MapController implements PropertyChangeListener {

    private static final int DEFAULT_RANGE = 1;
    private static final int MAX_RANGE_FOR_ALL = 10;

    private final MainFrame mainFrame;
    private final FireController fireController;
    private final SeverityController severityController;
    private final FireViewModel fireViewModel;

    public MapController(MainFrame mainFrame, FireController fireController, SeverityController severityController,
                         FireViewModel fireViewModel) {
        this.mainFrame = mainFrame;
        this.fireController = fireController;
        this.severityController = severityController;
        this.fireViewModel = fireViewModel;

        this.fireViewModel.addPropertyChangeListener(this);

        addListeners();
    }

    private void addListeners() {
        // Standard Load
        mainFrame.getSidePanelView().getLoadFiresButton().addActionListener(evt ->
                loadFires(false));

        // National Overview
        mainFrame.getSidePanelView().getNationalButton().addActionListener(evt ->
                loadFires(true));

        // Reset
        mainFrame.getSidePanelView().getResetButton().addActionListener(evt ->
                severityController.filterBySeverity(SeverityFilter.RESET));

        // Medium Severity
        mainFrame.getSidePanelView().getMedSeverityButton().addActionListener(evt ->
                severityController.filterBySeverity(SeverityFilter.MEDIUM));

        // High Severity
        mainFrame.getSidePanelView().getHighSeverityButton().addActionListener(evt ->
                severityController.filterBySeverity(SeverityFilter.HIGH));
    }

    private void loadFires(boolean isNational) {
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
            fireController.execute(province, date, range, isNational);
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
                    // Pass the list of display fires
                    final List<Fire> fires = state.getDisplayedFires();

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