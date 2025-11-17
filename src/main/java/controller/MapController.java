package controller;

import model.FilterSettings;
import model.WildfireEvent;
import view.MapView;
import view.SidePanelView;

import java.util.List;

public class MapController {

	private final MapView mapView;
	private final SidePanelView sidePanelView;
	private final DataFetchController dataFetcher;

	private final FilterSettings currentFilters = new FilterSettings();

	public MapController(MapView mapView, SidePanelView sidePanelView, DataFetchController dataFetcher) {
		this.mapView = mapView;
		this.sidePanelView = sidePanelView;
		this.dataFetcher = dataFetcher;

		addListeners();
	}

	private void addListeners() {
		sidePanelView.getLoadFiresButton().addActionListener(e -> onLoadFiresClicked());
	}

	private void onLoadFiresClicked() {
		String selectedProvince = (String) sidePanelView.getProvinceSelector().getSelectedItem();

		currentFilters.setProvince(selectedProvince);

		List<WildfireEvent> fires = dataFetcher.getFires(currentFilters);

		mapView.clearFires();

		for (WildfireEvent fire : fires) {
			mapView.addFireMarker(fire.getLocation(), fire.getRadius());
		}
	}
}
