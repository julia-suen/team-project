package interface_adapter.favourites;

import use_case.favourites.AddFavouriteOutputBoundary;
import view.MainFrame;
import view.SidePanelView;
import controller.UserController;

import javax.swing.*;
import java.util.List;

public class FavouritesPresenter implements AddFavouriteOutputBoundary {
    private final SidePanelView sidePanelView;
    private final MainFrame mainFrame;
    private final UserController userController;

    public AddFavouriteInteractor(AddFavouriteOutputBoundary outputBoundary) {
        this.outputBoundary = outputBoundary;
    }

    /**
     * Sets the current logged-in user.
     * Should be called by UserController after successful login.
     * @param user the logged-in user
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @Override
    public void execute(AddFavouriteInputData inputData) {
        // Check if user is logged in
        if (currentUser == null) {
            outputBoundary.presentLoginRequired();
            return;
        }

        final String province = inputData.getProvince();

        // Validate input
        if (province == null || province.isEmpty() || province.equals("All")) {
            outputBoundary.presentFailure("Cannot add 'All' to favourites");
            return;
        }

        // Get province coordinates
        final GeoPosition provincePosition = ProvinceMapper.getCenter(province);
        if (provincePosition == null) {
            outputBoundary.presentFailure("Invalid province: " + province);
            return;
        }

        // Check if already a favorite
        if (currentUser.getFavoriteLocations().contains(provincePosition)) {
            outputBoundary.presentFailure("Province already in favourites");
            return;
        }

        // Add to user's favorites
        currentUser.addFavorite(provincePosition);

        // Convert GeoPositions to province names for display
        final List<String> provinceNames = new ArrayList<>();
        for (GeoPosition pos : currentUser.getFavoriteLocations()) {
            final String name = ProvinceMapper.getProvinceName(pos);
            if (name != null) {
                provinceNames.add(name);
            }
        }

        outputBoundary.presentSuccess(provinceNames);
    }
}
