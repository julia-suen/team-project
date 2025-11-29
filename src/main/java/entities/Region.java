package entities;

import java.util.List;

import org.jxmapviewer.viewer.GeoPosition;

/**
 * Model class representing a geographical region, like a province.
 */
public class Region {

	private final String provinceName;
	private final List<List<GeoPosition>> boundary;

    public Region(String provinceName, List<List<GeoPosition>> boundary) {
        this.provinceName = provinceName;
		this.boundary = boundary;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public List<List<GeoPosition>> getBoundary() {
		return boundary;
	}

}
