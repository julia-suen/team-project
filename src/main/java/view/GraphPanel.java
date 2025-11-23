package view;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class GraphPanel extends JPanel {
    private Map<Integer, Integer> data;
    private static final int PADDING = 40;
    private static final int LABEL_PADDING = 20;

    public GraphPanel() {
        setPreferredSize(new Dimension(800, 200));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
    }

    public void setData(Map<Integer, Integer> data) {
        this.data = data;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Draw Title Background
        g2.setColor(new Color(245, 245, 245));
        g2.fillRect(0, 0, w, 30);
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawLine(0, 30, w, 30);

        // Draw Title
        g2.setColor(Color.DARK_GRAY);
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2.drawString("Fire Incident Trends (Yearly)", 10, 20);

        // Empty State Handler
        if (data == null || data.isEmpty()) {
            g2.setColor(Color.GRAY);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
            String msg = "No data loaded. Select 'National Overview' to see trends.";
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(msg);
            g2.drawString(msg, (w - textWidth) / 2, h / 2);
            return;
        }

        // Draw Axes
        g2.setColor(Color.BLACK);
        g2.drawLine(PADDING + LABEL_PADDING, h - PADDING, PADDING + LABEL_PADDING, PADDING + 30); // Y Axis
        g2.drawLine(PADDING + LABEL_PADDING, h - PADDING, w - PADDING, h - PADDING); // X Axis

        List<Integer> years = new ArrayList<>(data.keySet());
        int maxVal = data.values().stream().max(Integer::compare).orElse(1);
        if (maxVal == 0) maxVal = 1;

        // Scale factors
        double xScale = (double) (w - 2 * PADDING - LABEL_PADDING) / (years.size() > 1 ? years.size() - 1 : 1);
        double yScale = (double) (h - 2 * PADDING - 30) / maxVal;

        List<Point> graphPoints = new ArrayList<>();

        // Calculate points
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        for (int i = 0; i < years.size(); i++) {
            int x1 = (int) (i * xScale + PADDING + LABEL_PADDING);
            int y1 = (int) ((h - PADDING) - (data.get(years.get(i)) * yScale));
            graphPoints.add(new Point(x1, y1));

            // Draw Year Label (X Axis)
            g2.setColor(Color.BLACK);
            g2.drawString(years.get(i).toString(), x1 - 15, h - PADDING + 20);

            // Draw Value Label (Above Point)
            g2.setColor(new Color(100, 100, 100));
            g2.drawString(data.get(years.get(i)).toString(), x1 - 10, y1 - 8);

            // Draw vertical grid line (optional, purely aesthetic)
            g2.setColor(new Color(230, 230, 230));
            g2.drawLine(x1, h - PADDING - 1, x1, PADDING + 30);
        }

        // Draw Lines
        g2.setColor(new Color(60, 120, 200));
        g2.setStroke(new BasicStroke(2f));
        for (int i = 0; i < graphPoints.size() - 1; i++) {
            int x1 = graphPoints.get(i).x;
            int y1 = graphPoints.get(i).y;
            int x2 = graphPoints.get(i + 1).x;
            int y2 = graphPoints.get(i + 1).y;
            g2.drawLine(x1, y1, x2, y2);
        }

        // Draw Points
        g2.setColor(new Color(200, 60, 60));
        for (Point p : graphPoints) {
            g2.fillOval(p.x - 4, p.y - 4, 8, 8);
        }
    }
}