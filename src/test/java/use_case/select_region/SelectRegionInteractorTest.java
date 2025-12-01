package use_case.select_region;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import entities.Region;
import interface_adapter.region.RegionRepository;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jxmapviewer.viewer.GeoPosition;

class SelectRegionInteractorTest {

    private RegionRepository regionRepository;
    private SelectRegionOutputBoundary selectRegionPresenter;
    private SelectRegionInteractor interactor;

    @BeforeEach
    void setUp() {
        regionRepository = mock(RegionRepository.class);
        selectRegionPresenter = mock(SelectRegionOutputBoundary.class);
        CoordinateConverter coordinateConverter = mock(CoordinateConverter.class);

        interactor = new SelectRegionInteractor(regionRepository, selectRegionPresenter, coordinateConverter);

        // Mock coordinate conversions
        when(coordinateConverter.geoToPixel(any(GeoPosition.class))).thenAnswer(invocation -> {
            GeoPosition geo = invocation.getArgument(0);
            return new Point2D.Double(geo.getLongitude(), geo.getLatitude());
        });
    }

    @Test
    void testExecute_WhenNoRegionsInRepository_ShouldPrepareNoneView() {
        // Arrange
        when(regionRepository.getAllRegions()).thenReturn(null);
        SelectRegionInputData inputData = new SelectRegionInputData(new GeoPosition(0, 0));

        // Act
        interactor.execute(inputData);

        // Assert
        verify(selectRegionPresenter).prepareSuccessView(argThat(output -> output.getProvinceName().equals("None")));
    }

    @Test
    void testExecute_WhenClickIsOutsideAnyRegion_ShouldPrepareNoneView() {
        // Arrange
        List<GeoPosition> boundary = List.of(new GeoPosition(10, 10), new GeoPosition(10, 20), new GeoPosition(20, 10));
        Region region = new Region("TestRegion", List.of(boundary));
        when(regionRepository.getAllRegions()).thenReturn(Collections.singletonList(region));
        SelectRegionInputData inputData = new SelectRegionInputData(new GeoPosition(0, 0)); // Click outside

        // Act
        interactor.execute(inputData);

        // Assert
        verify(selectRegionPresenter).prepareSuccessView(argThat(output -> output.getProvinceName().equals("None")));
    }

    @Test
    void testExecute_WhenClickIsInsideRegion_ShouldPrepareSuccessView() {
        // Arrange
        List<GeoPosition> boundary = List.of(new GeoPosition(0, 0), new GeoPosition(0, 20), new GeoPosition(20, 20), new GeoPosition(20, 0));
        Region region = new Region("Ontario", List.of(boundary));
        when(regionRepository.getAllRegions()).thenReturn(Collections.singletonList(region));
        SelectRegionInputData inputData = new SelectRegionInputData(new GeoPosition(10, 10)); // Click inside

        // Act
        interactor.execute(inputData);

        // Assert
        verify(selectRegionPresenter).prepareSuccessView(argThat(output -> output.getProvinceName().equals("Ontario")));
    }

    @Test
    void testExecute_WhenRegionHasNullBoundaries_ShouldSkipAndPrepareNoneView() {
        // Arrange
        Region regionWithNullBoundary = new Region("NullBoundaryRegion", null);
        when(regionRepository.getAllRegions()).thenReturn(Collections.singletonList(regionWithNullBoundary));
        SelectRegionInputData inputData = new SelectRegionInputData(new GeoPosition(0, 0));

        // Act
        interactor.execute(inputData);

        // Assert
        verify(selectRegionPresenter).prepareSuccessView(argThat(output -> output.getProvinceName().equals("None")));
    }

    @Test
    void testExecute_WhenRegionHasInvalidPolygon_ShouldSkipAndPrepareNoneView() {
        // Arrange
        List<GeoPosition> invalidBoundary = List.of(new GeoPosition(0, 0), new GeoPosition(10, 10)); // Less than 3 points
        Region regionWithInvalidPolygon = new Region("InvalidPolygonRegion", List.of(invalidBoundary));
        when(regionRepository.getAllRegions()).thenReturn(Collections.singletonList(regionWithInvalidPolygon));
        SelectRegionInputData inputData = new SelectRegionInputData(new GeoPosition(5, 5));

        // Act
        interactor.execute(inputData);

        // Assert
        verify(selectRegionPresenter).prepareSuccessView(argThat(output -> output.getProvinceName().equals("None")));
    }

    @Test
    void testExecute_WhenClickIsInsideOneOfMultiplePolygons_ShouldPrepareSuccessView() {
        // Arrange
        List<GeoPosition> boundary1 = List.of(new GeoPosition(0, 0), new GeoPosition(0, 10), new GeoPosition(10, 0));
        List<GeoPosition> boundary2 = List.of(new GeoPosition(20, 20), new GeoPosition(20, 30), new GeoPosition(30, 20));
        Region region = new Region("MultiPolygonRegion", List.of(boundary1, boundary2));
        when(regionRepository.getAllRegions()).thenReturn(Collections.singletonList(region));
        SelectRegionInputData inputData = new SelectRegionInputData(new GeoPosition(22, 22)); // Click inside the second polygon

        // Act
        interactor.execute(inputData);

        // Assert
        verify(selectRegionPresenter).prepareSuccessView(argThat(output -> output.getProvinceName().equals("MultiPolygonRegion")));
    }
}
