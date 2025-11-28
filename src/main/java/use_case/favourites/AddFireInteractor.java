package use_case.favourites;

import entities.ProvinceMapper;
import entities.User;
import org.jxmapviewer.viewer.GeoPosition;

import java.util.ArrayList;
import java.util.List;

public class AddFavouriteInteractor implements AddFavouriteInputBoundary {
    private final AddFavouriteOutputBoundary outputBoundary;
    private User currentUser;

    public AddFavouriteInteractor(AddFavouriteOutputBoundary outputBoundary) {
        this.outputBoundary = outputBoundary;
    }

    /**
     * For the current logged-in user.
     * Should be called by UserController after successful login.
     *
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
