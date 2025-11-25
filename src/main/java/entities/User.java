package entities;

import java.util.HashSet;
import java.util.Set;

import org.jxmapviewer.viewer.GeoPosition;

public class User {
    private final String username;
    private final Set<GeoPosition> favoriteLocations;

    public User(String username) {
        this.username = username;
        this.favoriteLocations = new HashSet<>();
    }

    /**
     * Adds the given location to a ser's list of favorites.
     * @param location the location to be added
     */
    public void addFavorite(GeoPosition location) {
        this.favoriteLocations.add(location);
    }

    public String getUsername() {
        return username;
    }

    public Set<GeoPosition> getFavoriteLocations() {
        return favoriteLocations;
    }
}
