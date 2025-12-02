package controller;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import entities.MultiRegionFireStats;
import entities.Province;
import usecase.compare.CompareInputBoundary;
import usecase.compare.CompareInputData;
import usecase.compare.CompareOutputData;
import view.MainFrame;
import view.MultiRegionStatsPopupView;

public class RegionalAnalysisController {
    private final MainFrame mainFrame;
    private final CompareInputBoundary compareInteractor;
    private MultiRegionFireStats stats;

    public RegionalAnalysisController(MainFrame mainFrame, CompareInputBoundary compareInteractor) {
        this.mainFrame = mainFrame;
        this.compareInteractor = compareInteractor;
        addListeners();
    }

    private void addListeners() {
        this.mainFrame.getSidePanelView().getLaunchMultiRegionAnalysisButton().addActionListener(evt ->
                // Fetch fire data for selected provinces
                showPopup()
        );
    }

    private void setStats(MultiRegionFireStats stats) {
        this.stats = stats;
    }

    public void showPopup() {
        final String date = mainFrame.getSidePanelView().getDatePickerComponent().getDateStringOrEmptyString();

        if (date.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "Please select a date.");
        } else {
            final Object selectedRange = mainFrame.getSidePanelView().getDayRangeSelector().getSelectedItem();
            List<JCheckBox> provinces = this.mainFrame.getSidePanelView().getProvinceComboCheckBox().getCheckedItems();
            List<String> provinceNames = provinces.stream().map(JCheckBox::getText).toList();

            if (provinces.size() <= 1 && !provinceNames.contains("All")) {
                JOptionPane.showMessageDialog(mainFrame, "Please select at least 2 provinces to compare.");
            } else {
                int range;
                try {
                    assert selectedRange != null;
                    if ("All".equalsIgnoreCase(selectedRange.toString())) {
                        range = 10;
                    }
                    else if (!Objects.equals(selectedRange.toString(), "All")) {
                        range = Integer.parseInt(selectedRange.toString());
                    }
                    else {
                        range = 1;
                    }
                }
                catch (NumberFormatException ex) {
                    range = 1;
                }

                if (provinceNames.contains("All")) {
                    provinceNames = Arrays.asList(Province.ALL_PROVINCES);
                }

                System.out.println("Provinces selected for analysis: " + provinceNames);

                CompareInputData input = new CompareInputData(provinceNames, date, range);

                MultiRegionStatsPopupView popupView = new MultiRegionStatsPopupView(stats);
                popupView.setHighlightRange(date, range);
                popupView.setVisible(true);

                new SwingWorker<MultiRegionFireStats, Void>() {
                    @Override
                    protected MultiRegionFireStats doInBackground() throws Exception {
                        CompareOutputData output = compareInteractor.execute(input);
                        return output.getStats();
                    }

                    @Override
                    protected void done() {
                        try {
                            MultiRegionFireStats result = get();
                            setStats(result);
                            popupView.updateStats(result);
                        } catch (Exception e) {
                            popupView.showError("Failed to load fire stats: " + e.getMessage());
                        }
                    }
                }.execute();
            }
        }
    }
}
