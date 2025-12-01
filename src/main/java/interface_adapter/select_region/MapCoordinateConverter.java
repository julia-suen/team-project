package interface_adapter.select_region;

import java.awt.geom.Point2D;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;
import usecase.select_region.CoordinateConverter;

public class MapCoordinateConverter implements CoordinateConverter {

    private final JXMapViewer map;

    public MapCoordinateConverter(JXMapViewer map) {
        this.map = map;
    }

    @Override
    public Point2D geoToPixel(GeoPosition geoPosition) {
        return map.getTileFactory().geoToPixel(geoPosition, map.getZoom());
    }
}
