package interface_adapter.favourites;

import java.util.List;

/**
 * Contains the updated list of favourite provinces.
 */
public record FavouritesOutputData(List<String> favourites) {
}
