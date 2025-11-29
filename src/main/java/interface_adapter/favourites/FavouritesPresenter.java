package interface_adapter.favourites;

import controller.UserController;
import use_case.favourites.AddFavouriteOutputBoundary;
import view.MainFrame;
import view.SidePanelView;
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

    /**
     * Updates SidePanel with favourites added by user.
     * @param allFavourites - list of all favourites
     */
    @Override
    public void presentSuccess(List<String> allFavourites) {
        sidePanelView.updateFavouritesList(allFavourites);
    }

    @Override
    public void presentFailure(String message) {
        JOptionPane.showMessageDialog(
                sidePanelView,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Redirects logged-out user to login screen on attempt to add to favourites.
     */
    @Override
    public void presentLoginRequired() {
        final int result = JOptionPane.showConfirmDialog(
                sidePanelView,
                "Login to add to favourites. Go to Login screen?",
                "Login required",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            userController.requestLogin();
        }
    }
}
