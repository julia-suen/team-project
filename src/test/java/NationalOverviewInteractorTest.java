import entities.Coordinate;
import entities.Fire;
import entities.Region;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jxmapviewer.viewer.GeoPosition;
import org.mockito.ArgumentCaptor;
import usecase.common.FireService;
import usecase.national_overview.NationalOverviewBoundaryDataAccess;
import usecase.national_overview.NationalOverviewFireDataAccess;
import usecase.national_overview.NationalOverviewInputData;
import usecase.national_overview.NationalOverviewInteractor;
import usecase.national_overview.NationalOverviewOutputBoundary;
import usecase.national_overview.NationalOverviewOutputData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the NationalOverviewInteractor.
 * This class tests the orchestration logic for the National Overview feature,
 * ensuring that data is correctly fetched from the API, filtered by the Canada
 * boundary using the FireService, and aggregated into a trend report.
 */
class NationalOverviewInteractorTest {

    private NationalOverviewInteractor interactor;
    private NationalOverviewFireDataAccess fireAccess;
    private NationalOverviewBoundaryDataAccess boundaryAccess;
    private NationalOverviewOutputBoundary presenter;

    private final FireService fireService = new FireService();

    private static final String WORLD_BOUNDS = "-180,-90,180,90";

    /**
     * Sets up the test environment before each test.
     * Mocks the data access and presenter dependencies and initializes the Interactor.
     */
    @BeforeEach
    void setUp() {
        fireAccess = mock(NationalOverviewFireDataAccess.class);
        boundaryAccess = mock(NationalOverviewBoundaryDataAccess.class);
        presenter = mock(NationalOverviewOutputBoundary.class);

        interactor = new NationalOverviewInteractor(fireAccess, boundaryAccess, presenter, fireService);
    }

    /**
     * Tests the success scenario where all dependencies return valid data.
     * Scenario:
     * 1. The Canada boundary is successfully retrieved.
     * 2. The Fire API returns fire data for the requested months.
     * 3. Some fires are inside Canada, others are outside.
     * Expected Outcome:
     * - The interactor calls the presenter with success.
     * - Fires outside Canada are filtered out.
     * - Fires inside Canada are aggregated into the result list.
     * - Trend data correctly reflects the count of fires per month.
     * * @throws Exception if any data access operations fail.
     */
    @Test
    void execute_Success_ShouldAggregateAndFilter() throws Exception {
        // Arrange
        String dateStr = "2025-10-01"; // Oct, Sep, Aug
        int range = 1;
        NationalOverviewInputData inputData = new NationalOverviewInputData(dateStr, range);

        // Setup Canada Boundary
        List<GeoPosition> polygon = List.of(
                new GeoPosition(0, 0),
                new GeoPosition(0, 10),
                new GeoPosition(10, 10),
                new GeoPosition(10, 0)
        );
        Region canadaRegion = new Region("Canada", List.of(polygon));
        when(boundaryAccess.getRegion("Canada")).thenReturn(canadaRegion);

        // Setup Coordinates
        Coordinate pointA = new Coordinate(5, 5, new String[]{"2025-10-01", "D", "h"}, new double[]{300, 300}, 10);
        Coordinate pointB = new Coordinate(20, 20, new String[]{"2025-10-01", "D", "h"}, new double[]{300, 300}, 10);

        // Mock API Responses using mutable lists
        // Month 1 (Oct): Returns both points.
        when(fireAccess.getFireData(eq(range), eq("2025-10-01"), eq(WORLD_BOUNDS)))
                .thenReturn(new ArrayList<>(List.of(pointA, pointB)));

        // Month 2 (Sep): Returns only point A.
        when(fireAccess.getFireData(eq(range), eq("2025-09-01"), eq(WORLD_BOUNDS)))
                .thenReturn(new ArrayList<>(List.of(pointA)));

        // Month 3 (Aug): Returns empty.
        when(fireAccess.getFireData(eq(range), eq("2025-08-01"), eq(WORLD_BOUNDS)))
                .thenReturn(new ArrayList<>());

        // Act
        interactor.execute(inputData);

        // Assert
        ArgumentCaptor<NationalOverviewOutputData> captor = ArgumentCaptor.forClass(NationalOverviewOutputData.class);
        verify(presenter).prepareSuccessView(captor.capture());
        NationalOverviewOutputData output = captor.getValue();

        List<Fire> resultFires = output.getFires();
        Map<String, Integer> trendData = output.getFireTrendData();

        // Check Trend Data
        assertNotNull(trendData);
        assertEquals(3, trendData.size());
        assertEquals(1, trendData.get("Oct"));
        assertEquals(1, trendData.get("Sep"));
        assertEquals(0, trendData.get("Aug"));

        // Check Total Fires
        assertEquals(2, resultFires.size());
        assertEquals(5.0, resultFires.get(0).getCenter().getLat());
    }

    /**
     * Tests the scenario where the boundary data for Canada cannot be retrieved.
     * Scenario:
     * 1. {@code boundaryAccess.getRegion("Canada")} returns null.
     * 2. The Fire API returns valid fire data.
     * Expected Outcome:
     * - The system gracefully handles the missing boundary by treating the region as empty.
     * - No fires are returned (since we cannot verify if they are in Canada).
     * - Trend counts are zero.
     * * @throws Exception if any data access operations fail.
     */
    @Test
    void execute_MissingBoundary_ShouldReturnEmpty() throws Exception {
        // Arrange
        NationalOverviewInputData inputData = new NationalOverviewInputData("2025-10-01", 1);

        when(boundaryAccess.getRegion("Canada")).thenReturn(null);

        // Use mutable list here as well
        when(fireAccess.getFireData(anyInt(), anyString(), anyString()))
                .thenReturn(new ArrayList<>(List.of(new Coordinate(5, 5, new String[]{"D"}, new double[]{300, 300}, 10))));

        // Act
        interactor.execute(inputData);

        // Assert
        ArgumentCaptor<NationalOverviewOutputData> captor = ArgumentCaptor.forClass(NationalOverviewOutputData.class);
        verify(presenter).prepareSuccessView(captor.capture());

        NationalOverviewOutputData output = captor.getValue();
        assertTrue(output.getFires().isEmpty());
        assertEquals(0, output.getFireTrendData().get("Oct"));
    }

    /**
     * Tests the scenario where the Fire API returns null instead of a list.
     * Scenario:
     * 1. The Fire API returns {@code null} for a specific request.
     * Expected Outcome:
     * - The interactor treats null as an empty list (0 fires).
     * - No exception is thrown.
     * - Output contains empty fire list and zero trends.
     * * @throws Exception if any data access operations fail.
     */
    @Test
    void execute_ApiReturnsNull_ShouldHandleGracefully() throws Exception {
        NationalOverviewInputData inputData = new NationalOverviewInputData("2025-10-01", 1);
        when(boundaryAccess.getRegion("Canada")).thenReturn(new Region("Canada", new ArrayList<>()));
        when(fireAccess.getFireData(anyInt(), anyString(), anyString())).thenReturn(null);

        interactor.execute(inputData);

        ArgumentCaptor<NationalOverviewOutputData> captor = ArgumentCaptor.forClass(NationalOverviewOutputData.class);
        verify(presenter).prepareSuccessView(captor.capture());
        assertTrue(captor.getValue().getFires().isEmpty());
    }

    /**
     * Tests the failure scenario where the Fire API throws an exception.
     * Scenario:
     * 1. The Fire API throws a RuntimeException (e.g., connection timeout).
     * Expected Outcome:
     * - The interactor catches the exception.
     * - The presenter's {@code prepareFailView} method is called with a relevant error message.
     * * @throws Exception if any data access operations fail.
     */
    @Test
    void execute_ApiThrowsException_ShouldCallFailView() throws Exception {
        NationalOverviewInputData inputData = new NationalOverviewInputData("2025-10-01", 1);
        when(fireAccess.getFireData(anyInt(), anyString(), anyString()))
                .thenThrow(new RuntimeException("API Down"));

        interactor.execute(inputData);

        verify(presenter).prepareFailView(contains("Error processing national overview"));
    }
}
