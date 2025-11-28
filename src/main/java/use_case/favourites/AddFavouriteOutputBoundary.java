package use_case.favourites;

import java.util.List;

/**
 * Usecase Interactor for Favourites.
 */
public interface AddFavouriteOutputBoundary {
    void presentSuccess(List<String> allFavourites);
    void presentFailure(String errorMessage);
    void presentLoginRequired();
}



