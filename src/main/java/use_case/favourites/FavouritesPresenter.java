package use_case.favourites;

import view.MainFrame;
import view.SidePanelView;

import controller.UserController;
import javax.swing.*;
import java.util.List;

public class FavouritesPresenter implements AddFavouriteOutputBoundary {
    private final SidePanelView sidePanelView;
    private final MainFrame mainFrame;
    private final UserController userController;

    public FavouritesPresenter(SidePanelView sidePanelView, MainFrame mainFrame, UserController userController) {
        this.sidePanelView = sidePanelView;
        this.mainFrame = mainFrame;
        this.userController = userController;
    }

    @Override
    public void presentSuccess(List<String> allFavourites) {
        sidePanelView.updateFavouritesList(allFavourites);
    }

    @Override
    public void presentFailure(String errorMessage) {
        JOptionPane.showMessageDialog(
                sidePanelView,
                errorMessage,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    @Override
    public void presentLoginRequired() {
        final int result = JOptionPane.showConfirmDialog(
                sidePanelView,
                "You must be logged in to add favourites. Go to login?",
                "Login Required",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            userController.requestLogin();
        }
    }
}
