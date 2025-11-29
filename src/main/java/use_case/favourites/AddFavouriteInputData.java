package use_case.favourites;

public class AddFavouriteInputData {
    private final String province;

    public AddFavouriteInputData(String province) {
        this.province = province;
    }

    public String getProvince() {
        return province;
    }
}
