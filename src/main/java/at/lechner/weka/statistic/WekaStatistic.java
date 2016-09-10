package at.lechner.weka.statistic;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

public class WekaStatistic {

	StandardDeviation mathStdDev = new StandardDeviation();

	private String option;

	private List<Double> overallRecalls = new ArrayList<Double>();
	private List<Double> overallPrecisions = new ArrayList<Double>();

	private List<Double> negativeRecalls = new ArrayList<Double>();
	private List<Double> negativePrecisions = new ArrayList<Double>();

	private List<Double> neutralRecalls = new ArrayList<Double>();
	private List<Double> neutralPrecisions = new ArrayList<Double>();

	private List<Double> positiveRecalls = new ArrayList<Double>();
	private List<Double> positivePrecisions = new ArrayList<Double>();

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

	public void addOverallRecall(Double overallRecall) {
		overallRecalls.add(overallRecall);
	}

	public void addOverallPrecision(Double overallPrecision) {
		overallPrecisions.add(overallPrecision);
	}

	public void addNegativeRecall(Double negativeRecall) {
		negativeRecalls.add(negativeRecall);
	}

	public void addNegativePrecision(Double negativePrecision) {
		negativePrecisions.add(negativePrecision);
	}

	public void addNeutralRecall(Double neutralRecall) {
		neutralRecalls.add(neutralRecall);
	}

	public void addNeutralPrecision(Double neutralPrecision) {
		neutralPrecisions.add(neutralPrecision);
	}

	public void addPostiveRecall(Double positiveRecall) {
		positiveRecalls.add(positiveRecall);
	}

	public void addPositivePrecision(Double positivePrecision) {
		positivePrecisions.add(positivePrecision);
	}

	public Double getStdDevOverallRecall() {
		return getStdDev(overallRecalls);
	}

	public Double getStdDevOverallPrecision() {
		return getStdDev(overallPrecisions);
	}

	public Double getStdDevNegativeRecall() {
		return getStdDev(negativeRecalls);
	}

	public Double getStdDevNegativePrecision() {
		return getStdDev(negativePrecisions);
	}

	public Double getStdDevNeutralRecall() {
		return getStdDev(neutralRecalls);
	}

	public Double getStdDevNeutralPrecision() {
		return getStdDev(neutralPrecisions);
	}

	public Double getStdDevPositiveRecall() {
		return getStdDev(positiveRecalls);
	}

	public Double getStdDevPositivePrecision() {
		return getStdDev(positivePrecisions);
	}

	private Double getStdDev(List<Double> values) {
		if (overallRecalls.size() == 0) {
			return null;
		}
		return mathStdDev.evaluate(toArray(values));
	}

	private double[] toArray(List<Double> values) {
		double[] result = new double[values.size()];
		for (int i = 0; i < values.size(); i++) {
			result[i] = values.get(i);
		}
		return result;
	}

}
