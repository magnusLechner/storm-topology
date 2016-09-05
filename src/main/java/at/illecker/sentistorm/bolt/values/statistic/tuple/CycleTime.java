package at.illecker.sentistorm.bolt.values.statistic.tuple;

public abstract class CycleTime {

	private double min;
	private double max;
	private double avg;
	private double stdDev;
	
	public CycleTime(double min, double max, double avg, double stdDev) {
		super();
		this.min = min;
		this.max = max;
		this.avg = avg;
		this.stdDev = stdDev;
	}

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}

	public double getAvg() {
		return avg;
	}

	public double getStdDev() {
		return stdDev;
	}
	
}
