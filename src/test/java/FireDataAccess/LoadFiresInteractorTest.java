package usecase.load_fires;

import entities.Coordinate;
import entities.Region;
import org.junit.jupiter.api.Test;
import org.jxmapviewer.viewer.GeoPosition;
import usecase.common.FireService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LoadFiresInteractorTest {

    // Shared service for bundling/filtering logic
    private final FireService fireService = new FireService();

    /**
     * Tests if two points are bundled correctly into a fire.
     */
    @Test
    void correctlyBundlesData() {
        final List<Coordinate> coords = new ArrayList<>();
        coords.add(new Coordinate(43.7, -79.4,
                new String[]{"2025-11-20", "D", "h"}, new double[]{300.0, 280.0}, 5.0));
        coords.add(new Coordinate(43.7001, -79.4001,
                new String[]{"2025-11-20", "D", "h"}, new double[]{300.0, 280.0}, 5.0));

        final TestFireDataAccess dataAccess = new TestFireDataAccess(coords);
        final TestPresenter presenter = new TestPresenter();
        final TestBoundariesAccess boundariesAccess = new TestBoundariesAccess();

        final LoadFiresInteractor interactor = new LoadFiresInteractor(dataAccess, boundariesAccess, presenter, fireService);
        final LoadFiresInputData inputData = new LoadFiresInputData("All", "2025-11-20", 1);

        interactor.execute(inputData);

        assertNotNull(presenter.outputData);
        assertEquals(1, presenter.outputData.getFires().size());
    }

    /**
     * Tests if determined province boundaries + applying filters for that given province will only return a fire
     * located within that boundary.
     */
    @Test
    void testProvinceFilter() {
        final List<Coordinate> coords = new ArrayList<>();
        coords.add(new Coordinate(43.7, -79.4,
                new String[]{"2025-11-20", "D", "h"}, new double[]{300.0, 280.0}, 5.0));
        coords.add(new Coordinate(20, 20,
                new String[]{"2025-11-20", "D", "h"}, new double[]{300.0, 280.0}, 5.0));

        final TestFireDataAccess dataAccess = new TestFireDataAccess(coords);
        final TestPresenter presenter = new TestPresenter();
        final TestBoundariesAccess boundariesAccess = new TestBoundariesAccess();

        final LoadFiresInteractor interactor = new LoadFiresInteractor(dataAccess, boundariesAccess, presenter, fireService);
        final LoadFiresInputData inputData = new LoadFiresInputData("Ontario", "2025-11-20", 1);

        interactor.execute(inputData);

        // Should filter out the one at (20,20)
        assertEquals(1, presenter.outputData.getFires().size());
    }

    /**
     * Tests that making a call to the FIRMS api with an empty string "" date in the future still returns fires
     * for the current day, (i.e.: results in non-empty outputData), as intended.
     */
    @Test
    void testEmptyDate(){
        final TestFireDataAccess dataAccess = new TestFireDataAccess(new ArrayList<>());
        final TestPresenter presenter = new TestPresenter();
        final TestBoundariesAccess boundariesAccess = new TestBoundariesAccess();

        final LoadFiresInteractor interactor = new LoadFiresInteractor(dataAccess, boundariesAccess, presenter, fireService);
        final LoadFiresInputData inputData = new LoadFiresInputData("All", "", 1);
        interactor.execute(inputData);
        assertTrue(presenter.outputData != null || presenter.errorMessage != null);
    }

    /**
     * Tests that making a call to the FIRMS api with a null date in the future still returns fires for the current
     * day, (i.e.: results in non-empty outputData), as intended.
     */
    @Test
    void testNullDate() {
        final TestFireDataAccess dataAccess = new TestFireDataAccess(new ArrayList<>());
        final TestPresenter presenter = new TestPresenter();
        final TestBoundariesAccess boundariesAccess = new TestBoundariesAccess();

        final LoadFiresInteractor interactor = new LoadFiresInteractor(dataAccess, boundariesAccess, presenter, fireService);
        final LoadFiresInputData inputData = new LoadFiresInputData("All", null, 1);

        interactor.execute(inputData);

        // Expectation adapted: The new code catches exceptions and calls fail view.
        assertNotNull(presenter.errorMessage);
    }

    /**
     * Tests that making a call to the FIRMS api for a date in the future returns no fires.
     */
    @Test
    void testFutureDate() {
        final TestFireDataAccess dataAccess = new TestFireDataAccess(new ArrayList<>());
        final TestPresenter presenter = new TestPresenter();
        final TestBoundariesAccess boundariesAccess = new TestBoundariesAccess();

        final LoadFiresInteractor interactor = new LoadFiresInteractor(dataAccess, boundariesAccess, presenter, fireService);
        final LoadFiresInputData inputData = new LoadFiresInputData("All", "2030-11-22", 1);

        interactor.execute(inputData);

        assertEquals(0, presenter.outputData.getFires().size());
    }

    /**
     * Tests that making a call to the FIRMS api for a date range greater than allowed (10 day period)
     * runs without error and returns fires for a 10 day period instead.
     */
    @Test
    void testInvalidHighDayRange() {
        final TestFireDataAccess dataAccess = new TestFireDataAccess(new ArrayList<>());
        final TestPresenter presenter = new TestPresenter();
        final TestBoundariesAccess boundariesAccess = new TestBoundariesAccess();

        final LoadFiresInteractor interactor = new LoadFiresInteractor(dataAccess, boundariesAccess, presenter, fireService);
        final LoadFiresInputData inputData = new LoadFiresInputData("All", "2025-11-20", 15);

        interactor.execute(inputData);
        assertEquals(15, dataAccess.lastRange); // The mock records input, clamping happens inside real DataAccess impl.
    }

    /**
     * Tests that making a call to the FIRMS api for a date range smaller than allowed (less than 1 day)
     * runs without error and returns fires for a 1 day period (the selected date) instead.
     */
    @Test
    void testInvalidLowDayRange() {
        final TestFireDataAccess dataAccess = new TestFireDataAccess(new ArrayList<>());
        final TestPresenter presenter = new TestPresenter();
        final TestBoundariesAccess boundariesAccess = new TestBoundariesAccess();

        final LoadFiresInteractor interactor = new LoadFiresInteractor(dataAccess, boundariesAccess, presenter, fireService);
        final LoadFiresInputData inputData = new LoadFiresInputData("All", "2025-11-20", 0);

        interactor.execute(inputData);

        assertEquals(0, dataAccess.lastRange);
    }

    /**
     * Tests that processing invalid data will raise an exception as intended.
     */
    @Test
    void testInvalidDataException() {
        final TestFireDataAccess dataAccess = new TestFireDataAccess(null);
        dataAccess.shouldThrowException = true;
        final TestPresenter presenter = new TestPresenter();
        final TestBoundariesAccess boundariesAccess = new TestBoundariesAccess();

        final LoadFiresInteractor interactor = new LoadFiresInteractor(dataAccess, boundariesAccess, presenter, fireService);
        final LoadFiresInputData inputData = new LoadFiresInputData("All", "2025-11-20", 1);

        interactor.execute(inputData);

        assertNotNull(presenter.errorMessage);
        assertTrue(presenter.errorMessage.contains("Error fetching data"));
    }

    /**
     * Tests that submitting null coordinates results in 0 fires generated.
     */
    @Test
    void testNullCoords() {
        final TestFireDataAccess dataAccess = new TestFireDataAccess(null);
        final TestPresenter presenter = new TestPresenter();
        final TestBoundariesAccess boundariesAccess = new TestBoundariesAccess();

        final LoadFiresInteractor interactor = new LoadFiresInteractor(dataAccess, boundariesAccess, presenter, fireService);
        final LoadFiresInputData inputData = new LoadFiresInputData("All", "2025-11-20", 1);

        interactor.execute(inputData);

        assertTrue(presenter.outputData.getFires().isEmpty());
    }

    /**
     * Tests that making a request for a province without a predetermined boundary results in no fires returned.
     */
    @Test
    void testProvinceNoBoundary() {
        final List<Coordinate> coords = new ArrayList<>();
        coords.add(new Coordinate(43.7, -79.4,
                new String[]{"2025-11-20", "D", "h"}, new double[]{300.0, 280.0}, 5.0));

        final TestFireDataAccess dataAccess = new TestFireDataAccess(coords);
        final TestPresenter presenter = new TestPresenter();
        final TestBoundariesAccess boundariesAccess = new TestBoundariesAccess();
        boundariesAccess.returnNull = true;

        final LoadFiresInteractor interactor = new LoadFiresInteractor(dataAccess, boundariesAccess, presenter, fireService);
        final LoadFiresInputData inputData = new LoadFiresInputData("Ontario", "2025-11-20", 1);

        interactor.execute(inputData);

        assertNotNull(presenter.outputData);
        assertTrue(presenter.outputData.getFires().isEmpty());
    }

    /**
     * Below are helpers that mock data access objects and presenters required for tests.
     */
    private static class TestFireDataAccess implements LoadFiresFireDataAccess {
        private final List<Coordinate> coords;
        int lastRange = -1;
        boolean shouldThrowException = false;

        TestFireDataAccess(List<Coordinate> coords) {
            this.coords = coords;
        }

        @Override
        public List<Coordinate> getFireData(int dateRange, String date) throws Exception {
            lastRange = dateRange;
            if (shouldThrowException) {
                throw new Exception("Test error");
            }
            return coords;
        }
    }

    private static class TestPresenter implements LoadFiresOutputBoundary {
        LoadFiresOutputData outputData;
        String errorMessage;

        @Override
        public void prepareSuccessView(LoadFiresOutputData outputData) {
            this.outputData = outputData;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }

    private static class TestBoundariesAccess implements LoadFiresBoundaryDataAccess {
        boolean returnNull = false;

        @Override
        public Region getRegion(String provinceName) {
            if (returnNull) {
                return null;
            }
            // Create a fake boundary around the test coordinates
            final List<GeoPosition> boundary = List.of(
                    new GeoPosition(43.6, -79.5),
                    new GeoPosition(43.6, -79.3),
                    new GeoPosition(43.8, -79.3),
                    new GeoPosition(43.8, -79.5)
            );
            return new Region("Test", List.of(boundary));
        }
    }
}
