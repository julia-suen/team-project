package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.time.LocalDate;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryMarker;
import org.jfree.chart.ui.Layer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.awt.Color;
import org.jfree.chart.plot.CategoryPlot;

import entities.MultiRegionFireStats;
import kotlin.Pair;

public class MultiRegionStatsPopupView extends JDialog {
    private final MultiRegionFireStats stats;
    private final JLabel loadingLabel = new JLabel("Loading...");
    private final JPanel contentPanel = new JPanel();
    private List<String> highlightDates = new ArrayList<>();

    public MultiRegionStatsPopupView(MultiRegionFireStats stats) {
        this.stats = stats;

        // basic dialog setup
        setTitle("Fire Statistics at Given Date and DateRange by Province");
        setLayout(new BorderLayout());
        loadingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loadingLabel.setVerticalAlignment(SwingConstants.CENTER);
        add(loadingLabel, BorderLayout.CENTER);
        setSize(900, 600);
    }
    // Calculates the list of dates to highlight based on user selection.
    public void setHighlightRange(String centerDate, int rangeDays) {
        this.highlightDates.clear();
        if (centerDate == null || centerDate.isEmpty()) return;

        LocalDate start = LocalDate.parse(centerDate);
    //Populate the list with dates from start date for the duration of range.
        for (int i = 0; i < rangeDays; i++) {
            this.highlightDates.add(start.plusDays(i).toString());
        }
    }

    public void updateStats(MultiRegionFireStats stats) {
        remove(loadingLabel);
        JFreeChart chart = createLineChart(stats.getData());

        if (!highlightDates.isEmpty()) {
            CategoryPlot plot = (CategoryPlot) chart.getPlot();
            DateTimeFormatter shortFormat = DateTimeFormatter.ofPattern("MM-dd");

            for (String dateStr : highlightDates) {

                String shortLabel = dateStr;
                try {
                    LocalDate d = LocalDate.parse(dateStr);
                    shortLabel = d.format(shortFormat);
                } catch (Exception e) {

                    if (dateStr.length() >= 5) shortLabel = dateStr.substring(5);
                }

                CategoryMarker marker = new CategoryMarker(shortLabel); // <--- 改用 shortLabel

                marker.setPaint(new Color(255, 255, 0, 40));
                marker.setDrawAsLine(false);

                plot.addDomainMarker(marker, Layer.BACKGROUND);
            }

            ChartPanel chartPanel = new ChartPanel(chart);

            add(chartPanel, BorderLayout.CENTER);

            revalidate();
            repaint();

        }
    }
    public void showError(String errorMessage) {
        remove(loadingLabel);
        final JLabel errorLabel = new JLabel(errorMessage);
        add(errorLabel, BorderLayout.CENTER);
    }

    private JFreeChart createLineChart(
            Map<String, List<Pair<String, Integer>>> data
    ) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        DateTimeFormatter originalFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter shortFormat = DateTimeFormatter.ofPattern("MM-dd");

        for (Map.Entry<String, List<Pair<String, Integer>>> entry : data.entrySet()) {
            String provinceName = entry.getKey();
            List<Pair<String, Integer>> monthlyPoints = entry.getValue();

            for (Pair<String, Integer> point : monthlyPoints) {
                String fullDate = point.getFirst();
                Integer fireCount = point.getSecond();

                String shortLabel = fullDate;
                try {
                    LocalDate d = LocalDate.parse(fullDate, originalFormat);
                    shortLabel = d.format(shortFormat);
                } catch (Exception e) {
                    if (fullDate.length() >= 5) {
                        shortLabel = fullDate.substring(5); // "2025-11-08" -> "11-08"
                    }
                }

                dataset.addValue(fireCount, provinceName, shortLabel);
            }
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Fire Incidents Over Time",
                "Date",
                "Number of Fires",
                dataset
        );

        CategoryPlot plot = (CategoryPlot) chart.getPlot();

        // Customize chart style: white background and visible grid lines.
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        CategoryAxis domainAxis = plot.getDomainAxis();

        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        domainAxis.setCategoryMargin(0.0);

        return chart;
    }
}
