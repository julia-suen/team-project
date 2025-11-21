package app;

import controller.DataFetchController;
import controller.MapController;
import controller.UserController;
import view.MainFrame;
import javax.swing.SwingUtilities;

public class Main {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {

      MainFrame mainFrame = new MainFrame();

      DataFetchController dataFetcher = new DataFetchController();
      MapController mapController = new MapController(
        mainFrame.getMapView(),
        mainFrame.getSidePanelView(),
        dataFetcher
      );

      UserController userController = new UserController(mainFrame);

      mainFrame.setVisible(true);
    });
  }
}
