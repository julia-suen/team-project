package entities;

public class FireStats {
    private final double average;
    private final int max;
    private final double growthRate;

    public FireStats(double average, int max, double growthRate) {
        this.average = average;
        this.max = max;
        this.growthRate = growthRate;
    }

    public double getAverage() {
        return average;
    }

    public int getMax() {
        return max;
    }

    public double getGrowthRate() {
        return growthRate;
    }
}
