package usecase.favourites;

import entities.User;

public interface FavouritesInputBoundary {
    /**
     * Execute Favourites use case.
     * @param inputData the input data for given use case
     */
    void execute(FavouritesInputData inputData);

    void setCurrentUser(User user);
}
