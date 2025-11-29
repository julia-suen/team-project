package use_case.favourites;

import entities.User;
import data_access.BoundariesDataAccess;
import org.jxmapviewer.viewer.GeoPosition;

import java.util.ArrayList;
import java.util.List;

public class AddFavouriteInteractor implements AddFavouriteInputBoundary {
    private AddFavouriteOutputBoundary outputBoundary;
    private User currentUser;

    public AddFavouriteInteractor(AddFavouriteOutputBoundary outputBoundary) {
        this.outputBoundary = outputBoundary;
    }

    /**
     * Sets output boundary for handling presenter dependency later.
     * @param outputBoundary - presenter
     */
    public void setOutputBoundary(AddFavouriteOutputBoundary outputBoundary) {
        this.outputBoundary = outputBoundary;
    }

    /**
     * Set the current user.
     * @param user - current user
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    /**
     * If current user not logged in, reroute to login screen.
     * @param inputData - favourite province input data
     */
    @Override
    public void execute(AddFavouriteInputData inputData) {
        // Validate user
        if (currentUser == null) {
            outputBoundary.presentLoginRequired();
            return;
        }
        final String province = inputData.getProvince();

        // Disallow empty province
        if (province == null || province.isEmpty()) {
            outputBoundary.presentFailure("Cannot favourite empty province");
            return;
        }

        // Get province coordinates access
        final GeoPosition provincePosition = BoundariesDataAccess.getProvinceCentre(province);

        // Check if position exists
        if (provincePosition == null) {
            outputBoundary.presentFailure("Invalid province" + province);
            return;
        }

        // Check if province has already been added to favourites
        if (currentUser.getFavoriteLocations().contains(provincePosition)) {
            outputBoundary.presentFailure("Province already added to favourites!");
            return;
        }

        // Add province to favourites since already checked for duplicates
        currentUser.addFavorite(provincePosition);

        // Convert GeoPositions to province names for display
        final List<String> provinceNames = new ArrayList<>();
        for (GeoPosition pos : currentUser.getFavoriteLocations()) {
            final String provinceName = BoundariesDataAccess.getProvinceName(pos);
            if (provinceName != null)
                provinceNames.add(provinceName);
        }
        outputBoundary.presentSuccess(provinceNames);
    }
}