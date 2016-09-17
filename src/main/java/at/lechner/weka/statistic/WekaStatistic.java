package at.lechner.weka.statistic;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

public class WekaStatistic {

	StandardDeviation mathStdDev = new StandardDeviation();

	private int testSize;
	private int trainSize;
	private String classifierOption;
	private String classifierName;

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

	private List<Double> microFMeasure = new ArrayList<Double>();
	private List<Double> macroFMeasure = new ArrayList<Double>();

	public WekaStatistic(int trainSize, int testSize, String classifierOption, String classifierName) {
		super();
		this.testSize = testSize;
		this.trainSize = trainSize;
		this.classifierOption = classifierOption;
		this.classifierName = classifierName;
	}

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

	public void setClassifierOption(String classifierOption) {
		this.classifierOption = classifierOption;
	}

	public String getClassifierOption() {
		return classifierOption;
	}

	public void setClassifierName(String classifierName) {
		this.classifierName = classifierName;
	}

	public String getClassifierName() {
		return classifierName;
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

	public void addPositiveRecall(Double positiveRecall) {
		positiveRecalls.add(positiveRecall);
	}

	public void addPositivePrecision(Double positivePrecision) {
		positivePrecisions.add(positivePrecision);
	}

	public Double getAvgOverallRecall() {
		return calcAvg(overallRecalls);
	}

	public Double getStdDevOverallRecall() {
		return calcStdDev(overallRecalls);
	}

	public Double getAvgOverallPrecision() {
		return calcAvg(overallPrecisions);
	}

	public Double getStdDevOverallPrecision() {
		return calcStdDev(overallPrecisions);
	}

	public Double getAvgNegativeRecall() {
		return calcAvg(negativeRecalls);
	}

	public Double getStdDevNegativeRecall() {
		return calcStdDev(negativeRecalls);
	}

	public Double getAvgNegativePrecision() {
		return calcAvg(negativePrecisions);
	}

	public Double getStdDevNegativePrecision() {
		return calcStdDev(negativePrecisions);
	}

	public Double getAvgNeutralRecall() {
		return calcAvg(neutralRecalls);
	}

	public Double getStdDevNeutralRecall() {
		return calcStdDev(neutralRecalls);
	}

	public Double getAvgNeutralPrecision() {
		return calcAvg(neutralPrecisions);
	}

	public Double getStdDevNeutralPrecision() {
		return calcStdDev(neutralPrecisions);
	}

	public Double getAvgPositiveRecall() {
		return calcAvg(positiveRecalls);
	}

	public Double getStdDevPositiveRecall() {
		return calcStdDev(positiveRecalls);
	}

	public Double getAvgPositivePrecision() {
		return calcAvg(positivePrecisions);
	}

	public Double getStdDevPositivePrecision() {
		return calcStdDev(positivePrecisions);
	}

	public Double getAvgMacroOverallRecall() {
		macroOverallRecalls = new ArrayList<Double>();
		for (int i = 0; i < negativeRecalls.size(); i++) {
			macroOverallRecalls.add((negativeRecalls.get(i) + neutralRecalls.get(i) + positiveRecalls.get(i)) / 3);
		}
		return calcAvg(macroOverallRecalls);
	}

	public Double getStdDevMacroOverallRecall() {
		if (macroOverallRecalls.size() == 0) {
			getAvgMacroOverallRecall();
		}
		return calcStdDev(macroOverallRecalls);
	}

	public Double getAvgMacroOverallPrecision() {
		macroOverallPrecisions = new ArrayList<Double>();
		for (int i = 0; i < negativePrecisions.size(); i++) {
			macroOverallPrecisions
					.add((negativePrecisions.get(i) + neutralPrecisions.get(i) + positivePrecisions.get(i)) / 3);
		}
		return calcAvg(macroOverallPrecisions);
	}

	public Double getStdDevMacroOverallPrecision() {
		if (macroOverallPrecisions.size() == 0) {
			getAvgMacroOverallPrecision();
		}
		return calcStdDev(macroOverallPrecisions);
	}

	public Double getAvgMacroPosNegRecall() {
		macroPosNegRecalls = new ArrayList<Double>();
		for (int i = 0; i < negativeRecalls.size(); i++) {
			macroPosNegRecalls.add((negativeRecalls.get(i) + positiveRecalls.get(i)) / 2);
		}
		return calcAvg(macroPosNegRecalls);
	}

	public Double getStdDevMacroPosNegRecall() {
		if (macroPosNegRecalls.size() == 0) {
			getAvgMacroPosNegRecall();
		}
		return calcStdDev(macroPosNegRecalls);
	}

	public Double getAvgMacroPosNegPrecision() {
		macroPosNegPrecisions = new ArrayList<Double>();
		for (int i = 0; i < negativePrecisions.size(); i++) {
			macroPosNegPrecisions.add((negativePrecisions.get(i) + positivePrecisions.get(i)) / 2);
		}
		return calcAvg(macroPosNegPrecisions);
	}

	public Double getStdDevMacroPosNegPrecision() {
		if (macroPosNegPrecisions.size() == 0) {
			getAvgMacroPosNegPrecision();
		}
		return calcStdDev(macroPosNegPrecisions);
	}

	public Double getAvgMicroFMeasure() {
		microFMeasure = new ArrayList<Double>();
		for (int i = 0; i < overallPrecisions.size(); i++) {
			Double numerator = 2 * overallPrecisions.get(i) * overallRecalls.get(i);
			Double denominator = overallPrecisions.get(i) + overallRecalls.get(i);
			microFMeasure.add(numerator / denominator);
		}
		return calcAvg(microFMeasure);
	}

	public Double getStdDevMicroFMeasure() {
		if (microFMeasure.size() == 0) {
			getAvgMicroFMeasure();
		}
		return calcStdDev(microFMeasure);
	}

	public Double getAvgMacroFMeasure() {
		macroFMeasure = new ArrayList<Double>();
		for (int i = 0; i < macroOverallPrecisions.size(); i++) {
			Double numerator = 2 * macroOverallPrecisions.get(i) * macroOverallRecalls.get(i);
			Double denominator = macroOverallPrecisions.get(i) + macroOverallRecalls.get(i);
			microFMeasure.add(numerator / denominator);
		}
		return calcAvg(macroFMeasure);
	}

	public Double getStdDevMacroFMeasure() {
		if (macroFMeasure.size() == 0) {
			getAvgMacroFMeasure();
		}
		return calcStdDev(macroFMeasure);
	}

	public Double calcAvg(List<Double> values) {
		Double avg = 0.0;
		for (Double value : values) {
			avg += value;
		}
		return avg / values.size();
	}

	private Double calcStdDev(List<Double> values) {
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
