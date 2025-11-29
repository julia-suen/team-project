package use_case.select_region;

import entities.Region;
import interface_adapter.region.RegionRepository;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.List;
import org.jxmapviewer.viewer.GeoPosition;

/**
 * The interactor for the Select Region use case. It contains the business logic
 * for determining which region was clicked.
 */
public class SelectRegionInteractor implements SelectRegionInputBoundary {

    private final RegionRepository regionRepository;
    private final SelectRegionOutputBoundary selectRegionPresenter;
    private final CoordinateConverter coordinateConverter;

    /**
     * Constructs a SelectRegionInteractor.
     * @param regionRepository The repository to access region data.
     * @param selectRegionPresenter The presenter to send the output to.
     * @param coordinateConverter The converter for geo-to-pixel coordinate conversions.
     */
    public SelectRegionInteractor(final RegionRepository regionRepository,
                                  final SelectRegionOutputBoundary selectRegionPresenter,
                                  final CoordinateConverter coordinateConverter) {
        this.regionRepository = regionRepository;
        this.selectRegionPresenter = selectRegionPresenter;
        this.coordinateConverter = coordinateConverter;
    }

    @Override
    public void execute(final SelectRegionInputData selectRegionInputData) {
        final GeoPosition clickPosition = selectRegionInputData.getGeoPosition();
        final Collection<Region> allRegions = this.regionRepository.getAllRegions();
        String selectedProvinceName = "None";
        boolean found = false;

        if (allRegions != null) {
            for (final Region region : allRegions) {
                if (this.pointInRegion(clickPosition, region)) {
                    selectedProvinceName = region.getProvinceName();
                    found = true;
                    break;
                }
            }
        }

        final SelectRegionOutputData outputData = new SelectRegionOutputData(selectedProvinceName);
        this.selectRegionPresenter.prepareSuccessView(outputData);
    }

    private boolean pointInRegion(final GeoPosition clickPos, final Region region) {
        final List<List<GeoPosition>> boundaries = region.getBoundary();
        if (boundaries == null) {
            return false;
        }

        final Point2D clickPoint = this.coordinateConverter.geoToPixel(clickPos);

        boolean found = false;
        for (final List<GeoPosition> geoPoly : boundaries) {
            if (geoPoly.size() < 3) {
                continue;
            }

            final Path2D path = new Path2D.Double();
            boolean first = true;
            for (final GeoPosition gp : geoPoly) {
                final Point2D pt = this.coordinateConverter.geoToPixel(gp);
                if (first) {
                    path.moveTo(pt.getX(), pt.getY());
                    first = false;
                } else {
                    path.lineTo(pt.getX(), pt.getY());
                }
            }
            path.closePath();

            if (path.contains(clickPoint)) {
                found = true;
                break;
            }
        }
        return found;
    }
}
