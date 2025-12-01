package app;

import controller.MapController;
import data_access.BoundariesDataAccess;
import data_access.FireDataAccess;
import entities.FireFactory;
import interface_adapter.ViewManagerModel;
import interface_adapter.favourites.FavouritesViewModel;
import interface_adapter.fire_data.FireController;
import interface_adapter.fire_data.FirePresenter;
import interface_adapter.fire_data.FireViewModel;
import interface_adapter.severity_filter.SeverityController;
import interface_adapter.severity_filter.SeverityPresenter;
import use_case.severity_filter.SeverityInteractor;
import interface_adapter.region.RegionRepository;
import use_case.fire_data.FireInteractor;
import view.MainFrame;

import javax.swing.SwingUtilities;
import java.util.Collections;

/**
 * Initialize the GUI for the wildfire trend viewer.
 */

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            // Initialize Shared Data Access
            // Created once and shared so Interactor sees what Repo loads
            final BoundariesDataAccess boundariesAccess = new BoundariesDataAccess();

            final MainFrame mainFrame = new MainFrame(boundariesAccess);
            final FireViewModel fireViewModel = new FireViewModel();
            final FavouritesViewModel favouritesViewModel = new FavouritesViewModel();
            final ViewManagerModel viewManagerModel = new ViewManagerModel();

            final FireFactory factory = new FireFactory(Collections.emptyList());
            final FireDataAccess fireDataAccess = new FireDataAccess(factory);

            // load basic fire data presenter, interactor, controller
            final FirePresenter firePresenter = new FirePresenter(fireViewModel, viewManagerModel);
            final FireInteractor fireInteractor = new FireInteractor(fireDataAccess, firePresenter, boundariesAccess);
            final FireController fireController = new FireController(fireInteractor);

            // load severity use case presenter, interactor and controller
            final SeverityPresenter severityPresenter = new SeverityPresenter(fireViewModel);
            final SeverityInteractor severityInteractor = new SeverityInteractor(severityPresenter);
            final SeverityController severityController = new SeverityController(severityInteractor);
            fireViewModel.addPropertyChangeListener(severityController);

            final MapController mapController = new MapController(
                    mainFrame,
                    fireController,
                    severityController,
                    fireViewModel
            );

            mainFrame.setVisible(true);
        });
    }
}
