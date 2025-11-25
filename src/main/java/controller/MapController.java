package controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.*;

import entities.Fire;
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
        mainFrame.getSidePanelView().getLoadFiresButton().addActionListener(evt -> loadFires(false));

        // National Overview
        mainFrame.getSidePanelView().getNationalButton().addActionListener(evt -> loadFires(true));
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
                if ("All".equalsIgnoreCase(selectedRange.toString())) {
                    range = MAX_RANGE_FOR_ALL;
                }
                else if (selectedRange != null) {
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

            mainFrame.getSidePanelView().getLoadFiresButton().setEnabled(false);
            mainFrame.getSidePanelView().getNationalButton().setEnabled(false);

            // Get the selected province
            String province = (String) mainFrame.getSidePanelView().getProvinceSelector().getSelectedItem();
            if (province == null) {
                province = "All";
            }

            // Pass it to the controller
            fireController.execute(date, range, isNational, province);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // These enable buttons immediately, which is generally safe, but safer inside invokeLater
        SwingUtilities.invokeLater(() -> {
            mainFrame.getSidePanelView().getLoadFiresButton().setEnabled(true);
            mainFrame.getSidePanelView().getNationalButton().setEnabled(true);

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
}