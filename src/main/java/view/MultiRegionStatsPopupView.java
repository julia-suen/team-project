package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.Color;
import org.jfree.chart.plot.CategoryPlot;

import entities.MultiRegionFireStats;
import kotlin.Pair;

public class MultiRegionStatsPopupView extends JDialog {
    private final MultiRegionFireStats stats;
    private final JLabel loadingLabel = new JLabel("Loading...");
    private final JPanel contentPanel = new JPanel();

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

    public void updateStats(MultiRegionFireStats stats) {
        remove(loadingLabel);
        JFreeChart chart = createLineChart(stats.getData());
        ChartPanel chartPanel = new ChartPanel(chart);

        add(chartPanel, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    public void showError(String errorMessage) {
        remove(loadingLabel);
        final JLabel errorLabel = new JLabel(errorMessage);
        add(errorLabel, BorderLayout.CENTER);
    }

    private JFreeChart createLineChart(
            Map<String, List<Pair<String, Integer>>> data
    ) {
        // dataset: rows = provinces, columns = dates
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map.Entry<String, List<Pair<String, Integer>>> entry : data.entrySet()) {
            String provinceName = entry.getKey();
            List<Pair<String, Integer>> monthlyPoints = entry.getValue();

            for (Pair<String, Integer> point : monthlyPoints) {
                String date = point.getFirst();
                Integer fireCount = point.getSecond();
                dataset.addValue(fireCount, provinceName, date);
            }
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Fire Incidents Over Time",
                "Date",
                "Number of Fires",
                dataset
        );
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        return chart;

    }
}