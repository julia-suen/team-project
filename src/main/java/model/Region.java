package model;

import org.jxmapviewer.viewer.GeoPosition;
import java.util.List;

/**
 * Model class representing a geographical region, like a province.
 */
public class Region {

  private final String provinceName;
  private final List<GeoPosition> boundary;
  private final double susceptibilityScore;

  public Region(String provinceName, List<GeoPosition> boundary, double susceptibilityScore) {
    this.provinceName = provinceName;
    this.boundary = boundary;
    this.susceptibilityScore = susceptibilityScore;
  }

  public String getProvinceName() {
    return provinceName;
  }

  public List<GeoPosition> getBoundary() {
    return boundary;
  }

  public double getSusceptibilityScore() {
    return susceptibilityScore;
  }
}
