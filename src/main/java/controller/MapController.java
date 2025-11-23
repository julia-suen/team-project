package controller;

import entities.Fire;
import interface_adapter.fire_data.FireController;
import interface_adapter.fire_data.FireState;
import interface_adapter.fire_data.FireViewModel;
import view.MainFrame;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

public class MapController implements PropertyChangeListener {

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
        mainFrame.getSidePanelView().getLoadFiresButton().addActionListener(e -> loadFires(false));

        // National Overview
        mainFrame.getSidePanelView().getNationalButton().addActionListener(e -> loadFires(true));
    }

    private void loadFires(boolean isNational) {
        String date = mainFrame.getSidePanelView().getDatePickerComponent().getDateStringOrEmptyString();
        Object selectedRange = mainFrame.getSidePanelView().getDayRangeSelector().getSelectedItem();
        String rangeStr = selectedRange != null ? selectedRange.toString() : "1";

        if (date.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "Please select a date.");
            return;
        }

        int range;
        try {
            if ("All".equalsIgnoreCase(rangeStr)) {
                range = 10;
            } else {
                range = Integer.parseInt(rangeStr);
            }
        } catch (NumberFormatException e) {
            range = 1; // Fallback default
        }

        mainFrame.getSidePanelView().getLoadFiresButton().setEnabled(false);
        mainFrame.getSidePanelView().getNationalButton().setEnabled(false);

        fireController.execute(date, range, isNational);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        mainFrame.getSidePanelView().getLoadFiresButton().setEnabled(true);
        mainFrame.getSidePanelView().getNationalButton().setEnabled(true);

        if ("state".equals(evt.getPropertyName())) {
            FireState state = (FireState) evt.getNewValue();

            if (state.getError() != null) {
                JOptionPane.showMessageDialog(mainFrame, state.getError(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Pass the whole list of fires to be displayed at once
            List<Fire> fires = state.getFires();
            mainFrame.getMapView().displayFires(fires);

            // Update Graph
            mainFrame.getGraphPanel().setData(state.getGraphData());
        }
    }
}