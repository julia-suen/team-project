package interface_adapter.select_region;

import use_case.select_region.SelectRegionOutputBoundary;
import use_case.select_region.SelectRegionOutputData;

/**
 * The Presenter for the Select Region use case. It receives output from the
 * interactor and updates the ViewModel.
 */
public class SelectRegionPresenter implements SelectRegionOutputBoundary {

    private final SelectRegionViewModel selectRegionViewModel;

    /**
     * Constructs a SelectRegionPresenter.
     * @param selectRegionViewModel The ViewModel to update.
     */
    public SelectRegionPresenter(final SelectRegionViewModel selectRegionViewModel) {
        this.selectRegionViewModel = selectRegionViewModel;
    }

    @Override
    public void prepareSuccessView(final SelectRegionOutputData selectRegionOutputData) {
        this.selectRegionViewModel.setSelectedProvince(selectRegionOutputData.getProvinceName());
        this.selectRegionViewModel.firePropertyChanged();
    }
}
