package app;

import controller.MapController;
import entities.FireFactory;
import data_access.FireDataAccess;
import interface_adapter.ViewManagerModel;
import interface_adapter.fire_data.FireController;
import interface_adapter.fire_data.FirePresenter;
import interface_adapter.fire_data.FireViewModel;
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

            // Data Access
            FireFactory factory = new FireFactory(Collections.emptyList());
            FireDataAccess dataAccess = new FireDataAccess(factory);

            FirePresenter firePresenter = new FirePresenter(fireViewModel, viewManagerModel);
            FireInteractor fireInteractor = new FireInteractor(dataAccess, firePresenter);
            FireController fireController = new FireController(fireInteractor);

            // Connect View Listeners via MapController
            MapController mapController = new MapController(
                    mainFrame,
                    fireController,
                    fireViewModel
            );

            mainFrame.setVisible(true);
        });
    }
}
