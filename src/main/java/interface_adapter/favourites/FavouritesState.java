package interface_adapter.favourites;

import java.util.ArrayList;
import java.util.List;


/**
 * Holds list of favourite provinces.
 */
public class FavouritesState {
    private List<String> favourites = new  ArrayList<>();
    private String error;

    public List<String> getFavourites() {
        return favourites;
    }

    public void setFavourites(List<String> favourites) {
        this.favourites = new ArrayList<>(favourites);
    }

    public void addFavourite(String province) {
        if (!favourites.contains(province)) {
            favourites.add(province);
        }
    }

    public void clearFavourites() {
        favourites.clear();
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public boolean isEmpty() {
        return favourites.isEmpty();
    }
}
