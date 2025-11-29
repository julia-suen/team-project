package use_case.favourites;

import java.util.List;

/**
 * Usecase Interactor for Favourites.
 */
public interface AddFavouriteOutputBoundary {
    /**
     * Shows list of all provinces marked as favourite.
     * @param allFavourites - list of all favourites
     */
    void presentSuccess(List<String> allFavourites);

    /**
     * Prints error message if favourite not present.
     * @param errorMessage - description of error
     */
    void presentFailure(String errorMessage);

    /**
     * Ensures only logged-in users can access favourites.
     */
    void presentLoginRequired();
}
