package interface_adapter.favourites;

public interface FavouritesInputBoundary {
    /**
     * Execute Favourites use case.
     * @param inputData the input data for given use case
     */
    void execute(FavouritesInputData inputData);
}
