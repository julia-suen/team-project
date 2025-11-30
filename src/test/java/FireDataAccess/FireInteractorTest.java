package use_case.fire_data;

import data_access.BoundariesDataAccess;
import data_access.GetData;
import entities.Coordinate;
import entities.Region;
import org.junit.jupiter.api.Test;
import org.jxmapviewer.viewer.GeoPosition;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FireInteractorTest {

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

        final TestDataAccess dataAccess = new TestDataAccess(coords);
        final TestPresenter presenter = new TestPresenter();
        final TestBoundariesAccess boundariesAccess = new TestBoundariesAccess();

        final FireInteractor interactor = new FireInteractor(dataAccess, presenter, boundariesAccess);
        final FireInputData inputData = new FireInputData("All", "2025-11-20", 1, false);

        interactor.execute(inputData);

        assertNotNull(presenter.outputData);
        assertEquals(1, presenter.outputData.getFires().size());
    }

    /**
     * Tests if determined national boundary + applying national filter will correctly return fires located
     * within that boundary.
     */
    @Test
    void correctlyBundlesNationalOverview() {
        final List<Coordinate> coords = new ArrayList<>();
        coords.add(new Coordinate(43.7, -79.4,
                new String[]{"2025-11-20", "D", "h"}, new double[]{300.0, 280.0}, 5.0));

        final TestDataAccess dataAccess = new TestDataAccess(coords);
        final TestPresenter presenter = new TestPresenter();
        final TestBoundariesAccess boundariesAccess = new TestBoundariesAccess();

        final FireInteractor interactor = new FireInteractor(dataAccess, presenter, boundariesAccess);
        final FireInputData inputData = new FireInputData("Ontario", "2025-11-20",
                1, true);

        interactor.execute(inputData);

        assertNotNull(presenter.outputData);
        assertEquals(3, presenter.outputData.getFireTrendData().size());
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

        final TestDataAccess dataAccess = new TestDataAccess(coords);
        final TestPresenter presenter = new TestPresenter();
        final TestBoundariesAccess boundariesAccess = new TestBoundariesAccess();

        final FireInteractor interactor = new FireInteractor(dataAccess, presenter, boundariesAccess);
        final FireInputData inputData = new FireInputData("Ontario", "2025-11-20",
                1, false);

        interactor.execute(inputData);
        assertEquals(1, presenter.outputData.getFires().size());
    }

    /**
     * Tests that making a call to the FIRMS api with an empty string "" date in the future still returns fires
     * for the current day, (i.e.: results in non-empty outputData), as intended.
     */
    @Test
    void testEmptyDate(){
        final TestDataAccess dataAccess = new TestDataAccess(new ArrayList<>());
        final TestPresenter presenter = new TestPresenter();
        final TestBoundariesAccess boundariesAccess = new TestBoundariesAccess();

        final FireInteractor interactor = new FireInteractor(dataAccess, presenter, boundariesAccess);
        final FireInputData inputData = new FireInputData("All", "",
                1, false);

        interactor.execute(inputData);

        assertNotNull(presenter.outputData);
    }

    /**
     * Tests that making a call to the FIRMS api with a null date in the future still returns fires for the current
     * day, (i.e.: results in non-empty outputData), as intended.
     */
    @Test
    void testNullDate() {
        final TestDataAccess dataAccess = new TestDataAccess(new ArrayList<>());
        final TestPresenter presenter = new TestPresenter();
        final TestBoundariesAccess boundariesAccess = new TestBoundariesAccess();

        final FireInteractor interactor = new FireInteractor(dataAccess, presenter, boundariesAccess);
        final FireInputData inputData = new FireInputData("All", null,
                1, false);

        interactor.execute(inputData);

        assertNotNull(presenter.outputData);
    }

    /**
     * Tests that making a call to the FIRMS api for a date in the future returns no fires.
     */
    @Test
    void testFutureDate() {
        final TestDataAccess dataAccess = new TestDataAccess(new ArrayList<>());
        final TestPresenter presenter = new TestPresenter();
        final TestBoundariesAccess boundariesAccess = new TestBoundariesAccess();

        final FireInteractor interactor = new FireInteractor(dataAccess, presenter, boundariesAccess);
        final FireInputData inputData = new FireInputData("All", "2030-11-22",
                1, false);

        interactor.execute(inputData);

        assertEquals(0, presenter.outputData.getFires().size());
    }

    /**
     * Tests that making a call to the FIRMS api for a date range greater than allowed (10 day period)
     * runs without error and returns fires for a 10 day period instead.
     */
    @Test
    void testInvalidHighDayRange() {
        final TestDataAccess dataAccess = new TestDataAccess(new ArrayList<>());
        final TestPresenter presenter = new TestPresenter();
        final TestBoundariesAccess boundariesAccess = new TestBoundariesAccess();

        final FireInteractor interactor = new FireInteractor(dataAccess, presenter, boundariesAccess);
        final FireInputData inputData = new FireInputData("All", "2025-11-20",
                15, false);

        interactor.execute(inputData);

        assertEquals(10, dataAccess.lastRange);
    }

    /**
     * Tests that making a call to the FIRMS api for a date range smaller than allowed (less than 1 day)
     * runs without error and returns fires for a 1 day period (the selected date) instead.
     */
    @Test
    void testInvalidLowDayRange() {
        final TestDataAccess dataAccess = new TestDataAccess(new ArrayList<>());
        final TestPresenter presenter = new TestPresenter();
        final TestBoundariesAccess boundariesAccess = new TestBoundariesAccess();

        final FireInteractor interactor = new FireInteractor(dataAccess, presenter, boundariesAccess);
        final FireInputData inputData = new FireInputData("All", "2025-11-20",
                0, false);

        interactor.execute(inputData);

        assertEquals(1, dataAccess.lastRange);
    }

    /**
     * Tests that processing invalid data will raise an exception as intended.
     */
    @Test
    void testInvalidDataException() {
        final TestDataAccess dataAccess = new TestDataAccess(null);
        dataAccess.shouldThrowException = true;
        final TestPresenter presenter = new TestPresenter();
        final TestBoundariesAccess boundariesAccess = new TestBoundariesAccess();

        final FireInteractor interactor = new FireInteractor(dataAccess, presenter, boundariesAccess);
        final FireInputData inputData = new FireInputData("All", "2025-11-20",
                1, false);

        interactor.execute(inputData);

        assertNotNull(presenter.errorMessage);
        assertTrue(presenter.errorMessage.contains("Error fetching data"));
    }

    /**
     * Tests that submitting null coordinates results in 0 fires generated.
     */
    @Test
    void testNullCoords() {
        final TestDataAccess dataAccess = new TestDataAccess(null);
        final TestPresenter presenter = new TestPresenter();
        final TestBoundariesAccess boundariesAccess = new TestBoundariesAccess();

        final FireInteractor interactor = new FireInteractor(dataAccess, presenter, boundariesAccess);
        final FireInputData inputData = new FireInputData("All", "2025-11-20",
                1, false);

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

        final TestDataAccess dataAccess = new TestDataAccess(coords);
        final TestPresenter presenter = new TestPresenter();
        final TestBoundariesAccess boundariesAccess = new TestBoundariesAccess();
        boundariesAccess.returnNull = true;

        final FireInteractor interactor = new FireInteractor(dataAccess, presenter, boundariesAccess);
        final FireInputData inputData = new FireInputData("Ontario", "2025-11-20",
                1, false);

        interactor.execute(inputData);

        assertNotNull(presenter.outputData);
        assertTrue(presenter.outputData.getFires().isEmpty());
    }

    /**
     * Below are helpers that mock data access objects and presenters required for tests, instead of making calls to
     * the actual APIs.
     */
    private static class TestDataAccess implements GetData {
        private final List<Coordinate> coords;
        int lastRange = -1;
        boolean shouldThrowException = false;

        TestDataAccess(List<Coordinate> coords) {
            this.coords = coords;
        }

        @Override
        public List<Coordinate> getFireData(int dateRange, String date) throws InvalidDataException {
            lastRange = dateRange;
            if (shouldThrowException) {
                throw new InvalidDataException("Test error");
            }
            return coords;
        }

        @Override
        public List<Coordinate> getFireData(int dateRange, String date, String boundingBox)
                throws InvalidDataException {
            return getFireData(dateRange, date);
        }
    }

    private static class TestPresenter implements FireOutputBoundary {
        FireOutputData outputData;
        String errorMessage;

        @Override
        public void prepareSuccessView(FireOutputData outputData) {
            this.outputData = outputData;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }

    private static class TestBoundariesAccess extends BoundariesDataAccess {
        boolean returnNull = false;

        @Override
        public Region getRegion(String provinceName) {
            if (returnNull) {
                return null;
            }
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