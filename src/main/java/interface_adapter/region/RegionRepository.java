package interface_adapter.region;

import data_access.BoundariesDataAccess;
import entities.Province;
import entities.Region;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.SwingWorker;

/**
 * Repository that loads and stores all Canadian province Regions.
 * This class fetches data from a {@link BoundariesDataAccess} source asynchronously
 * and provides access to the loaded {@link Region} objects.
 */
public class RegionRepository {

    private final BoundariesDataAccess dataAccess;
    private final Map<String, Region> regionMap = new HashMap<>();
    private volatile boolean isLoaded = false;
    private final List<Runnable> onLoadCallbacks = new CopyOnWriteArrayList<>();

    /**
     * Constructs a RegionRepository and immediately begins loading region data in the background.
     *
     * @param dataAccess The data access object used to fetch boundary data.
     */
    public RegionRepository(final BoundariesDataAccess dataAccess) {
        this.dataAccess = dataAccess;
        new LoadRegionsWorker().execute();
    }

    /**
     * Checks if the initial loading of all region data is complete.
     *
     * @return true if data is fully loaded, false otherwise.
     */
    public boolean isLoaded() {
        return this.isLoaded;
    }

    /**
     * Adds a callback to be executed when the region data has finished loading.
     * If the data is already loaded, the callback is executed immediately.
     *
     * @param callback The Runnable to execute upon completion.
     */
    public void addOnLoadCallback(final Runnable callback) {
        if (this.isLoaded) {
            callback.run();
        } else {
            this.onLoadCallbacks.add(callback);
        }
    }

    /**
     * Retrieves a specific Region by its name.
     *
     * @param name The name of the province or territory.
     * @return The corresponding {@link Region} object, or null if not found.
     */
    public Region getRegion(final String name) {
        return this.regionMap.get(name);
    }

    /**
     * Retrieves a collection of all loaded Region objects.
     *
     * @return A {@link Collection} of all available {@link Region} objects.
     */
    public Collection<Region> getAllRegions() {
        return this.regionMap.values();
    }

    /**
     * A private SwingWorker to handle the asynchronous loading of province boundary data.
     */
    private class LoadRegionsWorker extends SwingWorker<Void, Void> {
        @Override
        protected Void doInBackground() throws Exception {
            try {
                dataAccess.loadProvinces();
            } catch (Exception e) {
                System.err.println("Failed to load province boundaries: " + e.getMessage());
            }

            for (Province name : Province.values()) {
                try {
                    Region region = dataAccess.getRegion(name.getDisplayName());
                    if (region != null) {
                        regionMap.put(name.getDisplayName(), region);
                    }
                } catch (Exception e) {
                    System.err.println("Failed to load region " + name + ": " + e.getMessage());
                }
            }
            return null;
        }

        @Override
        protected void done() {
            isLoaded = true;
            System.out.println("All region data loaded.");
            for (Runnable callback : onLoadCallbacks) {
                callback.run();
            }
        }
    }
}