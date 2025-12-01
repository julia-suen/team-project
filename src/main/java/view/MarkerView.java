package view;
// might use for refactoring

//import interface_adapter.marker.MarkerController;
//import org.jxmapviewer.JXMapViewer;
//
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//import java.awt.Point;
//import java.awt.geom.Point2D;
//import java.util.Set;
//
//public class MarkerView extends MouseAdapter {
//    private final JXMapViewer map;
//    private final Set<FireWaypoint> waypoints;
//    private final MarkerController controller;
//
//    public MarkerView(JXMapViewer map,
//                      Set<FireWaypoint> waypoints,
//                      MarkerController controller) {
//        this.map = map;
//        this.waypoints = waypoints;
//        this.controller = controller;
//
//        map.addMouseMotionListener(this);
//    }
//
//    @Override
//    public void mouseMoved(MouseEvent e) {
//        FireWaypoint hovered = detectHover(e.getPoint());
//
//        controller.execute(hovered);
//    }
//
//    private FireWaypoint detectHover(Point mousePoint) {
//        for (FireWaypoint wp : waypoints) {
//            Point2D marker = map.convertGeoPositionToPoint(wp.getPosition());
//            double dx = marker.getX() - mousePoint.x;
//            double dy = marker.getY() - mousePoint.y;
//
//            if (dx*dx + dy*dy < 8*8){
//                return wp;
//            }
//        }
//        return null;
//    }
//}
