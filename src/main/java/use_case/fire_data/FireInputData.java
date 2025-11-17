package fire_data;
/**
 * The input data for the fire analytics use case
 * think of as a request that a user makes to the program, giving it a date and a day range
 */
public class FireInputData {

    private final String date;
    private final int dateRange ;

    public FireInputData(String date, int dateRange) {
        this.date = date;
        this.dateRange = dateRange;
    }

    String getDate() {
        return date;
    }

    int getdateRange() {
        return dateRange;
    }


}




