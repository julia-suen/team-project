package app;

import controller.MapController;
import data_access.BoundariesDataAccess;
import data_access.FireDataAccess;
import entities.FireFactory;
import interface_adapter.ViewManagerModel;
import interface_adapter.favourites.FavouritesController;
import interface_adapter.favourites.FavouritesPresenter;
import interface_adapter.favourites.FavouritesViewModel;
import interface_adapter.fire_data.FireController;
import interface_adapter.fire_data.FirePresenter;
import interface_adapter.fire_data.FireViewModel;
import interface_adapter.region.RegionRepository;
import interface_adapter.severity_filter.SeverityController;
import interface_adapter.severity_filter.SeverityPresenter;
import use_case.favourites.FavouritesInteractor;
import use_case.fire_data.FireInteractor;
import use_case.severity_filter.SeverityInteractor;
import view.MainFrame;

import javax.swing.SwingUtilities;
import java.util.Collections;

/**
 * Initialize the GUI for the wildfire trend viewer.
 */
public class Main {
    private static final String[] PROVINCE_OPTIONS = {
        "Alberta",
        "British Columbia",
        "Manitoba",
        "Newfoundland and Labrador",
        "New Brunswick",
        "Nunavut",
        "Northwest Territories",
        "Nova Scotia",
        "Ontario",
        "Prince Edward Islands",
        "Quebec",
        "Saskatchewan",
        "Yukon",
    };

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            // Initialize Data Access (Frameworks/Drivers)
            final BoundariesDataAccess boundariesAccess = new BoundariesDataAccess();
            final FireFactory factory = new FireFactory(Collections.emptyList());
            final FireDataAccess fireDataAccess = new FireDataAccess(factory);

            // Initialize Interface Adapters & Services
            final RegionRepository regionRepository = new RegionRepository(boundariesAccess);
            final FireService fireService = new FireService();

            // Initialize ViewModels
            final FireViewModel fireViewModel = new FireViewModel();
            final ViewManagerModel viewManagerModel = new ViewManagerModel();

            // Initialize Presenters
            final FirePresenter firePresenter = new FirePresenter(fireViewModel, viewManagerModel);
            final SeverityPresenter severityPresenter = new SeverityPresenter(fireViewModel);

            // Initialize Interactors (Use Cases)
            final LoadFiresInteractor loadFiresInteractor = new LoadFiresInteractor(
                    fireDataAccess, boundariesAccess, firePresenter, fireService
            );

            final NationalOverviewInteractor nationalInteractor = new NationalOverviewInteractor(
                    fireDataAccess, boundariesAccess, firePresenter, fireService
            );

            final SeverityInteractor severityInteractor = new SeverityInteractor(severityPresenter);

            // Initialize Controllers
            final FireController fireController = new FireController(loadFiresInteractor, nationalInteractor);
            final SeverityController severityController = new SeverityController(severityInteractor);
            fireViewModel.addPropertyChangeListener(severityController);

            // load favourites use case presenter, interactor and controller
            final FavouritesViewModel favouritesViewModel = new FavouritesViewModel();
            final FavouritesPresenter favouritesPresenter = new FavouritesPresenter(favouritesViewModel);
            final FavouritesInteractor favouritesInteractor = new FavouritesInteractor(favouritesPresenter);
            final FavouritesController favouritesController = new FavouritesController(favouritesInteractor, PROVINCE_OPTIONS);

            // Initialize View
            final MainFrame mainFrame = new MainFrame(regionRepository);

            // Initialize Mediator/View Logic
            final MapController mapController = new MapController(
                    mainFrame,
                    fireController,
                    severityController,
                    favouritesController,
                    fireViewModel,
                    favouritesViewModel
            );

            mainFrame.setVisible(true);
        });
    }
}
