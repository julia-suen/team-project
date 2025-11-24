package app;

import controller.MapController;
import data_access.BoundariesDataAccess;
import entities.FireFactory;
import data_access.FireDataAccess;
import interface_adapter.ViewManagerModel;
import interface_adapter.fire_data.FireController;
import interface_adapter.fire_data.FirePresenter;
import interface_adapter.fire_data.FireViewModel;
import interface_adapter.region.RegionRepository;
import use_case.fire_data.FireInteractor;
import view.MainFrame;

import javax.swing.SwingUtilities;
import java.util.Collections;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            MainFrame mainFrame = new MainFrame();
            FireViewModel fireViewModel = new FireViewModel();
            ViewManagerModel viewManagerModel = new ViewManagerModel();

            // Initialize Shared Data Access
            // Created once and shared so Interactor sees what Repo loads
            BoundariesDataAccess boundariesAccess = new BoundariesDataAccess();

            // RegionRepository starts loading boundaries in background
            RegionRepository regionRepository = new RegionRepository(boundariesAccess);

            FireFactory factory = new FireFactory(Collections.emptyList());
            FireDataAccess fireDataAccess = new FireDataAccess(factory);

            // Initialize Presenter first
            FirePresenter firePresenter = new FirePresenter(fireViewModel, viewManagerModel);

            // Initialize Interactor with Shared Dependencies
            FireInteractor fireInteractor = new FireInteractor(fireDataAccess, firePresenter, boundariesAccess);

            // Initialize Controllers
            FireController fireController = new FireController(fireInteractor);

            MapController mapController = new MapController(
                    mainFrame,
                    fireController,
                    fireViewModel
            );

            mainFrame.setVisible(true);
        });
    }
}
