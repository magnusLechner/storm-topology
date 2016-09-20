package at.lechner.weka.statistic;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

public class WekaStatistic {

	private StandardDeviation mathStdDev = new StandardDeviation();

	private int testSize;
	private int trainSize;
	private String classifierOption;
	private String classifierName;

	private List<Double> notUnclassifiedList = new ArrayList<Double>();
	private List<ConfusionMatrixStatistic> cmsList = new ArrayList<ConfusionMatrixStatistic>();

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

	public void addNotUnclassified(Double notUnclassified) {
		notUnclassifiedList.add(notUnclassified);
	}

	public void addCMS(ConfusionMatrixStatistic confusionMatrixStatistic) {
		cmsList.add(confusionMatrixStatistic);
	}

	private Double calcAvg(List<Double> values) {
		Double avg = 0.0;
		for (Double value : values) {
			avg += value;
		}
		return avg / values.size();
	}

	private Double calcStdDev(List<Double> values) {
		if (values.size() == 0) {
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

	// ##### calculations #####

	// Micro Overall Recall
	public Double getAvgOverallRecall() {
		return calcAvg(getOverallRecalls());
	}

	public Double getStdDevOverallRecall() {
		return calcStdDev(getOverallRecalls());
	}

	private List<Double> getOverallRecalls() {
		List<Double> values = new ArrayList<Double>();
		for (int i = 0; i < cmsList.size(); i++) {
			values.add(cmsList.get(i).getOverallRecall());
		}
		return values;
	}

	// Macro Overall Recall
	public Double getAvgMacroOverallRecall() {
		return calcAvg(getMacroOverallRecalls());
	}

	public Double getStdDevMacroOverallRecall() {
		return calcStdDev(getMacroOverallRecalls());
	}

	private List<Double> getMacroOverallRecalls() {
		List<Double> values = new ArrayList<Double>();
		for (int i = 0; i < cmsList.size(); i++) {
			values.add(cmsList.get(i).getMacroOverallRecalls());
		}
		return values;
	}

	// Macro Pos/Neg Recall
	public Double getAvgMacroPosNegRecall() {
		return calcAvg(getMacroPosNegRecalls());
	}

	public Double getStdDevMacroPosNegRecall() {
		return calcStdDev(getMacroPosNegRecalls());
	}

	private List<Double> getMacroPosNegRecalls() {
		List<Double> values = new ArrayList<Double>();
		for (int i = 0; i < cmsList.size(); i++) {
			values.add(cmsList.get(i).getMacroPosNegRecalls());
		}
		return values;
	}

	// Micro Overall Precision
	public Double getAvgOverallPrecision() {
		return calcAvg(getOverallPrecisions());
	}

	public Double getStdDevOverallPrecision() {
		return calcStdDev(getOverallPrecisions());
	}

	private List<Double> getOverallPrecisions() {
		List<Double> values = new ArrayList<Double>();
		for (int i = 0; i < cmsList.size(); i++) {
			values.add(cmsList.get(i).getOverallPrecision());
		}
		return values;
	}

	// Macro Overall Precision
	public Double getAvgMacroOverallPrecision() {
		return calcAvg(getMacroOverallPrecisions());
	}

	public Double getStdDevMacroOverallPrecision() {
		return calcStdDev(getMacroOverallPrecisions());
	}

	private List<Double> getMacroOverallPrecisions() {
		List<Double> values = new ArrayList<Double>();
		for (int i = 0; i < cmsList.size(); i++) {
			values.add(cmsList.get(i).getMacroOverallPrecision());
		}
		return values;
	}

	// Macro Pos/Neg Precision
	public Double getAvgMacroPosNegPrecision() {
		return calcAvg(getMacroPosNegPrecisions());
	}

	public Double getStdDevMacroPosNegPrecision() {
		return calcStdDev(getMacroPosNegPrecisions());
	}

	private List<Double> getMacroPosNegPrecisions() {
		List<Double> values = new ArrayList<Double>();
		for (int i = 0; i < cmsList.size(); i++) {
			values.add(cmsList.get(i).getMacroPosNegPrecisions());
		}
		return values;
	}

	// Negative Recall
	public Double getAvgNegativeRecall() {
		return calcAvg(getNegativeRecalls());
	}

	public Double getStdDevNegativeRecall() {
		return calcStdDev(getNegativeRecalls());
	}

	private List<Double> getNegativeRecalls() {
		List<Double> values = new ArrayList<Double>();
		for (int i = 0; i < cmsList.size(); i++) {
			values.add(cmsList.get(i).getRecallNegative());
		}
		return values;
	}

	// Negative Precision
	public Double getAvgNegativePrecision() {
		return calcAvg(getNegativePrecisions());
	}

	public Double getStdDevNegativePrecision() {
		return calcStdDev(getNegativePrecisions());
	}

	private List<Double> getNegativePrecisions() {
		List<Double> values = new ArrayList<Double>();
		for (int i = 0; i < cmsList.size(); i++) {
			values.add(cmsList.get(i).getPrecisionNegative());
		}
		return values;
	}

	// Negative F-Measure
	public Double getAvgNegativeFMeasure() {
		return calcAvg(getNegativeFMeasure());
	}

	public Double getStdDevNegativeFMeasure() {
		return calcStdDev(getNegativeFMeasure());
	}

	private List<Double> getNegativeFMeasure() {
		List<Double> values = new ArrayList<Double>();
		for (int i = 0; i < cmsList.size(); i++) {
			values.add(cmsList.get(i).getNegativeFMeasure());
		}
		return values;
	}

	// Neutral Recall
	public Double getAvgNeutralRecall() {
		return calcAvg(getNeutralRecalls());
	}

	public Double getStdDevNeutralRecall() {
		return calcStdDev(getNeutralRecalls());
	}

	private List<Double> getNeutralRecalls() {
		List<Double> values = new ArrayList<Double>();
		for (int i = 0; i < cmsList.size(); i++) {
			values.add(cmsList.get(i).getRecallNeutral());
		}
		return values;
	}

	// Neutral Precision
	public Double getAvgNeutralPrecision() {
		return calcAvg(getNeutralPrecisions());
	}

	public Double getStdDevNeutralPrecision() {
		return calcStdDev(getNeutralPrecisions());
	}

	private List<Double> getNeutralPrecisions() {
		List<Double> values = new ArrayList<Double>();
		for (int i = 0; i < cmsList.size(); i++) {
			values.add(cmsList.get(i).getPrecisionNeutral());
		}
		return values;
	}

	// Neutral F-Measure
	public Double getAvgNeutralFMeasure() {
		return calcAvg(getNeutralFMeasure());
	}

	public Double getStdDevNeutralFMeasure() {
		return calcStdDev(getNeutralFMeasure());
	}

	private List<Double> getNeutralFMeasure() {
		List<Double> values = new ArrayList<Double>();
		for (int i = 0; i < cmsList.size(); i++) {
			values.add(cmsList.get(i).getNeutralFMeasure());
		}
		return values;
	}

	// Positive Recall
	public Double getAvgPositiveRecall() {
		return calcAvg(getPositiveRecalls());
	}

	public Double getStdDevPositiveRecall() {
		return calcStdDev(getPositiveRecalls());
	}

	private List<Double> getPositiveRecalls() {
		List<Double> values = new ArrayList<Double>();
		for (int i = 0; i < cmsList.size(); i++) {
			values.add(cmsList.get(i).getRecallPositive());
		}
		return values;
	}

	// Positive Precision
	public Double getAvgPositivePrecision() {
		return calcAvg(getPositivePrecisions());
	}

	public Double getStdDevPositivePrecision() {
		return calcStdDev(getPositivePrecisions());
	}

	private List<Double> getPositivePrecisions() {
		List<Double> values = new ArrayList<Double>();
		for (int i = 0; i < cmsList.size(); i++) {
			values.add(cmsList.get(i).getPrecisionPositive());
		}
		return values;
	}

	// Positive F-Measure
	public Double getAvgPositiveFMeasure() {
		return calcAvg(getPositiveFMeasure());
	}

	public Double getStdDevPositiveFMeasure() {
		return calcStdDev(getPositiveFMeasure());
	}

	private List<Double> getPositiveFMeasure() {
		List<Double> values = new ArrayList<Double>();
		for (int i = 0; i < cmsList.size(); i++) {
			values.add(cmsList.get(i).getPositiveFMeasure());
		}
		return values;
	}

	// Micro F-Measure
	public Double getAvgMicroFMeasure() {
		return calcAvg(getMicroFMeasure());
	}

	public Double getStdDevMicroFMeasure() {
		return calcAvg(getMicroFMeasure());
	}

	private List<Double> getMicroFMeasure() {
		List<Double> values = new ArrayList<Double>();
		for (int i = 0; i < cmsList.size(); i++) {
			values.add(cmsList.get(i).getMicroFMeasure());
		}
		return values;
	}

	// Macro F-Measure
	public Double getAvgMacroFMeasure() {
		return calcAvg(getMacroFMeasure());
	}

	public Double getStdDevMacroFMeasure() {
		return calcAvg(getMacroFMeasure());
	}

	private List<Double> getMacroFMeasure() {
		List<Double> values = new ArrayList<Double>();
		for (int i = 0; i < cmsList.size(); i++) {
			values.add(cmsList.get(i).getMacroFMeasure());
		}
		return values;
	}

}
