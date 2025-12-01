package app;

import controller.MapController;
import controller.RegionalAnalysisController;
import data_access.BoundariesDataAccess;
import data_access.FireDataAccess;
import entities.FireFactory;
import interface_adapter.ViewManagerModel;
import interface_adapter.fire_data.FireController;
import interface_adapter.fire_data.FirePresenter;
import interface_adapter.fire_data.FireViewModel;
import interface_adapter.region.RegionRepository;
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

            // Initialize View
            final MainFrame mainFrame = new MainFrame(regionRepository);

            // Initialize Mediator/View Logic
            final MapController mapController = new MapController(
                    mainFrame,
                    fireController,
                    severityController,
                    fireViewModel
            );

            final RegionalAnalysisController regionalAnalysisController = new RegionalAnalysisController(mainFrame, loadFiresInteractor);

            mainFrame.setVisible(true);
        });
    }
}