package view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.WaypointRenderer;

public class FireMarkerRenderer implements WaypointRenderer<FireWaypoint> {
    private static final int MARKER_SIZE = 12;

    @Override
    public void paintWaypoint(final Graphics2D g, final JXMapViewer map, final FireWaypoint wp) {
        Point2D centerPoint = map.getTileFactory().geoToPixel(wp.getPosition(), map.getZoom());

        int x = (int) centerPoint.getX() - MARKER_SIZE / 2;
        int y = (int) centerPoint.getY() - MARKER_SIZE / 2;

        // dot's outline
        g.setColor(Color.WHITE);
        g.fillOval(x-1, y-1, MARKER_SIZE+2, MARKER_SIZE+2);

        // dot's fill
        g.setColor(Color.RED);
        g.fillOval(x, y, MARKER_SIZE, MARKER_SIZE);
    }
}
