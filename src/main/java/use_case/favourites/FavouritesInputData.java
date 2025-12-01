package use_case.favourites;

/**
 * Input data for Favourites use case.
 * Represents request to add or manage favourites.
 */
public record FavouritesInputData(String province, FavouritesAction action) {
}
