package interface_adapter.fire_data;

import interface_adapter.ViewManagerModel;
import use_case.fire_data.FireOutputBoundary;
import use_case.fire_data.FireOutputData;

/**
 * The presenter for the fire analysis use case.
 */

public class FirePresenter implements FireOutputBoundary {

    private final FireViewModel fireViewModel;
    private final ViewManagerModel viewManagerModel;

    public FirePresenter(FireViewModel fireViewModel, ViewManagerModel viewManagerModel) {
        this.fireViewModel = fireViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void prepareSuccessView(FireOutputData response) {
        //this.fireViewModel.firePropertyChange();

        // switch to the fire data view
        //this.viewManagerModel.setState(FireViewModel.getViewName());
        this.viewManagerModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String error) {
        //fireViewModel.firePropertyChange();
    }

}
