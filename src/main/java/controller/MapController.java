package controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Objects;

import javax.swing.*;

import entities.Fire;
import entities.SeverityFilter;
import interface_adapter.favourites.FavouritesController;
import interface_adapter.favourites.FavouritesState;
import interface_adapter.favourites.FavouritesViewModel;
import interface_adapter.fire_data.FireController;
import interface_adapter.fire_data.FireState;
import interface_adapter.fire_data.FireViewModel;
import interface_adapter.severity_filter.SeverityController;
import view.MainFrame;
import view.components.JComboCheckBox;

public class MapController implements PropertyChangeListener {
    private boolean isUpdatingFavourites = false;
    private static final int DEFAULT_RANGE = 1;
    private static final int MAX_RANGE_FOR_ALL = 10;
    private static final String NO_FAVOURITES_MESSAGE = "No favourites added yet!";

    private final MainFrame mainFrame;
    private final FireController fireController;
    private final SeverityController severityController;
    private final FavouritesController favouritesController;
    private final FavouritesViewModel favouritesViewModel;
    private final FireViewModel fireViewModel;
    private final UserController userController;

    public MapController(MainFrame mainFrame, FireController fireController, SeverityController severityController,
                         FavouritesController favouritesController, UserController userController,
                         FireViewModel fireViewModel, FavouritesViewModel favouritesViewModel) {
        this.mainFrame = mainFrame;
        this.fireController = fireController;
        this.severityController = severityController;
        this.favouritesController = favouritesController;
        this.userController = userController;
        this.fireViewModel = fireViewModel;
        this.favouritesViewModel = favouritesViewModel;

        this.fireViewModel.addPropertyChangeListener(this);
        this.favouritesViewModel.addPropertyChangeListener(this);

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

        // Add Favourite
        mainFrame.getSidePanelView().getFavouritesButton().addActionListener(evt ->
                favouritesController.showAddFavouriteDialog(mainFrame));

        // Clear Favourites
        mainFrame.getSidePanelView().getRemoveFavouritesButton().addActionListener(evt ->
                favouritesController.clearFavourites());

        // Select Favourite
        mainFrame.getSidePanelView().getFavouriteSelector().addActionListener(evt -> {

            if (isUpdatingFavourites) {
                return;
            }

            final String selected = (String) mainFrame.getSidePanelView().getFavouriteSelector().getSelectedItem();
            if (selected != null && !selected.equals(NO_FAVOURITES_MESSAGE)) {
                loadFromFavourite(selected);
            }
        });

        // Log out
        mainFrame.getSidePanelView().getLogoutButton().addActionListener(evt -> {
            if (userController.getCurrentUser() == null) {
                JOptionPane.showMessageDialog(
                        mainFrame,
                        "You are logged out",
                        "Not logged in",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
            else {
                userController.logout();
                //Clear favourites from interactor
                favouritesController.setCurrentUser(null);
            }
        });
    }

    private void loadFires(boolean isNational) {
        final String date = mainFrame.getSidePanelView().getDatePickerComponent().getDateStringOrEmptyString();
        final Object selectedRange = mainFrame.getSidePanelView().getDayRangeSelector().getSelectedItem();

        if (date.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "Please select a date.");
            return;
        }

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
            range = DEFAULT_RANGE;
        }

        // Get selected provinces from the Multi-Select CheckBox
        List<JCheckBox> selectedBoxes = mainFrame.getSidePanelView().getProvinceComboCheckBox().getCheckedItems();
        String province = "";

        if (selectedBoxes.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "Please select a province.");
            return;
        }

        boolean allSelected = selectedBoxes.stream()
                .anyMatch(cb -> cb.getText().equalsIgnoreCase("All"));

        if (allSelected) {
            province = "All";
        }
        else if (selectedBoxes.size() > 1) {
            // Enforce single selection for "Load Fires"
            JOptionPane.showMessageDialog(mainFrame,
                    "Multiple provinces selected. Please use the 'Compare' button to view multiple regions, or select only one province.",
                    "Selection Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        else {
            province = selectedBoxes.get(0).getText();
        }

        toggleButtons(false);
        // Pass it to the controller
        fireController.execute(province, date, range, isNational);
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
            else if (FavouritesViewModel.FAVOURITES_UPDATED.equals(evt.getPropertyName())) {

                // Update the favourites dropdown
                final FavouritesState favouritesState = favouritesViewModel.getState();

                updateFavouritesDropdown(favouritesState);

                // show error if province already added
                if (favouritesState.getError() != null) {
                    JOptionPane.showMessageDialog(mainFrame,
                            favouritesState.getError(),
                            "Favourites Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void updateFavouritesDropdown(FavouritesState state) {

        // setting flag to prevent actionlistener from triggering
        isUpdatingFavourites = true;
        try {
            final JComboBox<String> favouritesSelector = mainFrame.getSidePanelView().getFavouriteSelector();
            final DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) favouritesSelector.getModel();
            model.removeAllElements();
            if (state.isEmpty()) {
                model.addElement(NO_FAVOURITES_MESSAGE);
            } else {
                for (String favourite : state.getFavourites()) {
                    model.addElement(favourite);
                }
            }
        }
        finally {
            isUpdatingFavourites = false;
        }
    }

    private void loadFromFavourite(String provinceName) {
        // Logic to set the specific item in the Multi-Select CheckBox
        JComboCheckBox box = mainFrame.getSidePanelView().getProvinceComboCheckBox();
        ComboBoxModel<JCheckBox> model = box.getModel();

        // Loop through and select only the favourite province
        for (int i = 0; i < model.getSize(); i++) {
            JCheckBox cb = model.getElementAt(i);
            cb.setSelected(cb.getText().equalsIgnoreCase(provinceName));
        }
        box.repaint();

        // Trigger load
        loadFires(false);
    }

    private void toggleButtons (Boolean enabled) {
        mainFrame.getSidePanelView().getLoadFiresButton().setEnabled(enabled);
        mainFrame.getSidePanelView().getNationalButton().setEnabled(enabled);
        mainFrame.getSidePanelView().getResetButton().setEnabled(enabled);
        mainFrame.getSidePanelView().getMedSeverityButton().setEnabled(enabled);
        mainFrame.getSidePanelView().getHighSeverityButton().setEnabled(enabled);
        mainFrame.getSidePanelView().getFavouritesButton().setEnabled(enabled);
    }
}
