package at.lechner.weka.statistic;

import java.util.ArrayList;
import java.util.List;

public class StatisticPrinter {

	private List<StringBuilder> allList = new ArrayList<StringBuilder>();

	private StringBuilder classifierName = new StringBuilder();
	private StringBuilder optionName = new StringBuilder();
	// private StringBuilder overallRecall = new StringBuilder();
	// private StringBuilder stdDevOverallRecall = new StringBuilder();
	// private StringBuilder macroOverallRecalls = new StringBuilder();
	// private StringBuilder stdDevMacroOverallRecalls = new StringBuilder();
	// private StringBuilder macroPosNegRecalls = new StringBuilder();
	// private StringBuilder stdDevMacroPosNegRecalls = new StringBuilder();
	private StringBuilder overallPrecision = new StringBuilder();
	// private StringBuilder stdDevOverallPrecision = new StringBuilder();
	// private StringBuilder macroOverallPrecisions = new StringBuilder();
	// private StringBuilder stdDevMacroOverallPrecisions = new StringBuilder();
	// private StringBuilder macroPosNegPrecisions = new StringBuilder();
	// private StringBuilder stdDevMacroPosNegPrecisions = new StringBuilder();
	// private StringBuilder negativeRecalls = new StringBuilder();
	// private StringBuilder stdDevNegativeRecalls = new StringBuilder();
	// private StringBuilder negativePrecisions = new StringBuilder();
	// private StringBuilder stdDevNegativePrecisions = new StringBuilder();
	// private StringBuilder neutralRecalls = new StringBuilder();
	// private StringBuilder stdDevNeutralRecalls = new StringBuilder();
	// private StringBuilder neutralPrecisions = new StringBuilder();
	// private StringBuilder stdDevNeutralPrecisions = new StringBuilder();
	// private StringBuilder positiveRecalls = new StringBuilder();
	// private StringBuilder stdDevPositiveRecalls = new StringBuilder();
	// private StringBuilder positivePrecisions = new StringBuilder();
	// private StringBuilder stdDevPositivePrecisions = new StringBuilder();

	public StatisticPrinter() {
		allList.add(classifierName);
		allList.add(optionName);
		// allList.add(overallRecall);
		allList.add(overallPrecision);
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
		overallPrecision.append("Overall Precision: ").append("\t");
	}

	public StringBuilder getClassifierName() {
		return classifierName;
	}

	public StringBuilder getOptionName() {
		return optionName;
	}

	public StringBuilder getOverallPrecision() {
		return overallPrecision;
	}
	
}
