package interface_adapter.favourites;

import use_case.favourites.FavouritesOutputBoundary;
import use_case.favourites.FavouritesOutputData;

/**
 * Updates FavouritesViewModel when Favourites change.
 */
public class FavouritesPresenter implements FavouritesOutputBoundary {

    private final FavouritesViewModel favouritesViewModel;

    public FavouritesPresenter(FavouritesViewModel favouritesViewModel) {
        this.favouritesViewModel = favouritesViewModel;
    }

    @Override
    public void prepareSuccessView(FavouritesOutputData outputData) {
        final FavouritesState state = favouritesViewModel.getState();
        state.setFavourites(outputData.favourites());
        state.setError(null);

        favouritesViewModel.setState(state);
        favouritesViewModel.firePropertyChange();
    }

    @Override
    public void prepareFailureView(String errorMessage) {
        final FavouritesState state = favouritesViewModel.getState();
        state.setError(errorMessage);

        favouritesViewModel.setState(state);
        favouritesViewModel.firePropertyChange();
    }
}
