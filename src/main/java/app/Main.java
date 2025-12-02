package app;

import controller.MapController;
import controller.RegionalAnalysisController;
import controller.UserController;
import data_access.BoundariesDataAccess;
import data_access.FavouritesDataAccess;
import data_access.FireDataAccess;
import entities.FireFactory;
import interface_adapter.ViewManagerModel;
import interface_adapter.favourites.FavouritesController;
import interface_adapter.favourites.FavouritesPresenter;
import interface_adapter.favourites.FavouritesViewModel;
import interface_adapter.fire_data.FireController;
import interface_adapter.fire_data.FirePresenter;
import interface_adapter.fire_data.FireViewModel;
import interface_adapter.marker.MarkerController;
import interface_adapter.marker.MarkerPresenter;
import interface_adapter.marker.MarkerViewModel;
import interface_adapter.region.RegionRepository;
import interface_adapter.severity_filter.SeverityController;
import interface_adapter.severity_filter.SeverityPresenter;
import usecase.favourites.FavouritesInteractor;
import usecase.compare.CompareInteractor;
import usecase.favourites.FavouritesInteractor;
import usecase.common.FireService;
import usecase.load_fires.LoadFiresInteractor;
import usecase.marker.MarkerInteractor;
import usecase.national_overview.NationalOverviewInteractor;
import usecase.severity_filter.SeverityInteractor;
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
        "Northwest Territories",
        "Nova Scotia",
        "New Brunswick",
        "Nunavut",
        "Ontario",
        "Prince Edward Island",
        "Quebec",
        "Saskatchewan",
        "Yukon"
    };

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            // Initialize Data Access (Frameworks/Drivers)
            final BoundariesDataAccess boundariesAccess = new BoundariesDataAccess();
            final FireFactory factory = new FireFactory(Collections.emptyList());
            final FireDataAccess fireDataAccess = new FireDataAccess(factory);
            final FavouritesDataAccess favouritesDataAccess = new FavouritesDataAccess();

            // Initialize Interface Adapters & Services
            final RegionRepository regionRepository = new RegionRepository(boundariesAccess);
            final FireService fireService = new FireService();

            // Initialize ViewModels
            final FireViewModel fireViewModel = new FireViewModel();
            final ViewManagerModel viewManagerModel = new ViewManagerModel();
            final MarkerViewModel markerViewModel = new MarkerViewModel();

            // Initialize Presenters
            final FirePresenter firePresenter = new FirePresenter(fireViewModel, viewManagerModel);
            final SeverityPresenter severityPresenter = new SeverityPresenter(fireViewModel);
            final MarkerPresenter markerPresenter = new MarkerPresenter(markerViewModel);

            // Initialize Interactors (Use Cases)
            final LoadFiresInteractor loadFiresInteractor = new LoadFiresInteractor(
                    fireDataAccess, boundariesAccess, firePresenter, fireService
            );

            final NationalOverviewInteractor nationalInteractor = new NationalOverviewInteractor(
                    fireDataAccess, boundariesAccess, firePresenter, fireService
            );

            final SeverityInteractor severityInteractor = new SeverityInteractor(severityPresenter);
            final MarkerInteractor markerInteractor = new MarkerInteractor(markerPresenter, fireViewModel);

            final CompareInteractor compareInteractor = new CompareInteractor(
                    fireDataAccess, boundariesAccess, fireService
            );

            // Initialize Controllers
            final FireController fireController = new FireController(loadFiresInteractor, nationalInteractor);
            final SeverityController severityController = new SeverityController(severityInteractor);
            final MarkerController markerController = new MarkerController(markerInteractor);
            fireViewModel.addPropertyChangeListener(severityController);

            // Initialise favourites use case presenter, interactor and view model
            final FavouritesViewModel favouritesViewModel = new FavouritesViewModel();
            final FavouritesPresenter favouritesPresenter = new FavouritesPresenter(favouritesViewModel);
            final FavouritesInteractor favouritesInteractor = new FavouritesInteractor(favouritesPresenter, favouritesDataAccess);

            // Initialize View
            final MainFrame mainFrame = new MainFrame(regionRepository, markerController, markerViewModel);

            final UserController userController = new UserController(mainFrame, false);
            final FavouritesController favouritesController = new FavouritesController(favouritesInteractor, PROVINCE_OPTIONS, userController);
            favouritesInteractor.setCurrentUser(userController.getCurrentUser());

            // Initialize Mediator/View Logic
            final MapController mapController = new MapController(
                    mainFrame,
                    fireController,
                    severityController,
                    favouritesController,
                    userController,
                    fireViewModel,
                    favouritesViewModel
            );

            final RegionalAnalysisController regionalAnalysisController = new RegionalAnalysisController(mainFrame, compareInteractor);

            mainFrame.setVisible(true);
        });
    }
}
