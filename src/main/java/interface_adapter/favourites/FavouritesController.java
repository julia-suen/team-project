package interface_adapter.favourites;

import use_case.favourites.AddFavouriteInputBoundary;
import use_case.favourites.AddFavouriteInputData;

public class FavouritesController {
    private final AddFavouriteInputBoundary addFavouriteInteractor;

    public FavouritesController(AddFavouriteInputBoundary addFavouriteInteractor) {
        this.addFavouriteInteractor = addFavouriteInteractor;
    }

    public void addFavourite(String province) {
        addFavouriteInteractor.execute(new AddFavouriteInputData(province));
    }

}
