package usecase.severity_filter;

import entities.Coordinate;
import entities.Fire;
import entities.SeverityFilter;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SeverityInteractorTest {

    /**
     * Tests if the reset filter correctly restores displayed fires to all loaded fires.
     */
    @Test
    void correctResetFilter() {
        final List<Fire> fires = createTestFires();
        final TestPresenter presenter = new TestPresenter();
        final SeverityInteractor interactor = new SeverityInteractor(presenter);
        final SeverityInputData inputData = new SeverityInputData(fires, SeverityFilter.RESET);

        interactor.execute(inputData);

        assertNotNull(presenter.outputData);
        assertNotNull(presenter.outputData.filteredFires());
        assertFalse(presenter.outputData.filteredFires().isEmpty());
    }

    /**
     * Tests if the medium filter correctly filters displayed fires to only fires with FRP greater than 5 (medium
     * severity threshold).
     */
    @Test
    void testMediumFilter() {
        final List<Fire> fires = createTestFires();
        final TestPresenter presenter = new TestPresenter();
        final SeverityInteractor interactor = new SeverityInteractor(presenter);
        final SeverityInputData inputData = new SeverityInputData(fires, SeverityFilter.MEDIUM);

        interactor.execute(inputData);

        assertNotNull(presenter.outputData);
        assertNotNull(presenter.outputData.filteredFires());
        assertEquals(2, presenter.outputData.filteredFires().size());
    }

    /**
     * Tests if the high filter correctly filters displayed fires to only fires with FRP greater than 7 (high
     * severity threshold).
     */
    @Test
    void testHighFilter() {
        final List<Fire> fires = createTestFires();
        final TestPresenter presenter = new TestPresenter();
        final SeverityInteractor interactor = new SeverityInteractor(presenter);
        final SeverityInputData inputData = new SeverityInputData(fires, SeverityFilter.HIGH);

        interactor.execute(inputData);

        assertNotNull(presenter.outputData);
        assertNotNull(presenter.outputData.filteredFires());
        assertEquals(1, presenter.outputData.filteredFires().size());
    }

    /**
     * Tests if filtering null fires returns no fires.
     */
    @Test
    void testNullFires() {
        final TestPresenter presenter = new TestPresenter();
        final SeverityInteractor interactor = new SeverityInteractor(presenter);
        final SeverityInputData inputData = new SeverityInputData(null, SeverityFilter.RESET);

        interactor.execute(inputData);

        assertNotNull(presenter.outputData);
        assertTrue(presenter.outputData.filteredFires().isEmpty());
    }

    /**
     * Tests if filtering an empty fire list returns no fires.
     */
    @Test
    void testEmptyFire() {
        final List<Fire> fires = new ArrayList<>();
        final TestPresenter presenter = new TestPresenter();
        final SeverityInteractor interactor = new SeverityInteractor(presenter);
        final SeverityInputData inputData = new SeverityInputData(fires, SeverityFilter.MEDIUM);

        interactor.execute(inputData);

        assertNotNull(presenter.outputData);
        assertTrue(presenter.outputData.filteredFires().isEmpty());
    }

    /**
     * Tests if filtering a low severity fire with a medium filter returns an empty list of fires, and is also not null.
     */
    @Test
    void testLowSeverityFire() {
        final List<Fire> fires = createLowSeverityFire();
        final TestPresenter presenter = new TestPresenter();
        final SeverityInteractor interactor = new SeverityInteractor(presenter);
        final SeverityInputData inputData = new SeverityInputData(fires, SeverityFilter.MEDIUM);

        interactor.execute(inputData);

        assertNotNull(presenter.outputData);
        assertTrue(presenter.outputData.filteredFires().isEmpty());
    }

    /**
     * Tests if filtering a medium severity fire with a high filter returns an empty list of fires, and is
     * also not null.
     */
    @Test
    void testMediumSeverityFire() {
        final List<Fire> fires = createMediumSeverityFire();
        final TestPresenter presenter = new TestPresenter();
        final SeverityInteractor interactor = new SeverityInteractor(presenter);
        final SeverityInputData inputData = new SeverityInputData(fires, SeverityFilter.HIGH);

        interactor.execute(inputData);

        assertNotNull(presenter.outputData);
        assertTrue(presenter.outputData.filteredFires().isEmpty());
    }

    /**
     * Tests if filtering a high severity fire with a high filter does not return an empty list of fires, and is
     * also not null.
     */
    @Test
    void testHighSeverityFire() {
        final List<Fire> fires = createHighSeverityFire();
        final TestPresenter presenter = new TestPresenter();
        final SeverityInteractor interactor = new SeverityInteractor(presenter);
        final SeverityInputData inputData = new SeverityInputData(fires, SeverityFilter.HIGH);


        interactor.execute(inputData);

        assertNotNull(presenter.outputData);
        assertFalse(presenter.outputData.filteredFires().isEmpty());
        assertEquals(1, presenter.outputData.filteredFires().size());
    }

    /**
     * Creates a mock list of 3 fires, each of differing severity.
     * @return a list containing 3 different severity fires.
     */
    private List<Fire> createTestFires() {
        final List<Fire> fires = new ArrayList<>();
        final List<Coordinate> coordsLow = new ArrayList<>();
        final List<Coordinate> coordsMed = new ArrayList<>();
        final List<Coordinate> coordsHigh = new ArrayList<>();

        coordsLow.add(new Coordinate(43.7, -79.4, new String[]{"2025-11-25", "D", "h"},
                        new double[]{300.0, 280.0}, 1.0));
        fires.add(new Fire(0.01, coordsLow.get(0), coordsLow));

        coordsMed.add(new Coordinate(43.71, -79.41, new String[]{"2025-11-25", "D", "h"},
                        new double[]{310.0, 290.0}, 5.0));
        fires.add(new Fire(0.01, coordsMed.get(0), coordsMed));

        coordsHigh.add(new Coordinate(43.72, -79.42, new String[]{"2025-11-25", "D", "h"},
                        new double[]{320.0, 300.0}, 9.0));
        fires.add(new Fire(0.01, coordsHigh.get(0), coordsHigh));

        return fires;
    }

    /**
     * Creates a mock list of a single, low severity fire (FRP < 3).
     * @return a list containing a single low severity fire.
     */
    private List<Fire> createLowSeverityFire() {
        final List<Fire> fires = new ArrayList<>();
        final List<Coordinate> coords = new ArrayList<>();
        coords.add(new Coordinate(43.7, -79.4, new String[]{"2025-11-25", "D", "h"},
                        new double[]{300.0, 280.0}, 1.0));

        fires.add(new Fire(0.01, coords.get(0), coords));
        return fires;
    }

    /**
     * Creates a mock list of a single, medium severity fire (3 < FRP < 7).
     * @return a list containing a single medium severity fire.
     */
    private List<Fire> createMediumSeverityFire() {
        final List<Fire> fires = new ArrayList<>();
        final List<Coordinate> coords = new ArrayList<>();
        coords.add(new Coordinate(43.7, -79.4, new String[]{"2025-11-25", "D", "h"},
                new double[]{300.0, 280.0}, 5.0));

        fires.add(new Fire(0.01, coords.get(0), coords));
        return fires;
    }

    /**
     * Creates a mock list of a single, high severity fire (FRP > 7).
     * @return a list containing a single high severity fire.
     */
    private List<Fire> createHighSeverityFire() {
        final List<Fire> fires = new ArrayList<>();
        final List<Coordinate> coords = new ArrayList<>();
        coords.add(new Coordinate(43.7, -79.4, new String[]{"2025-11-25", "D", "h"},
                new double[]{300.0, 280.0}, 9.0));

        fires.add(new Fire(0.01, coords.get(0), coords));
        return fires;
    }

    /**
     * Helper to mock a presenter required for tests.
     */
    private static class TestPresenter implements SeverityOutputBoundary {
        boolean successCalled = false;
        boolean failCalled = false;
        SeverityOutputData outputData;
        String errorMessage;

        @Override
        public void prepareSuccessView(SeverityOutputData outputData) {
            this.successCalled = true;
            this.outputData = outputData;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            this.failCalled = true;
            this.errorMessage = errorMessage;
        }
    }
}