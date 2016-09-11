package at.lechner.weka.statistic;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

public class WekaStatistic {

	StandardDeviation mathStdDev = new StandardDeviation();

	private int testSize;
	private int trainSize;
	private String option;

	private List<Double> overallRecalls = new ArrayList<Double>();
	private List<Double> overallPrecisions = new ArrayList<Double>();

	private List<Double> negativeRecalls = new ArrayList<Double>();
	private List<Double> negativePrecisions = new ArrayList<Double>();

	private List<Double> neutralRecalls = new ArrayList<Double>();
	private List<Double> neutralPrecisions = new ArrayList<Double>();

	private List<Double> positiveRecalls = new ArrayList<Double>();
	private List<Double> positivePrecisions = new ArrayList<Double>();

	private List<Double> macroOverallRecalls = new ArrayList<Double>();
	private List<Double> macroOverallPrecisions = new ArrayList<Double>();

	private List<Double> macroPosNegRecalls = new ArrayList<Double>();
	private List<Double> macroPosNegPrecisions = new ArrayList<Double>();

	public void setTestSize(int testSize) {
		this.testSize = testSize;
	}

	public int getTestSize() {
		return testSize;
	}

	public void setTrainSize(int trainSize) {
		this.trainSize = trainSize;
	}

	public int getTrainSize() {
		return trainSize;
	}

	public void setOption(String option) {
		this.option = option;
	}

	public String getOption() {
		return option;
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

	public void addMacroOverallRecall(Double macroOverallRecall) {
		macroOverallRecalls.add(macroOverallRecall);
	}

	public void addMacroOverallPrecision(Double macroOverallPrecision) {
		macroOverallPrecisions.add(macroOverallPrecision);
	}

	public void addMacroPosNegRecall(Double macroPosNegRecall) {
		macroPosNegRecalls.add(macroPosNegRecall);
	}

	public void addMacroPosNegPrecision(Double macroPosNegPrecision) {
		macroPosNegPrecisions.add(macroPosNegPrecision);
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

	public Double getStdDevMacroOverallRecall() {
		return getStdDev(positivePrecisions);
	}

	public Double getStdDevMacroOverallPrecision() {
		return getStdDev(positivePrecisions);
	}

	public Double getStdDevMacroPosNegRecall() {
		return getStdDev(positivePrecisions);
	}

	public Double getStdDevMacroPosNegPrecision() {
		return getStdDev(positivePrecisions);
	}

	public Double calcAvg(List<Double> values) {
		Double avg = 0.0;
		for (Double value : values) {
			avg += value;
		}
		return avg / values.size();
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
