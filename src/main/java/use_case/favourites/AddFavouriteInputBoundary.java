package use_case.favourites;

public interface AddFavouriteInputBoundary {
    /**
     * Takes in province selection from controller to be implemented by AddFavouriteInteractor.
     * @param inputData - favourite province input data
     */
    void execute(AddFavouriteInputData inputData);
}

