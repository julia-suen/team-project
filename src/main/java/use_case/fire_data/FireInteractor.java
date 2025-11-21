package use_case.fire_data;

/**
 *  The Fire Interactor.
 */

import entities.Coordinate;
import entities.Fire;
import entities.FireFactory;
import fireapi.DataAccess;
import fireapi.GetData;

import java.util.List;

import static entities.FireFactory.bundleDataPoints;
import static entities.FireFactory.makeFireList;

public class FireInteractor implements FireInputBoundary {
    private final GetData dataAccessInterface;
    private final FireOutputBoundary firePresenter;

    public FireInteractor(GetData dataAccessInterface,
                          FireOutputBoundary fireOutputBoundary) {
        this.firePresenter = fireOutputBoundary;
        this.dataAccessInterface = dataAccessInterface;
    }

    /**
     * Executes the use case for analyzing fire data based off a day, date range and province provided. If no province
     * is provided, parse and display all data available.
     * @param fireInputData the input data for this use case
     */

    @Override
    public void execute(FireInputData fireInputData) throws GetData.InvalidDataException {
        final String date = fireInputData.getDate();
        final int dateRange = fireInputData.getdateRange();

        try {
            List<Coordinate> dataPoints = dataAccessInterface.getFireData(dateRange, date);
            FireFactory fireFactory = new FireFactory(dataPoints);
            List<List<Coordinate>> bundles = bundleDataPoints(fireFactory.getDataPoints());
            List<Fire> fires = makeFireList(bundles);

            final FireOutputData fireOutputData = new FireOutputData(fires);
            firePresenter.prepareSuccessView(fireOutputData);
        }
        catch (GetData.InvalidDataException error) {
            firePresenter.prepareFailView(String.valueOf(error));
        }


        // there's no return for this function! except should there be... NO it gets passed to presenter apparently
        // enter in julia data parse using the fire bundles to narrow down the list to a list of fire in the province IF
        // province is selected. otherwise, just use the fires variable on its own

        // and then insert a bunch of calls to like controllers and shi.... or like output views and like the output
        // boundaries to communicate the output info idk





    }
}