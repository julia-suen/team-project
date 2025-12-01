package interface_adapter.favourites;

public interface FavouritesOutputBoundary {
    /**
     * Prepares success view for favourites use case.
     * @param outputData the output for success view.
     */
    void preparesuccessView(FavouritesOutputData outputData);

    /**
     * Prepares failure view for favourites use case.
     * @param errorMessage the output on failure view.
     */
    void prepareFailureView(String errorMessage);
}
