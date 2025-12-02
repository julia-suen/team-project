package usecase.favourites;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import data_access.FavouritesDataAccess;
import entities.User;

/**
 * Interactor for handling adding, getting, clearing favourites.
 */
public class FavouritesInteractor implements FavouritesInputBoundary {
    private final FavouritesOutputBoundary favouritesPresenter;
    private final FavouritesDataAccess favouritesDao;
    private User currentUser;
    private List<String> favourites = new ArrayList<>();

    public FavouritesInteractor(FavouritesOutputBoundary favouritesPresenter, FavouritesDataAccess favouritesDao) {
        this.favouritesPresenter = favouritesPresenter;
        this.favouritesDao = favouritesDao;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            loadFavouritesFromFile();
        }
        else {
            favourites.clear();
            final FavouritesOutputData outputData = new FavouritesOutputData(new ArrayList<>(favourites));
            favouritesPresenter.prepareSuccessView(outputData);
        }
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
        if (currentUser == null) {
            System.out.println("DEBUG: handleAdd called with province " + province);
            System.out.println("DEBUG: Current user is: " + (currentUser != null ? currentUser.getUsername() : "NULL"));  // NEW
            // System.out.println("DEBUG: handleAdd called with username " +  currentUser.getUsername());
            favouritesPresenter.prepareFailureView("Please log in to add favourite!");
            return;
        }
        if (favourites.contains(province)) {
            favouritesPresenter.prepareFailureView("Province already added!");
            return;
        }
        favourites.add(province);
        saveFavouritesToFile();
        final FavouritesOutputData outputData = new FavouritesOutputData(new ArrayList<>(favourites));
        favouritesPresenter.prepareSuccessView(outputData);
    }

    private void handleClear() {
        favourites.clear();
        saveFavouritesToFile();
        final FavouritesOutputData outputData = new FavouritesOutputData(new ArrayList<>(favourites));
        favouritesPresenter.prepareSuccessView(outputData);
    }

    private void handleGet() {
        final FavouritesOutputData outputData = new FavouritesOutputData(new ArrayList<>(favourites));
        favouritesPresenter.prepareSuccessView(outputData);
    }

    private void saveFavouritesToFile() {
        if (currentUser == null) {
            return;
        }
        else {
            try {
                favouritesDao.saveFavourites(currentUser.getUsername(), favourites);
            }
            catch (IOException e) {
                favouritesPresenter.prepareFailureView("Failed to save favourites: " + e.getMessage());
            }
        }
    }

    private void loadFavouritesFromFile() {
        if (currentUser == null) {
            return;
        }
        try {
            favourites = new ArrayList<>(favouritesDao.loadFromFavourites(currentUser.getUsername()));
            final FavouritesOutputData outputData = new FavouritesOutputData(new ArrayList<>(favourites));
            favouritesPresenter.prepareSuccessView(outputData);
        } catch (IOException e) {
            favourites = new ArrayList<>();
        }
    }
}
