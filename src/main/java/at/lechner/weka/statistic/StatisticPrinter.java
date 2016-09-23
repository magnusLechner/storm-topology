package at.lechner.weka.statistic;

import java.util.ArrayList;
import java.util.List;

public class StatisticPrinter {

	private List<StringBuilder> allList = new ArrayList<StringBuilder>();

	private StringBuilder classifierName = new StringBuilder();
	private StringBuilder optionName = new StringBuilder();

	private StringBuilder overallRecall = new StringBuilder();
	private StringBuilder stdDevOverallRecall = new StringBuilder();
	private StringBuilder macroOverallRecalls = new StringBuilder();
	private StringBuilder stdDevMacroOverallRecalls = new StringBuilder();
	private StringBuilder macroPosNegRecalls = new StringBuilder();
	private StringBuilder stdDevMacroPosNegRecalls = new StringBuilder();

	private StringBuilder overallPrecision = new StringBuilder();
	private StringBuilder stdDevOverallPrecision = new StringBuilder();
	private StringBuilder macroOverallPrecisions = new StringBuilder();
	private StringBuilder stdDevMacroOverallPrecisions = new StringBuilder();
	private StringBuilder macroPosNegPrecisions = new StringBuilder();
	private StringBuilder stdDevMacroPosNegPrecisions = new StringBuilder();

	private StringBuilder negativeRecalls = new StringBuilder();
	private StringBuilder stdDevNegativeRecalls = new StringBuilder();
	private StringBuilder negativePrecisions = new StringBuilder();
	private StringBuilder stdDevNegativePrecisions = new StringBuilder();
	private StringBuilder negativeFMeasure = new StringBuilder();
	private StringBuilder stdDevNegativeFMeasure = new StringBuilder();

	private StringBuilder neutralRecalls = new StringBuilder();
	private StringBuilder stdDevNeutralRecalls = new StringBuilder();
	private StringBuilder neutralPrecisions = new StringBuilder();
	private StringBuilder stdDevNeutralPrecisions = new StringBuilder();
	private StringBuilder neutralFMeasure = new StringBuilder();
	private StringBuilder stdDevNeutralFMeasure = new StringBuilder();

	private StringBuilder positiveRecalls = new StringBuilder();
	private StringBuilder stdDevPositiveRecalls = new StringBuilder();
	private StringBuilder positivePrecisions = new StringBuilder();
	private StringBuilder stdDevPositivePrecisions = new StringBuilder();
	private StringBuilder positiveFMeasure = new StringBuilder();
	private StringBuilder stdDevPositiveFMeasure = new StringBuilder();

	private StringBuilder microFMeasure = new StringBuilder();
	private StringBuilder stdDevMicroFMeasure = new StringBuilder();
	private StringBuilder macroFMeasure = new StringBuilder();
	private StringBuilder stdDevMacroFMeasure = new StringBuilder();

	private StringBuilder microPosNegFMeasure = new StringBuilder();
	private StringBuilder stdDevMicroPosNegFMeasure = new StringBuilder();
	private StringBuilder macroPosNegFMeasure = new StringBuilder();
	private StringBuilder stdDevMacroPosNegFMeasure = new StringBuilder();

	private StringBuilder averageAccuracy = new StringBuilder();
	private StringBuilder stdDevAverageAccuracy = new StringBuilder();
	private StringBuilder negativeAverageAccuracy = new StringBuilder();
	private StringBuilder stdDevNegativeAverageAccuracy = new StringBuilder();
	private StringBuilder neutralAverageAccuracy = new StringBuilder();
	private StringBuilder stdDevNeutralAverageAccuracy = new StringBuilder();
	private StringBuilder positiveAverageAccuracy = new StringBuilder();
	private StringBuilder stdDevPositiveAverageAccuracy = new StringBuilder();

	private StringBuilder errorRate = new StringBuilder();
	private StringBuilder stdDevErrorRate = new StringBuilder();
	private StringBuilder negativeErrorRate = new StringBuilder();
	private StringBuilder stdDevNegativeErrorRate = new StringBuilder();
	private StringBuilder neutralErrorRate = new StringBuilder();
	private StringBuilder stdDevNeutralErrorRate = new StringBuilder();
	private StringBuilder positiveErrorRate = new StringBuilder();
	private StringBuilder stdDevPositiveErrorRate = new StringBuilder();

	public StatisticPrinter() {
		allList.add(classifierName);
		allList.add(optionName);

		allList.add(overallRecall);
		allList.add(stdDevOverallRecall);
		allList.add(macroOverallRecalls);
		allList.add(stdDevMacroOverallRecalls);
		allList.add(macroPosNegRecalls);
		allList.add(stdDevMacroPosNegRecalls);

		allList.add(overallPrecision);
		allList.add(stdDevOverallPrecision);
		allList.add(macroOverallPrecisions);
		allList.add(stdDevMacroOverallPrecisions);
		allList.add(macroPosNegPrecisions);
		allList.add(stdDevMacroPosNegPrecisions);

		allList.add(negativeRecalls);
		allList.add(stdDevNegativeRecalls);
		allList.add(negativePrecisions);
		allList.add(stdDevNegativePrecisions);
		allList.add(negativeFMeasure);
		allList.add(stdDevNegativeFMeasure);

		allList.add(neutralRecalls);
		allList.add(stdDevNeutralRecalls);
		allList.add(neutralPrecisions);
		allList.add(stdDevNeutralPrecisions);
		allList.add(neutralFMeasure);
		allList.add(stdDevNeutralFMeasure);

		allList.add(positiveRecalls);
		allList.add(stdDevPositiveRecalls);
		allList.add(positivePrecisions);
		allList.add(stdDevPositivePrecisions);
		allList.add(positiveFMeasure);
		allList.add(stdDevPositiveFMeasure);

		allList.add(microFMeasure);
		allList.add(stdDevMicroFMeasure);
		allList.add(macroFMeasure);
		allList.add(stdDevMacroFMeasure);

		allList.add(microPosNegFMeasure);
		allList.add(stdDevMicroPosNegFMeasure);
		allList.add(macroPosNegFMeasure);
		allList.add(stdDevMacroPosNegFMeasure);

		allList.add(averageAccuracy);
		allList.add(stdDevAverageAccuracy);
		allList.add(negativeAverageAccuracy);
		allList.add(stdDevNegativeAverageAccuracy);
		allList.add(neutralAverageAccuracy);
		allList.add(stdDevNeutralAverageAccuracy);
		allList.add(positiveAverageAccuracy);
		allList.add(stdDevPositiveAverageAccuracy);

		allList.add(errorRate);
		allList.add(stdDevErrorRate);
		allList.add(negativeErrorRate);
		allList.add(stdDevNegativeErrorRate);
		allList.add(neutralErrorRate);
		allList.add(stdDevNeutralErrorRate);
		allList.add(positiveErrorRate);
		allList.add(stdDevPositiveErrorRate);
	}

	public void appendAllTab() {
		for (StringBuilder sb : allList) {
			sb.append("\t");
		}
	}

	public void clearAll() {
		for (StringBuilder sb : allList) {
			sb.setLength(0);
		}
	}

	public String getCompleteString() {
		StringBuilder res = new StringBuilder();
		for (StringBuilder sb : allList) {
			sb.append("\n");
			res.append(sb.toString());
		}
		res.append("\n");
		return res.toString();
	}

	public void appendStandardNames() {
		overallRecall.append("Overall Recall: ").append("\t");
		stdDevOverallRecall.append("StdDev Overall Recall: ").append("\t");
		macroOverallRecalls.append("Macro Overall Recall: ").append("\t");
		stdDevMacroOverallRecalls.append("StdDev Macro Overall Recall: ").append("\t");
		macroPosNegRecalls.append("Macro Pos/Neg Recall: ").append("\t");
		stdDevMacroPosNegRecalls.append("StdDev Macro Pos/Neg Recall: ").append("\t");

		overallPrecision.append("Overall Precision: ").append("\t");
		stdDevOverallPrecision.append("StdDev Overall Precision: ").append("\t");
		macroOverallPrecisions.append("Macro Overall Precision: ").append("\t");
		stdDevMacroOverallPrecisions.append("StdDev Macro Overall Precision: ").append("\t");
		macroPosNegPrecisions.append("Macro Pos/Neg Precision: ").append("\t");
		stdDevMacroPosNegPrecisions.append("StdDev Macro Pos/Neg Precision: ").append("\t");

		negativeRecalls.append("Negative Recall: ").append("\t");
		stdDevNegativeRecalls.append("StdDev Negative Recall: ").append("\t");
		negativePrecisions.append("Negative Precision: ").append("\t");
		stdDevNegativePrecisions.append("StdDev Negative Precision: ").append("\t");
		negativeFMeasure.append("Negative F-Measure: ").append("\t");
		stdDevNegativeFMeasure.append("StdDev Negative F-Measure: ").append("\t");

		neutralRecalls.append("Neutral Recalls: ").append("\t");
		stdDevNeutralRecalls.append("StdDev Neutral Recalls: ").append("\t");
		neutralPrecisions.append("Neutral Precision: ").append("\t");
		stdDevNeutralPrecisions.append("StdDev Neutral Precision: ").append("\t");
		neutralFMeasure.append("Neutral F-Measure: ").append("\t");
		stdDevNeutralFMeasure.append("StdDev Neutral F-Measure: ").append("\t");

		positiveRecalls.append("Positive Recall: ").append("\t");
		stdDevPositiveRecalls.append("StdDev Positive Recall: ").append("\t");
		positivePrecisions.append("Positive Precision: ").append("\t");
		stdDevPositivePrecisions.append("StdDev Positive Precision: ").append("\t");
		positiveFMeasure.append("Positive F-Measure: ").append("\t");
		stdDevPositiveFMeasure.append("StdDev Positive F-Measure: ").append("\t");

		microFMeasure.append("Micro F-Measure: ").append("\t");
		stdDevMicroFMeasure.append("StdDev Micro F-Measure: ").append("\t");
		macroFMeasure.append("Macro F-Measure: ").append("\t");
		stdDevMacroFMeasure.append("StdDev Macro F-Measure: ").append("\t");

		microPosNegFMeasure.append("Micro PosNeg F-Measure: ").append("\t");
		stdDevMicroPosNegFMeasure.append("StdDev Micro PosNeg F-Measure: ").append("\t");
		macroPosNegFMeasure.append("Macro PosNeg F-Measure: ").append("\t");
		stdDevMacroPosNegFMeasure.append("StdDev Macro PosNeg F-Measure: ").append("\t");

		averageAccuracy.append("Average Accuracy: ").append("\t");
		stdDevAverageAccuracy.append("StdDev Average Accuracy: ").append("\t");
		negativeAverageAccuracy.append("Negative Average Accuracy: ").append("\t");
		stdDevNegativeAverageAccuracy.append("StdDev Negative Average Accuracy: ").append("\t");
		neutralAverageAccuracy.append("Neutral Average Accuracy: ").append("\t");
		stdDevNeutralAverageAccuracy.append("StdDev Neutral Average Accuracy: ").append("\t");
		positiveAverageAccuracy.append("Positive Average Accuracy: ").append("\t");
		stdDevPositiveAverageAccuracy.append("StdDev Positive Average Accuracy: ").append("\t");

		errorRate.append("Error Rate: ").append("\t");
		stdDevErrorRate.append("StdDev Error Rate: ").append("\t");
		negativeErrorRate.append("Negative Error Rate: ").append("\t");
		stdDevNegativeErrorRate.append("StdDev Negative Error Rate: ").append("\t");
		neutralErrorRate.append("Neutral Error Rate: ").append("\t");
		stdDevNeutralErrorRate.append("StdDev Neutral Error Rate: ").append("\t");
		positiveErrorRate.append("Positive Error Rate: ").append("\t");
		stdDevPositiveErrorRate.append("StdDev Positive Error Rate: ").append("\t");
	}

	public List<StringBuilder> getAllList() {
		return allList;
	}

	public StringBuilder getClassifierName() {
		return classifierName;
	}

	public StringBuilder getOptionName() {
		return optionName;
	}

	public StringBuilder getOverallRecall() {
		return overallRecall;
	}

	public StringBuilder getStdDevOverallRecall() {
		return stdDevOverallRecall;
	}

	public StringBuilder getMacroOverallRecalls() {
		return macroOverallRecalls;
	}

	public StringBuilder getStdDevMacroOverallRecalls() {
		return stdDevMacroOverallRecalls;
	}

	public StringBuilder getMacroPosNegRecalls() {
		return macroPosNegRecalls;
	}

	public StringBuilder getStdDevMacroPosNegRecalls() {
		return stdDevMacroPosNegRecalls;
	}

	public StringBuilder getOverallPrecision() {
		return overallPrecision;
	}

	public StringBuilder getStdDevOverallPrecision() {
		return stdDevOverallPrecision;
	}

	public StringBuilder getMacroOverallPrecisions() {
		return macroOverallPrecisions;
	}

	public StringBuilder getStdDevMacroOverallPrecisions() {
		return stdDevMacroOverallPrecisions;
	}

	public StringBuilder getMacroPosNegPrecisions() {
		return macroPosNegPrecisions;
	}

	public StringBuilder getStdDevMacroPosNegPrecisions() {
		return stdDevMacroPosNegPrecisions;
	}

	public StringBuilder getNegativeRecalls() {
		return negativeRecalls;
	}

	public StringBuilder getStdDevNegativeRecalls() {
		return stdDevNegativeRecalls;
	}

	public StringBuilder getNegativePrecisions() {
		return negativePrecisions;
	}

	public StringBuilder getStdDevNegativePrecisions() {
		return stdDevNegativePrecisions;
	}

	public StringBuilder getNegativeFMeasure() {
		return negativeFMeasure;
	}

	public StringBuilder getStdDevNegativeFMeasure() {
		return stdDevNegativeFMeasure;
	}

	public StringBuilder getNeutralRecalls() {
		return neutralRecalls;
	}

	public StringBuilder getStdDevNeutralRecalls() {
		return stdDevNeutralRecalls;
	}

	public StringBuilder getNeutralPrecisions() {
		return neutralPrecisions;
	}

	public StringBuilder getStdDevNeutralPrecisions() {
		return stdDevNeutralPrecisions;
	}

	public StringBuilder getNeutralFMeasure() {
		return neutralFMeasure;
	}

	public StringBuilder getStdDevNeutralFMeasure() {
		return stdDevNeutralFMeasure;
	}

	public StringBuilder getPositiveRecalls() {
		return positiveRecalls;
	}

	public StringBuilder getStdDevPositiveRecalls() {
		return stdDevPositiveRecalls;
	}

	public StringBuilder getPositivePrecisions() {
		return positivePrecisions;
	}

	public StringBuilder getStdDevPositivePrecisions() {
		return stdDevPositivePrecisions;
	}

	public StringBuilder getPositiveFMeasure() {
		return positiveFMeasure;
	}

	public StringBuilder getStdDevPositiveFMeasure() {
		return stdDevPositiveFMeasure;
	}

	public StringBuilder getMicroFMeasure() {
		return microFMeasure;
	}

	public StringBuilder getStdDevMicroFMeasure() {
		return stdDevMicroFMeasure;
	}

	public StringBuilder getMacroFMeasure() {
		return macroFMeasure;
	}

	public StringBuilder getStdDevMacroFMeasure() {
		return stdDevMacroFMeasure;
	}

	public StringBuilder getMicroPosNegFMeasure() {
		return microPosNegFMeasure;
	}

	public StringBuilder getStdDevMicroPosNegFMeasure() {
		return stdDevMicroPosNegFMeasure;
	}

	public StringBuilder getMacroPosNegFMeasure() {
		return macroPosNegFMeasure;
	}

	public StringBuilder getStdDevMacroPosNegFMeasure() {
		return stdDevMacroPosNegFMeasure;
	}
	
	public StringBuilder getAverageAccuracy() {
		return averageAccuracy;
	}

	public StringBuilder getStdDevAverageAccuracy() {
		return stdDevAverageAccuracy;
	}

	public StringBuilder getNegativeAverageAccuracy() {
		return negativeAverageAccuracy;
	}

	public StringBuilder getStdDevNegativeAverageAccuracy() {
		return stdDevNegativeAverageAccuracy;
	}

	public StringBuilder getNeutralAverageAccuracy() {
		return neutralAverageAccuracy;
	}

	public StringBuilder getStdDevNeutralAverageAccuracy() {
		return stdDevNeutralAverageAccuracy;
	}
	
	public StringBuilder getPositiveAverageAccuracy() {
		return positiveAverageAccuracy;
	}

	public StringBuilder getStdDevPositiveAverageAccuracy() {
		return stdDevPositiveAverageAccuracy;
	}
	
	public StringBuilder getErrorRate() {
		return errorRate;
	}
	
	public StringBuilder getStdDevErrorRate() {
		return stdDevErrorRate;
	}
	
	public StringBuilder getNegativeErrorRate() {
		return negativeErrorRate;
	}
	
	public StringBuilder getStdDevNegativeErrorRate() {
		return stdDevNegativeErrorRate;
	}

	public StringBuilder getNeutralErrorRate() {
		return neutralErrorRate;
	}
	
	public StringBuilder getStdDevNeutralErrorRate() {
		return stdDevNeutralErrorRate;
	}
	
	public StringBuilder getPositiveErrorRate() {
		return positiveErrorRate;
	}
	
	public StringBuilder getStdDevPositiveErrorRate() {
		return stdDevPositiveErrorRate;
	}
	
}
