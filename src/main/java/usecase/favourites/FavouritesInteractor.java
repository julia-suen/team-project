package usecase.favourites;

import java.util.ArrayList;
import java.util.List;

/**
 * Interactor for handling adding, getting, clearing favourites.
 */
public class FavouritesInteractor implements FavouritesInputBoundary {
    private final FavouritesOutputBoundary favouritesPresenter;
    private final List<String> favourites = new ArrayList<>();

    public FavouritesInteractor(FavouritesOutputBoundary favouritesPresenter) {
        this.favouritesPresenter = favouritesPresenter;
    }

    @Override
    public void execute(FavouritesInputData inputData) {
        final FavouritesAction action = inputData.action();

        switch (action) {
            case ADD:
                handleAdd(inputData.province());
                break;
            case GET:
                handleGet();
                break;
            case CLEAR:
                handleClear();
                break;
            default:
                favouritesPresenter.prepareFailureView("Unknown action");
        }
    }

    private void handleAdd(String province) {
        if (province == null) {
            return;
        }
        if (favourites.contains(province)) {
            favouritesPresenter.prepareFailureView("Province already added!");
            return;
        }
        favourites.add(province);
        final FavouritesOutputData outputData = new FavouritesOutputData(new ArrayList<>(favourites));
        favouritesPresenter.prepareSuccessView(outputData);
    }

    private void handleClear() {
        favourites.clear();
        final FavouritesOutputData outputData = new FavouritesOutputData(new ArrayList<>(favourites));
        favouritesPresenter.prepareSuccessView(outputData);
    }

    private void handleGet() {
        final FavouritesOutputData outputData = new FavouritesOutputData(new ArrayList<>(favourites));
        favouritesPresenter.prepareSuccessView(outputData);
    }
}
