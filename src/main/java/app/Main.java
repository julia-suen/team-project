package app;

import controller.MapController;
import data_access.BoundariesDataAccess;
import data_access.FireDataAccess;
import entities.FireFactory;
import interface_adapter.ViewManagerModel;
import interface_adapter.fire_data.FireController;
import interface_adapter.fire_data.FirePresenter;
import interface_adapter.fire_data.FireViewModel;
import interface_adapter.severity_filter.SeverityController;
import interface_adapter.severity_filter.SeverityPresenter;
import usecase.common.FireService;
import usecase.load_fires.LoadFiresInteractor;
import usecase.national_overview.NationalOverviewInteractor;
import usecase.severity_filter.SeverityInteractor;
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
            final BoundariesDataAccess boundariesAccess = new BoundariesDataAccess();

            final MainFrame mainFrame = new MainFrame(boundariesAccess);
            final FireViewModel fireViewModel = new FireViewModel();
            final ViewManagerModel viewManagerModel = new ViewManagerModel();

            final FireFactory factory = new FireFactory(Collections.emptyList());
            final FireDataAccess fireDataAccess = new FireDataAccess(factory);

            // Shared Service
            final FireService fireService = new FireService();

            // Setup Presenter (handles both use cases)
            final FirePresenter firePresenter = new FirePresenter(fireViewModel, viewManagerModel);

            // Setup Interactors
            final LoadFiresInteractor loadFiresInteractor = new LoadFiresInteractor(
                    fireDataAccess, boundariesAccess, firePresenter, fireService
            );

            final NationalOverviewInteractor nationalInteractor = new NationalOverviewInteractor(
                    fireDataAccess, boundariesAccess, firePresenter, fireService
            );

            // Setup Controller
            final FireController fireController = new FireController(loadFiresInteractor, nationalInteractor);

            // Load severity use case
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
