package use_case.favourites;

public interface FavouritesOutputBoundary {
    /**
     * Prepares success view for favourites use case.
     * @param outputData the output for success view.
     */
    void prepareSuccessView(FavouritesOutputData outputData);

    /**
     * Prepares failure view for favourites use case.
     * @param errorMessage the output on failure view.
     */
    void prepareFailureView(String errorMessage);
}

