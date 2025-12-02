package data_access;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for saving and loading user favourites.
 * Stores one file per user.
 */
public class FavouritesDataAccess {
    private static final String DATA_DIR = "data";
    private static final String FILE_PREFIX = "favourites_";
    private static final String FILE_EXT = ".txt";


    /**
     * Saves list of favourite provinces for every user.
     */
    public void saveFavourites(String username, List<String> favourites) throws IOException {
        final File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        // Create file path
        final String fileName = DATA_DIR + File.separator + FILE_PREFIX + sanitizeUsername(username) + FILE_EXT;
        final File file = new File(fileName);

        // Writing favourites to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String province : favourites) {
                writer.write(province);
                writer.newLine();
            }
        }
    }
    /**
     * Loads list of favourite provinces for a logged-in user.
     */
    public List<String> loadFromFavourites(String username) throws IOException {
        final String filename = DATA_DIR + File.separator + FILE_PREFIX + sanitizeUsername(username) + FILE_EXT;
        final File file = new File(filename);

        final List<String> favourites = new ArrayList<>();

        if (!file.exists()) {
            return favourites;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                final String province = line.trim();
                if (!province.isEmpty()) {
                    favourites.add(province);
                }
            }
        }
        return favourites;
    }
    /**
     * Cleans username using regex rules to be safe for use in filenames.
     * @param username the username to sanitize
     */
    private String sanitizeUsername(String username) {
        return username.replaceAll("[^a-zA-Z0-9_-]", "_");
    }
}