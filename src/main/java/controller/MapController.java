package controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JOptionPane;

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

        final String rangeStr;
        if (selectedRange != null) {
            rangeStr = selectedRange.toString();
        }
        else {
            rangeStr = "1";
        }

        if (date.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "Please select a date.");
        }
        else {
            int range;
            try {
                if ("All".equalsIgnoreCase(rangeStr)) {
                    range = MAX_RANGE_FOR_ALL;
                }
                else {
                    range = Integer.parseInt(rangeStr);
                }
            }
            catch (NumberFormatException ex) {
                // Fallback default
                range = DEFAULT_RANGE;
            }

            mainFrame.getSidePanelView().getLoadFiresButton().setEnabled(false);
            mainFrame.getSidePanelView().getNationalButton().setEnabled(false);

            fireController.execute(date, range, isNational);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
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
                mainFrame.getMapView().displayFires(fires);

                // Update Graph
                mainFrame.getSidePanelView().getGraphPanel().setData(state.getGraphData());
            }
        }
    }
}
