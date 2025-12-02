package interface_adapter.favourites;

import usecase.favourites.FavouritesInputData;
import usecase.favourites.FavouritesInputBoundary;
import usecase.favourites.FavouritesAction;
import controller.UserController;

import javax.swing.*;

/**
 * Handles interaction with favourites button panel.
 */
public class FavouritesController {
    private final FavouritesInputBoundary favouritesInteractor;
    private final String[] provinceOptions;
    private final UserController userController;

    // Constructor
    public FavouritesController(FavouritesInputBoundary favouritesInteractor, String[] provinceOptions, UserController userController) {
        this.favouritesInteractor = favouritesInteractor;
        this.provinceOptions = provinceOptions;
        this.userController = userController;
    }

    /**
     * Shows pop-up for user to select province from dialogue box.
     */
    public void showAddFavouriteDialog(JFrame parentFrame) {
        // Check if current user is logged in or not
        if (userController.getCurrentUser() == null) {
            final int logout = JOptionPane.showConfirmDialog(
                    parentFrame,
                    "Login to add to favourites. Log in?",
                    "Login Required",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE
            );

            if (logout == JOptionPane.YES_OPTION) {
                userController.requestLogin();

                setCurrentUser(userController.getCurrentUser());

                // After log in, check if attempt is successful
                if (userController.getCurrentUser() != null) {
                    showProvinceSelector(parentFrame);
                }
            }
            return;
        }
        // If user is already logged in, show province selector
       showProvinceSelector(parentFrame);
    }

    public void showProvinceSelector(JFrame parentFrame) {
        final String selectedProvince = (String) JOptionPane.showInputDialog(
                parentFrame,
                "Select a province to add to favourites:",
                "Add Favourite",
                JOptionPane.PLAIN_MESSAGE,
                null,
                provinceOptions,
                provinceOptions[0]
        );

        if (selectedProvince != null) {
            final FavouritesInputData inputData = new FavouritesInputData(
                    selectedProvince,
                    FavouritesAction.ADD
            );
            favouritesInteractor.execute(inputData);
        }
    }

    /**
     * Clears all favourites.
     */

    public void clearFavourites() {
        final FavouritesInputData inputData = new FavouritesInputData(
                null,
                FavouritesAction.CLEAR
        );
        favouritesInteractor.execute(inputData);
    }
    /**
     * Gets list of current favourites.
     */

    public void getFavourites() {
        final FavouritesInputData inputData = new FavouritesInputData(
                null,
                FavouritesAction.GET
        );
        favouritesInteractor.execute(inputData);
    }

    public void setCurrentUser(entities.User user) {
        favouritesInteractor.setCurrentUser(user);
    }
}
