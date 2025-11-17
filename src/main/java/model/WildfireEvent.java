package model;

import org.jxmapviewer.viewer.GeoPosition;

import java.time.LocalDate;

public class WildfireEvent {

	// State of the object
	private final String id;
	private final GeoPosition location;
	private final int radius;
	private final LocalDate acquisitionDate;
	private final boolean isDay;
	private final double brightness;
	private final double frp; // Fire Radiative Power

	// Assigns the initial values to the fields
	public WildfireEvent(String id, GeoPosition location, int radius, LocalDate acquisitionDate,
	                     boolean isDay, double brightness, double frp) {
		this.id = id;
		this.location = location;
		this.radius = radius;
		this.acquisitionDate = acquisitionDate;
		this.isDay = isDay;
		this.brightness = brightness;
		this.frp = frp;
	}

	// Public methods to let other classes READ the data
	public String getId() {
		return id;
	}

	public GeoPosition getLocation() {
		return location;
	}

	public int getRadius() {
		return radius;
	}

	public LocalDate getAcquisitionDate() {
		return acquisitionDate;
	}

	public boolean isDay() {
		return isDay;
	}

	public double getBrightness() {
		return brightness;
	}

	public double getFrp() {
		return frp;
	}
}
