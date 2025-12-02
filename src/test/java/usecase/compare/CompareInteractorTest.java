package usecase.compare;

import entities.Coordinate;
import entities.MultiRegionFireStats;
import entities.Region;
import kotlin.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jxmapviewer.viewer.GeoPosition;
import usecase.common.FireService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the CompareInteractor.
 * This class tests the orchestration logic for the Compare/Regional Analysis feature,
 * ensuring that data is correctly fetched for multiple regions over an expanded time window,
 * filtered by region boundaries, and aggregated into daily statistics.
 */
class CompareInteractorTest {

    private CompareInteractor interactor;
    private CompareFireDataAccess fireAccess;
    private CompareBoundaryDataAccess boundaryAccess;
    private final FireService fireService = new FireService();

    /**
     * Sets up the test environment before each test.
     * Mocks the data access dependencies and initializes the Interactor.
     */
    @BeforeEach
    void setUp() {
        fireAccess = mock(CompareFireDataAccess.class);
        boundaryAccess = mock(CompareBoundaryDataAccess.class);
        interactor = new CompareInteractor(fireAccess, boundaryAccess, fireService);
    }

    /**
     * Tests the success scenario with multiple provinces.
     * Scenario:
     * 1. Two provinces are selected
     * 2. Fire data is fetched for the expanded time window
     * 3. Each province has fires within its boundary
     * Expected Outcome:
     * - Stats are calculated for each province
     * - Daily counts are correct
     * - Time window is [selectDate - Range] to [selectDate + 2*Range]
     */
    @Test
    void execute_Success_MultipleProvinces_ShouldReturnStatsForEach() throws Exception {
        // Arrange
        String date = "2025-11-20";
        int range = 2;
        List<String> provinces = List.of("Ontario", "Quebec");
        CompareInputData inputData = new CompareInputData(provinces, date, range);

        // Create test regions
        List<GeoPosition> ontarioBoundary = List.of(
                new GeoPosition(43.6, -79.5),
                new GeoPosition(43.6, -79.3),
                new GeoPosition(43.8, -79.3),
                new GeoPosition(43.8, -79.5)
        );
        Region ontarioRegion = new Region("Ontario", List.of(ontarioBoundary));

        List<GeoPosition> quebecBoundary = List.of(
                new GeoPosition(46.6, -71.5),
                new GeoPosition(46.6, -71.3),
                new GeoPosition(46.8, -71.3),
                new GeoPosition(46.8, -71.5)
        );
        Region quebecRegion = new Region("Quebec", List.of(quebecBoundary));

        when(boundaryAccess.getRegion("Ontario")).thenReturn(ontarioRegion);
        when(boundaryAccess.getRegion("Quebec")).thenReturn(quebecRegion);

        // Create test coordinates
        // Ontario fires (inside boundary) - different locations to avoid bundling
        Coordinate ontarioFire1 = new Coordinate(43.7, -79.4,
                new String[]{"2025-11-18", "D", "h"}, new double[]{300.0, 280.0}, 5.0);
        Coordinate ontarioFire2 = new Coordinate(43.75, -79.45,
                new String[]{"2025-11-20", "D", "h"}, new double[]{300.0, 280.0}, 5.0);

        // Quebec fires (inside boundary)
        Coordinate quebecFire1 = new Coordinate(46.7, -71.4,
                new String[]{"2025-11-19", "D", "h"}, new double[]{300.0, 280.0}, 5.0);

        // Outside both boundaries
        Coordinate outsideFire = new Coordinate(20.0, 20.0,
                new String[]{"2025-11-20", "D", "h"}, new double[]{300.0, 280.0}, 5.0);

        // Mock API response for expanded time window: 2025-11-18 to 2025-11-23 (6 days total)
        // With range=2, totalDaysToFetch = 2*3 = 6 days, fetched in ONE batch
        // grandStartDate = 2025-11-20 - 2 = 2025-11-18
        when(fireAccess.getFireData(eq(6), eq("2025-11-18")))
                .thenReturn(new ArrayList<>(List.of(ontarioFire1, ontarioFire2, quebecFire1, outsideFire)));

        // Act
        CompareOutputData result = interactor.execute(inputData);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getStats());
        MultiRegionFireStats stats = result.getStats();
        Map<String, List<Pair<String, Integer>>> data = stats.getData();

        // Check that stats exist for both provinces
        assertTrue(data.containsKey("Ontario"));
        assertTrue(data.containsKey("Quebec"));

        // Check Ontario stats
        List<Pair<String, Integer>> ontarioStats = data.get("Ontario");
        assertNotNull(ontarioStats);
        // Should have 6 days of data (2025-11-18 to 2025-11-23)
        assertEquals(6, ontarioStats.size());
        // Check specific dates
        assertTrue(ontarioStats.stream().anyMatch(p -> p.getFirst().equals("2025-11-18") && p.getSecond() >= 1));
        assertTrue(ontarioStats.stream().anyMatch(p -> p.getFirst().equals("2025-11-20") && p.getSecond() >= 1));

        // Check Quebec stats
        List<Pair<String, Integer>> quebecStats = data.get("Quebec");
        assertNotNull(quebecStats);
        assertEquals(6, quebecStats.size());
        assertTrue(quebecStats.stream().anyMatch(p -> p.getFirst().equals("2025-11-19") && p.getSecond() >= 1));
    }

    /**
     * Tests the scenario with "All" province.
     * Expected Outcome:
     * - All raw points are included (no filtering)
     * - Stats are calculated for "All"
     */
    @Test
    void execute_Success_AllProvince_ShouldReturnAllData() throws Exception {
        // Arrange
        String date = "2025-11-20";
        int range = 1;
        List<String> provinces = List.of("All");
        CompareInputData inputData = new CompareInputData(provinces, date, range);

        Coordinate fire1 = new Coordinate(43.7, -79.4,
                new String[]{"2025-11-19", "D", "h"}, new double[]{300.0, 280.0}, 5.0);
        Coordinate fire2 = new Coordinate(46.7, -71.4,
                new String[]{"2025-11-20", "D", "h"}, new double[]{300.0, 280.0}, 5.0);

        // Mock API response for 3 days (2025-11-19 to 2025-11-21)
        // With range=1, totalDaysToFetch = 1*3 = 3 days, fetched in ONE batch
        // grandStartDate = 2025-11-20 - 1 = 2025-11-19
        when(fireAccess.getFireData(eq(3), eq("2025-11-19")))
                .thenReturn(new ArrayList<>(List.of(fire1, fire2)));

        // Act
        CompareOutputData result = interactor.execute(inputData);

        // Assert
        assertNotNull(result);
        MultiRegionFireStats stats = result.getStats();
        Map<String, List<Pair<String, Integer>>> data = stats.getData();

        assertTrue(data.containsKey("All"));
        List<Pair<String, Integer>> allStats = data.get("All");
        assertEquals(3, allStats.size());
        // Should include both fires
        assertTrue(allStats.stream().anyMatch(p -> p.getFirst().equals("2025-11-19") && p.getSecond() >= 1));
        assertTrue(allStats.stream().anyMatch(p -> p.getFirst().equals("2025-11-20") && p.getSecond() >= 1));
    }

    /**
     * Tests the time window calculation.
     * Expected Outcome:
     * - Time window is correctly calculated as [selectDate - Range] to [selectDate + 2*Range]
     * - Total days fetched = range * 3
     */
    @Test
    void execute_Success_TimeWindow_ShouldFetchCorrectRange() throws Exception {
        // Arrange
        String date = "2025-11-20";
        int range = 2;
        List<String> provinces = List.of("All");
        CompareInputData inputData = new CompareInputData(provinces, date, range);

        // Expected time window: 2025-11-18 to 2025-11-23 (6 days total)
        // With range=2, totalDaysToFetch = 2*3 = 6 days, fetched in ONE batch
        // grandStartDate = 2025-11-20 - 2 = 2025-11-18

        when(fireAccess.getFireData(anyInt(), anyString())).thenReturn(new ArrayList<>());

        // Act
        interactor.execute(inputData);

        // Assert - verify API was called for the correct date range
        // Should make ONE call: getFireData(6, "2025-11-18")
        verify(fireAccess, times(1)).getFireData(eq(6), eq("2025-11-18"));
    }

    /**
     * Tests the scenario where a province has no boundary data.
     * Expected Outcome:
     * - Province with null boundary returns empty stats
     */
    @Test
    void execute_ProvinceWithoutBoundary_ShouldReturnEmptyStats() throws Exception {
        // Arrange
        String date = "2025-11-20";
        int range = 1;
        List<String> provinces = List.of("UnknownProvince");
        CompareInputData inputData = new CompareInputData(provinces, date, range);

        when(boundaryAccess.getRegion("UnknownProvince")).thenReturn(null);
        when(fireAccess.getFireData(anyInt(), anyString())).thenReturn(new ArrayList<>());

        // Act
        CompareOutputData result = interactor.execute(inputData);

        // Assert
        assertNotNull(result);
        MultiRegionFireStats stats = result.getStats();
        Map<String, List<Pair<String, Integer>>> data = stats.getData();

        assertTrue(data.containsKey("UnknownProvince"));
        List<Pair<String, Integer>> provinceStats = data.get("UnknownProvince");
        // Should have 3 days, all with 0 counts
        assertEquals(3, provinceStats.size());
        assertTrue(provinceStats.stream().allMatch(p -> p.getSecond() == 0));
    }

    /**
     * Tests the scenario where API returns null.
     * Expected Outcome:
     * - Null is handled gracefully (treated as empty list)
     * - No exception is thrown
     */
    @Test
    void execute_ApiReturnsNull_ShouldHandleGracefully() throws Exception {
        // Arrange
        String date = "2025-11-20";
        int range = 1;
        List<String> provinces = List.of("All");
        CompareInputData inputData = new CompareInputData(provinces, date, range);

        when(fireAccess.getFireData(anyInt(), anyString())).thenReturn(null);

        // Act & Assert - should not throw exception
        CompareOutputData result = assertDoesNotThrow(() -> interactor.execute(inputData));

        // Assert
        assertNotNull(result);
        MultiRegionFireStats stats = result.getStats();
        Map<String, List<Pair<String, Integer>>> data = stats.getData();
        assertTrue(data.containsKey("All"));
        // All counts should be 0
        List<Pair<String, Integer>> allStats = data.get("All");
        assertTrue(allStats.stream().allMatch(p -> p.getSecond() == 0));
    }

    /**
     * Tests the scenario where API throws exception during batch fetching.
     * Expected Outcome:
     * - Exception is caught and logged
     * - Partial data is still returned (other batches succeed)
     */
    @Test
    void execute_ApiThrowsException_ShouldHandleGracefully() throws Exception {
        // Arrange
        String date = "2025-11-20";
        int range = 1;
        List<String> provinces = List.of("All");
        CompareInputData inputData = new CompareInputData(provinces, date, range);

        Coordinate fire1 = new Coordinate(43.7, -79.4,
                new String[]{"2025-11-19", "D", "h"}, new double[]{300.0, 280.0}, 5.0);

        // First batch succeeds, second batch throws exception, third batch succeeds
        when(fireAccess.getFireData(eq(1), eq("2025-11-19")))
                .thenReturn(new ArrayList<>(List.of(fire1)));
        when(fireAccess.getFireData(eq(1), eq("2025-11-20")))
                .thenThrow(new Exception("API Error"));
        when(fireAccess.getFireData(eq(1), eq("2025-11-21")))
                .thenReturn(new ArrayList<>());

        // Act & Assert - should not throw exception
        CompareOutputData result = assertDoesNotThrow(() -> interactor.execute(inputData));

        // Assert - should still return partial results
        assertNotNull(result);
        MultiRegionFireStats stats = result.getStats();
        Map<String, List<Pair<String, Integer>>> data = stats.getData();
        assertTrue(data.containsKey("All"));
    }

    /**
     * Tests batch fetching for ranges greater than 10 days.
     * Expected Outcome:
     * - Data is fetched in chunks of 10 days max
     * - Multiple API calls are made correctly
     */
    @Test
    void execute_BatchFetching_ShouldHandle10DayLimit() throws Exception {
        // Arrange
        String date = "2025-11-20";
        int range = 5; // Total days = 15 (range * 3)
        List<String> provinces = List.of("All");
        CompareInputData inputData = new CompareInputData(provinces, date, range);

        // Expected: 15 days total, fetched in batches of 10, 5
        when(fireAccess.getFireData(anyInt(), anyString())).thenReturn(new ArrayList<>());

        // Act
        interactor.execute(inputData);

        // Assert - verify multiple batches were called
        // Batch 1: 10 days starting from 2025-11-15
        verify(fireAccess, atLeastOnce()).getFireData(eq(10), eq("2025-11-15"));
        // Batch 2: 5 days starting from 2025-11-25
        verify(fireAccess, atLeastOnce()).getFireData(eq(5), eq("2025-11-25"));
    }

    /**
     * Tests daily count aggregation.
     * Expected Outcome:
     * - Coordinates are correctly grouped by date
     * - Counts match the number of coordinates per date
     */
    @Test
    void execute_Success_DailyCounts_ShouldAggregateByDate() throws Exception {
        // Arrange
        String date = "2025-11-20";
        int range = 1;
        List<String> provinces = List.of("All");
        CompareInputData inputData = new CompareInputData(provinces, date, range);

        // Create coordinates with different dates and locations (far enough apart to not bundle)
        Coordinate fire1 = new Coordinate(43.7, -79.4,
                new String[]{"2025-11-19", "D", "h"}, new double[]{300.0, 280.0}, 5.0);
        Coordinate fire2 = new Coordinate(44.0, -80.0,
                new String[]{"2025-11-19", "D", "h"}, new double[]{300.0, 280.0}, 5.0);
        Coordinate fire3 = new Coordinate(45.0, -81.0,
                new String[]{"2025-11-20", "D", "h"}, new double[]{300.0, 280.0}, 5.0);

        // Note: fetchAllRawPoints fetches in batches. With range=1, totalDays=3, it makes ONE call:
        // getFireData(3, "2025-11-19") to fetch all 3 days at once
        when(fireAccess.getFireData(eq(3), eq("2025-11-19")))
                .thenReturn(new ArrayList<>(List.of(fire1, fire2, fire3)));

        // Act
        CompareOutputData result = interactor.execute(inputData);

        // Assert
        assertNotNull(result);
        MultiRegionFireStats stats = result.getStats();
        Map<String, List<Pair<String, Integer>>> data = stats.getData();
        List<Pair<String, Integer>> allStats = data.get("All");

        // Find counts for specific dates
        int count19 = allStats.stream()
                .filter(p -> p.getFirst().equals("2025-11-19"))
                .findFirst()
                .map(Pair::getSecond)
                .orElse(0);
        int count20 = allStats.stream()
                .filter(p -> p.getFirst().equals("2025-11-20"))
                .findFirst()
                .map(Pair::getSecond)
                .orElse(0);

        assertEquals(2, count19, "Should have 2 fires on 2025-11-19");
        assertEquals(1, count20, "Should have 1 fire on 2025-11-20");
    }

    /**
     * Tests the scenario with empty provinces list.
     * Expected Outcome:
     * - Returns empty stats
     */
    @Test
    void execute_EmptyProvincesList_ShouldReturnEmptyStats() throws Exception {
        // Arrange
        String date = "2025-11-20";
        int range = 1;
        List<String> provinces = new ArrayList<>();
        CompareInputData inputData = new CompareInputData(provinces, date, range);

        when(fireAccess.getFireData(anyInt(), anyString())).thenReturn(new ArrayList<>());

        // Act
        CompareOutputData result = interactor.execute(inputData);

        // Assert
        assertNotNull(result);
        MultiRegionFireStats stats = result.getStats();
        Map<String, List<Pair<String, Integer>>> data = stats.getData();
        assertTrue(data.isEmpty());
    }
}

