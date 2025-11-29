package app;

import controller.MapController;
import controller.UserController;
import data_access.BoundariesDataAccess;
import data_access.FireDataAccess;
import entities.FireFactory;
import interface_adapter.ViewManagerModel;
import interface_adapter.favourites.FavouritesController;
import interface_adapter.favourites.FavouritesPresenter;
import interface_adapter.fire_data.FireController;
import interface_adapter.fire_data.FirePresenter;
import interface_adapter.fire_data.FireViewModel;
import interface_adapter.region.RegionRepository;
import use_case.favourites.AddFavouriteInteractor;
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

            final MainFrame mainFrame = new MainFrame();
            final FireViewModel fireViewModel = new FireViewModel();
            final ViewManagerModel viewManagerModel = new ViewManagerModel();

            // Initialize Shared Data Access
            // Created once and shared so Interactor sees what Repo loads
            final BoundariesDataAccess boundariesAccess = new BoundariesDataAccess();

            // RegionRepository starts loading boundaries in background
            final RegionRepository regionRepository = new RegionRepository(boundariesAccess);

            final FireFactory factory = new FireFactory(Collections.emptyList());
            final FireDataAccess fireDataAccess = new FireDataAccess(factory);

            // Initialize Presenter first
            final FirePresenter firePresenter = new FirePresenter(fireViewModel, viewManagerModel);

            // Initialize Interactor with Shared Dependencies
            final FireInteractor fireInteractor = new FireInteractor(fireDataAccess, firePresenter, boundariesAccess);

            // Initialize Controllers
            final FireController fireController = new FireController(fireInteractor);

            // Initialising Favourites Controller, interactor & presenter
            final AddFavouriteInteractor favouritesInteractor = new AddFavouriteInteractor(null);
            final FavouritesController favouritesController = new FavouritesController(favouritesInteractor);
            final UserController userController = new UserController(mainFrame, favouritesInteractor);
            final FavouritesPresenter favouritesPresenter = new FavouritesPresenter(mainFrame.getSidePanelView(),
                    mainFrame,
                    userController);
            // Setting presenter in the interactor
            favouritesInteractor.setOutputBoundary(favouritesPresenter);

            final MapController mapController = new MapController(
                    mainFrame,
                    fireController,
                    fireViewModel,
                    favouritesController
            );

            mainFrame.setVisible(true);
        });
    }
}
