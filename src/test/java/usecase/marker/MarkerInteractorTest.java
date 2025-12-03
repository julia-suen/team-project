package usecase.marker;

import entities.Coordinate;
import entities.Fire;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the MarkerInteractor to achieve 100% code coverage.
 * Uses helper classes (TestPresenter and TestFireDisplayStateReader) for explicit mocking
 * and adherence to project testing standards.
 */
class MarkerInteractorTest {

    // Mock Coordinate Entity (to correctly construct the MockFire)
    private static class MockCoordinate extends Coordinate {
        public MockCoordinate(double lat, double lon, double frp) {
            // Call the full Coordinate constructor with dummy values for unused fields
            super(lat, lon, new String[]{"n/a", "n/a", "n/a"}, new double[]{0.0, 0.0}, frp);
        }
    }

    // Mock Fire Entity
    private static class MockFire extends Fire {
        private final double lat;
        private final double lon;
        private final int size;
        private final double frp;

        public MockFire(double lat, double lon, int size, double frp) {
            super(1.0, new MockCoordinate(lat, lon, frp), Collections.emptyList());
            this.lat = lat;
            this.lon = lon;
            this.size = size;
            this.frp = frp;
        }

        @Override public double getLat() { return lat; }
        @Override public double getLon() { return lon; }
        @Override public int getCoordinatesSize() { return size; }
        @Override public double getFrp() { return frp; }
    }

    // Mock MarkerInputData
    private static class MockMarkerInputData extends MarkerInputData {
        private final double lat;
        private final double lon;

        public MockMarkerInputData(double lat, double lon) {
            super(lat, lon);
            this.lat = lat;
            this.lon = lon;
        }
        @Override public double getLat() { return lat; }
        @Override public double getLon() { return lon; }
    }

    private static class TestPresenter implements MarkerOutputBoundary {
        MarkerOutputData outputData;
        String errorMessage;

        @Override
        public void prepareSuccessView(MarkerOutputData outputData) {
            this.outputData = outputData;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }

    private static class TestFireDisplayStateReader implements FireDisplayStateReader {
        private final List<Fire> fires;
        private boolean shouldThrowException = false;

        public TestFireDisplayStateReader(List<Fire> fires) {
            this.fires = fires;
        }

        public void setShouldThrowException(boolean shouldThrowException) {
            this.shouldThrowException = shouldThrowException;
        }

        @Override
        public List<Fire> getDisplayedFires() {
            String exceptionMessage = "Simulated Database Error";
            if (shouldThrowException) {
                throw new RuntimeException(exceptionMessage);
            }
            return fires;
        }
    }

    private MarkerInteractor interactor;
    private TestPresenter presenter;
    private TestFireDisplayStateReader fireDisplayStateReader;

    @BeforeEach
    void setUp() {
        List<Fire> commonFires;
        commonFires = new ArrayList<>();
        // Fire 1 (Target fire)
        commonFires.add(new MockFire(34.00000000001, -118.50000000001, 5, 10.5));
        // Fire 2 (Distractor fire)
        commonFires.add(new MockFire(35.0, -119.0, 1, 1.1));

        presenter = new TestPresenter();
        fireDisplayStateReader = new TestFireDisplayStateReader(commonFires);

        // Initialize Interactor
        interactor = new MarkerInteractor(presenter, fireDisplayStateReader);
    }


    /**
     * Coverage: execute
     * Tests exact coordinate match and successful presentation where foundFire != null.
     */
    @Test
    void testExecute_Success_MatchFound() {
        double targetLat = 34.00000000000;
        double targetLon = -118.50000000000;
        MarkerInputData inputData = new MockMarkerInputData(targetLat, targetLon);

        interactor.execute(inputData);

        // Assert to verify success view was called and data is correct
        assertNotNull(presenter.outputData);
        assertNull(presenter.errorMessage);

        MarkerOutputData actualOutput = presenter.outputData;
        assertEquals(34.00000000001, actualOutput.getLat(), 1e-11);
        assertEquals(-118.50000000001, actualOutput.getLon(), 1e-11);
        assertEquals(5, actualOutput.getSize());
        assertEquals(10.5, actualOutput.getFrp(), 0.001);
    }

    /**
     * Coverage: execute
     * Tests when coordinates do not match any displayed fire where foundFire == null.
     */
    @Test
    void testExecute_FireNotFound_NoMatch() {
        // Coordinates far away from all displayed fires
        // Should not happen because fireAtPoint finds the exact marker
        MarkerInputData inputData = new MockMarkerInputData(0.0, 0.0);

        interactor.execute(inputData);

        // Assert to verify prepareFailView was called
        assertNull(presenter.outputData);
        assertEquals("No fires are found", presenter.errorMessage);
    }

    /**
     * Coverage: findFireAtCoord (Loop completion/returns null).
     * Tests when the displayed list is empty.
     */
    @Test
    void testExecute_FireNotFound_EmptyList() {
        // Reset the data reader to return an empty list and re-initialize interactor
        fireDisplayStateReader = new TestFireDisplayStateReader(Collections.emptyList());
        interactor = new MarkerInteractor(presenter, fireDisplayStateReader);

        MarkerInputData inputData = new MockMarkerInputData(34.0, -118.5);

        interactor.execute(inputData);

        // Assert that foundFire is null and leads to failure view
        assertNull(presenter.outputData);
        assertEquals("No fires are found", presenter.errorMessage);
    }

    /**
     * Coverage: execute (Catch block execution).
     * Tests when an unexpected exception occurs during execution.
     */
    @Test
    void testExecute_Exception_UnexpectedError() {
        // Force to throw a runtime exception
        fireDisplayStateReader.setShouldThrowException(true);
        MarkerInputData inputData = new MockMarkerInputData(34.0, -118.5);

        interactor.execute(inputData);

        // Assert to verify the catch block was executed and called prepareFailView with the exception message
        assertNull(presenter.outputData);
        assertTrue(presenter.errorMessage.contains("Unexpected error: Simulated Database Error"));
    }

    /**
     * Coverage: findFireAtCoord (Missing Branch: Lat passes, Lon fails).
     * This test ensures the second part of the AND condition is evaluated and fails.
     */
    @Test
    void testExecute_FireNotFound_LatPassesLonFails() {
        // Target Lat matches Fire 2's Lat (35.0) within tolerance (passes first check).
        double targetLat = 35.00000000000;
        // Target Lon is far from Fire 2's Lon (-119.0) (fails second check).
        double targetLon = 1.0;

        MarkerInputData inputData = new MockMarkerInputData(targetLat, targetLon);

        interactor.execute(inputData);

        // Assert that it fails the coordinate match and leads to failure view.
        assertNull(presenter.outputData);
        assertEquals("No fires are found", presenter.errorMessage);
    }
}