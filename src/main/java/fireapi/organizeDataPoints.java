package fireapi;

public class organizeDataPoints {

    /**
     * fireapi.organizeDataPoints implementation that relies on the NRT VIIRS active fire API.
     * Note that all failures get reported as DataNotFoundException
     * exceptions to align with the requirements of the fireapi.dataAccess interface.
     */

    /** TO REPLACE WITH ACTUAL IMPLEMENTATION IF THE BELOW IS A GOOD IDEA ...
     * idea to further categorize fire data: there are many fire points for one single wildfire.
     * group up points likely to be single fires by checking for small discrepancies in coords
     *
     * then group all of those coords into one (set but not acc set) and set the center of the fire to the average
     * of the lats and longs of the coords
     *
     * the radius of the fire is the number of the points sqrted??? or scaled somehow in terms of the
     * number of data points attributed to that fire.
     */
}
