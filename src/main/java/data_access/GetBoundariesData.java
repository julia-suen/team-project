//package data_access;
//
//import entities.Region;
//
///**
// * DAO Interface getting boundary data for provinces.
// */
//public interface GetBoundariesData {
//
//    /**
//     * Get region data by name.
//     * @param regionName Name of the region (e.g., "Ontario", "Canada")
//     * @return Region object with boundaries, or null if not found
//     */
//    Region getBoundariesData(String regionName) throws GetFireData.InvalidDataException;
//
//    Region getRegion(String regionName);
//
//}

// we prolly need to make this + connect with BoundariesDataAccess cuz rn fireInteractor relies on data access object
// BoundariesDataAccess instead of its interface
// will let whoever is in charge of it complete implementation since it's not rly in my scope of the project anymore