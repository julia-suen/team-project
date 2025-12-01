package interface_adapter.favourites;

import use_case.favourites.FavouritesInputData;
import use_case.favourites.FavouritesInputBoundary;
import use_case.favourites.FavouritesAction;

import javax.swing.*;

/**
 * Handles interaction with favourites button panel.
 */
public class FavouritesController {
    private final FavouritesInputBoundary favouritesInteractor;
    private final String[] provinceOptions;

    // Constructor
    public FavouritesController(FavouritesInputBoundary favouritesInteractor, String[] provinceOptions) {
        this.favouritesInteractor = favouritesInteractor;
        this.provinceOptions = provinceOptions;
    }

    /**
     * Shows pop-up for user to select province from dialogue box.
     */
    public void showAddFavouriteDialog(JFrame parentFrame) {
        final String selectedProvince = (String) JOptionPane.showInputDialog(
                parentFrame,
                "Select province to add to favourite: ",
                "Add Favourite",
                JOptionPane.PLAIN_MESSAGE,
                null,
                provinceOptions,
                provinceOptions[0]
        );

        // If valid input
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
}
