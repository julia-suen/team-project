package controller;

import model.FilterSettings;
import model.WildfireEvent;
import org.jxmapviewer.viewer.GeoPosition;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DataFetchController {

	public DataFetchController() {
	}

	public List<WildfireEvent> getFires(FilterSettings filters) {

		List<WildfireEvent> fakeFires = new ArrayList<>();

		if (filters.getProvince().equals("Alberta") || filters.getProvince().equals("All")) {
			fakeFires.add(new WildfireEvent(
					"AB-001",
					new GeoPosition(51.04, -114.07), // Calgary
					50000,
					LocalDate.now(),
					true, 320.0, 50.0
			));
		}
		if (filters.getProvince().equals("British Columbia") || filters.getProvince().equals("All")) {
			fakeFires.add(new WildfireEvent(
					"BC-001",
					new GeoPosition(49.28, -123.12), // Vancouver
					25000,
					LocalDate.now(),
					true, 340.0, 80.0
			));
		}

		return fakeFires;
	}
}
