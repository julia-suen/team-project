package use_case.favourites;

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
        System.out.println("DEBUG: handleAdd called with province: " + province);
        if (province == null) {
            return;
        }
        if (favourites.contains(province)) {
            favouritesPresenter.prepareFailureView("Province already added!");
            return;
        }
        System.out.println("DEBUG: Adding province to favourites");
        favourites.add(province);
        System.out.println("DEBUG: Current list of favourites : " + favourites);
        final FavouritesOutputData outputData = new FavouritesOutputData(new ArrayList<>(favourites));
        favouritesPresenter.prepareSuccessView(outputData);
        System.out.println("DEBUG: Calling prepareSuccessView");
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
