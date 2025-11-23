package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * A panel that displays a line graph of fire incident trends over time.
 */
public class GraphPanel extends JPanel {

    private static final int PADDING = 40;
    private static final int LABEL_PADDING = 20;
    private static final int PREF_WIDTH = 800;
    private static final int PREF_HEIGHT = 200;
    private static final int TITLE_HEIGHT = 30;
    private static final int TITLE_FONT_SIZE = 12;
    private static final int AXIS_FONT_SIZE = 11;
    private static final int MSG_FONT_SIZE = 14;
    private static final int TITLE_X = 10;
    private static final int TITLE_Y = 20;
    private static final int POINT_SIZE = 8;
    private static final int POINT_OFFSET = 4;
    private static final float STROKE_WIDTH = 2f;

    // Colors
    private static final Color BG_COLOR = Color.WHITE;
    private static final Color BORDER_COLOR = Color.LIGHT_GRAY;
    private static final Color TITLE_BG = new Color(245, 245, 245);
    private static final Color AXIS_COLOR = Color.BLACK;
    private static final Color GRID_COLOR = new Color(230, 230, 230);
    private static final Color LABEL_COLOR = new Color(100, 100, 100);
    private static final Color LINE_COLOR = new Color(60, 120, 200);
    private static final Color POINT_COLOR = new Color(200, 60, 60);

    private Map<Integer, Integer> data;

    /**
     * Constructs a new GraphPanel with default styling.
     */
    public GraphPanel() {
        setPreferredSize(new Dimension(PREF_WIDTH, PREF_HEIGHT));
        setBackground(BG_COLOR);
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));
    }

    /**
     * Updates the data to be displayed on the graph and repaints the component.
     * @param data a map of Year (Integer) to Incident Count (Integer)
     */
    public void setData(Map<Integer, Integer> data) {
        this.data = data;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        final int w = getWidth();
        final int h = getHeight();

        drawTitle(g2, w);

        if (data == null || data.isEmpty()) {
            drawEmptyState(g2, w, h);
        } else {
            drawGraphContent(g2, w, h);
        }
    }

    private void drawTitle(Graphics2D g2, int width) {
        g2.setColor(TITLE_BG);
        g2.fillRect(0, 0, width, TITLE_HEIGHT);
        g2.setColor(BORDER_COLOR);
        g2.drawLine(0, TITLE_HEIGHT, width, TITLE_HEIGHT);

        g2.setColor(Color.DARK_GRAY);
        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, TITLE_FONT_SIZE));
        g2.drawString("Fire Incident Trends (Yearly)", TITLE_X, TITLE_Y);
    }

    private void drawEmptyState(Graphics2D g2, int width, int height) {
        g2.setColor(Color.GRAY);
        g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, MSG_FONT_SIZE));
        final String msg = "No data loaded. Select 'National Overview' to see trends.";
        final FontMetrics fm = g2.getFontMetrics();
        final int textWidth = fm.stringWidth(msg);
        g2.drawString(msg, (width - textWidth) / 2, height / 2);
    }

    private void drawGraphContent(Graphics2D g2, int width, int height) {
        final List<Integer> years = new ArrayList<>(data.keySet());
        int maxVal = data.values().stream().max(Integer::compare).orElse(1);
        if (maxVal == 0) {
            maxVal = 1;
        }

        // Scale factors
        final double xScale = (double) (width - 2 * PADDING - LABEL_PADDING)
                / (years.size() > 1 ? years.size() - 1 : 1);
        final double yScale = (double) (height - 2 * PADDING - TITLE_HEIGHT) / maxVal;

        final List<Point> graphPoints = new ArrayList<>();
        g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, AXIS_FONT_SIZE));

        drawAxes(g2, width, height);

        // Calculate and draw points/grid
        for (int i = 0; i < years.size(); i++) {
            final int x1 = (int) (i * xScale + PADDING + LABEL_PADDING);
            final int y1 = (int) ((height - PADDING) - (data.get(years.get(i)) * yScale));
            graphPoints.add(new Point(x1, y1));

            drawGridAndLabels(g2, x1, height, years.get(i), data.get(years.get(i)), y1);
        }

        drawLinesAndPoints(g2, graphPoints);
    }

    private void drawAxes(Graphics2D g2, int width, int height) {
        g2.setColor(AXIS_COLOR);
        // Y Axis
        g2.drawLine(PADDING + LABEL_PADDING, height - PADDING, PADDING + LABEL_PADDING, PADDING + TITLE_HEIGHT);
        // X Axis
        g2.drawLine(PADDING + LABEL_PADDING, height - PADDING, width - PADDING, height - PADDING);
    }

    private void drawGridAndLabels(Graphics2D g2, int x, int height, int year, int val, int y) {
        // Draw Year Label (X Axis)
        g2.setColor(AXIS_COLOR);
        g2.drawString(String.valueOf(year), x - 15, height - PADDING + 20);

        // Draw Value Label (Above Point)
        g2.setColor(LABEL_COLOR);
        g2.drawString(String.valueOf(val), x - 10, y - 8);

        // Draw vertical grid line
        g2.setColor(GRID_COLOR);
        g2.drawLine(x, height - PADDING - 1, x, PADDING + TITLE_HEIGHT);
    }

    private void drawLinesAndPoints(Graphics2D g2, List<Point> graphPoints) {
        // Draw Lines
        g2.setColor(LINE_COLOR);
        g2.setStroke(new BasicStroke(STROKE_WIDTH));
        for (int i = 0; i < graphPoints.size() - 1; i++) {
            final int x1 = graphPoints.get(i).x;
            final int y1 = graphPoints.get(i).y;
            final int x2 = graphPoints.get(i + 1).x;
            final int y2 = graphPoints.get(i + 1).y;
            g2.drawLine(x1, y1, x2, y2);
        }

        // Draw Points
        g2.setColor(POINT_COLOR);
        for (Point p : graphPoints) {
            g2.fillOval(p.x - POINT_OFFSET, p.y - POINT_OFFSET, POINT_SIZE, POINT_SIZE);
        }
    }
}
