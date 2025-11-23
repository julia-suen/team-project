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
 * A panel that displays a line graph of fire incident trends.
 * Resized for Side Panel (Width ~230px).
 */
public class GraphPanel extends JPanel {

    private static final int PADDING = 25; // Reduced padding for smaller view
    private static final int PREF_WIDTH = 230;
    private static final int PREF_HEIGHT = 180;
    private static final int TITLE_HEIGHT = 25;
    private static final int TITLE_FONT_SIZE = 11;
    private static final int AXIS_FONT_SIZE = 10;
    private static final int MSG_FONT_SIZE = 11;
    private static final int POINT_SIZE = 6;
    private static final int POINT_OFFSET = 3;
    private static final float STROKE_WIDTH = 1.5f;

    // Colors
    private static final Color BG_COLOR = Color.WHITE;
    private static final Color BORDER_COLOR = Color.LIGHT_GRAY;
    private static final Color TITLE_BG = new Color(245, 245, 245);
    private static final Color AXIS_COLOR = Color.DARK_GRAY;
    private static final Color LABEL_COLOR = new Color(100, 100, 100);
    private static final Color LINE_COLOR = new Color(60, 120, 200);
    private static final Color POINT_COLOR = new Color(200, 60, 60);

    private Map<String, Integer> data;

    /**
     * Constructs a new GraphPanel.
     */
    public GraphPanel() {
        setPreferredSize(new Dimension(PREF_WIDTH, PREF_HEIGHT));
        setBackground(BG_COLOR);
        setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, BORDER_COLOR));
    }

    /**
     * Updates the data to be displayed on the graph.
     * @param data map of Label (String) to Count (Integer)
     */
    public void setData(Map<String, Integer> data) {
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

        g2.setColor(Color.BLACK);
        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, TITLE_FONT_SIZE));
        // Centered title
        final String title = "3-Month Trend";
        final int strWidth = g2.getFontMetrics().stringWidth(title);
        g2.drawString(title, (width - strWidth) / 2, 17);
    }

    private void drawEmptyState(Graphics2D g2, int width, int height) {
        g2.setColor(Color.GRAY);
        g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, MSG_FONT_SIZE));
        final String msg = "No Data";
        final int textWidth = g2.getFontMetrics().stringWidth(msg);
        g2.drawString(msg, (width - textWidth) / 2, (height + TITLE_HEIGHT) / 2);
    }

    private void drawGraphContent(Graphics2D g2, int width, int height) {
        final List<String> keys = new ArrayList<>(data.keySet());
        int maxVal = data.values().stream().max(Integer::compare).orElse(1);
        if (maxVal == 0) {
            maxVal = 1;
        }

        // Dynamic scaling
        final double availableWidth = width - 2.0 * PADDING;
        final double availableHeight = height - 2.0 * PADDING - TITLE_HEIGHT;

        final double xScale = availableWidth / (keys.size() > 1 ? keys.size() - 1 : 1);
        final double yScale = availableHeight / maxVal;

        final List<Point> graphPoints = new ArrayList<>();
        g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, AXIS_FONT_SIZE));

        // Draw Y-Axis Line
        g2.setColor(AXIS_COLOR);
        g2.drawLine(PADDING, height - PADDING, PADDING, TITLE_HEIGHT + PADDING);
        // Draw X-Axis Line
        g2.drawLine(PADDING, height - PADDING, width - PADDING, height - PADDING);

        for (int i = 0; i < keys.size(); i++) {
            final int x1 = (int) (i * xScale + PADDING);
            final int y1 = (int) ((height - PADDING) - (data.get(keys.get(i)) * yScale));
            graphPoints.add(new Point(x1, y1));

            // Draw X Labels
            g2.setColor(AXIS_COLOR);
            g2.drawString(keys.get(i), x1 - 10, height - PADDING + 15);

            // Draw Value Labels above points
            g2.setColor(LABEL_COLOR);
            g2.drawString(String.valueOf(data.get(keys.get(i))), x1 - 5, y1 - 6);
        }

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
