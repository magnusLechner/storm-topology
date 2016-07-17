package at.lechner.evaluation;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashSet;

import at.lechner.commons.EvaluationResult;
import at.lechner.commons.MyTupel;
import at.lechner.commons.Sentiment;
import at.lechner.util.BasicUtil;
import at.lechner.util.EvaluationUtil;

public class DifferenceEvaluator implements EvaluationTool {

	private static final String EVALUATION_TWITCH_PATH = "resources/preparation/twitch-results-original.txt";
	private static final String EVALUATION_STORM_PATH = "evaluation/storm-results.txt";
	private static final String EVALUATION_OUTPUT_PATH = "evaluation/evaluation-result.txt";

	public static void evaluateResult(String evaTwitchPath, String evaStormPath, String evaOutputPath,
			boolean withMixed) throws IOException {
		String[] twitchLines = BasicUtil.readLines(evaTwitchPath);
		MyTupel[] twitchTupels = BasicUtil.extractTwitchLabeling(twitchLines, withMixed);

		String[] stormLines = BasicUtil.readLines(evaStormPath);
		MyTupel[] stormTupels = EvaluationUtil.extractStormResult(stormLines);

		EvaluationResult evaResult = calcDifferences(twitchTupels, stormTupels);
		createEvaluationResult(evaResult, evaOutputPath);
	}

	private static EvaluationResult calcDifferences(MyTupel[] twitchTupels, MyTupel[] stormTupels) {
		EvaluationResult evaResult = new EvaluationResult();
		HashSet<String> messages = new HashSet<String>();
		for (int i = 0; i < twitchTupels.length; i++) {
			MyTupel curTwitchTupel = twitchTupels[i];
			MyTupel curStormTupel = stormTupels[i];
			if (!messages.contains(curTwitchTupel.getText())) {
				if (curTwitchTupel.getText().equals(curStormTupel.getText())) {
					if (curTwitchTupel.getSentiment().equals(curStormTupel.getSentiment())) {
						updateEquality(curTwitchTupel, curStormTupel, evaResult);
					} else {
						updateDifferences(curTwitchTupel, curStormTupel, evaResult);
					}
					messages.add(curTwitchTupel.getText());
				} else {
					System.out.println("ERROR. MESSAGES ARE NOT EQUAL");
					break;
				}
			}
		}
		return evaResult;
	}

	private static void updateEquality(MyTupel curTwitchTupel, MyTupel curStormTupel, EvaluationResult evaResult) {
		switch (curTwitchTupel.getSentiment()) {
		case POSITIVE:
			evaResult.updateCorrectPositive();
			break;
		case NEGATIVE:
			evaResult.updateCorrectNegative();
			break;
		case NEUTRAL:
			evaResult.updateCorrectNeutral();
			break;
		case MIXED:
			// currently: handle as always wrong
			break;
		default:
			System.out.println("ERROR UPDATING EQUALITY");
		}
	}

	private static void updateDifferences(MyTupel curTwitchTupel, MyTupel curStormTupel, EvaluationResult evaResult) {
		switch (curTwitchTupel.getSentiment()) {
		case POSITIVE:
			if (curStormTupel.getSentiment().equals(Sentiment.NEGATIVE)) {
				evaResult.updatePositiveFalseNegative();
			} else if (curStormTupel.getSentiment().equals(Sentiment.NEUTRAL)) {
				evaResult.updatePositiveFalseNeutral();
			}
			break;
		case NEGATIVE:
			if (curStormTupel.getSentiment().equals(Sentiment.POSITIVE)) {
				evaResult.updateNegativeFalsePositive();
			} else if (curStormTupel.getSentiment().equals(Sentiment.NEUTRAL)) {
				evaResult.updateNegativeFalseNeutral();
			}
			break;
		case NEUTRAL:
			if (curStormTupel.getSentiment().equals(Sentiment.NEGATIVE)) {
				evaResult.updateNeutralFalseNegative();
			} else if (curStormTupel.getSentiment().equals(Sentiment.POSITIVE)) {
				evaResult.updateNeutralFalsePositive();
			}
			break;
		case MIXED:
			if (curStormTupel.getSentiment().equals(Sentiment.NEGATIVE)) {
				evaResult.updateMixedFalseNegative();
			} else if (curStormTupel.getSentiment().equals(Sentiment.POSITIVE)) {
				evaResult.updateMixedFalsePositive();
			} else if (curStormTupel.getSentiment().equals(Sentiment.NEUTRAL)) {
				evaResult.updateMixedFalseNeutral();
			}
			break;
		default:
			System.out.println("ERROR UPDATING DIFFERENCES");
		}
	}

	private static void createEvaluationResult(EvaluationResult evaResult, String outputPath) throws IOException {
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath), "utf-8"))) {
			writer.write("Correct Positive: " + evaResult.getCorrectPositive() + "\n");
			writer.write("Correct Neutral: " + evaResult.getCorrectNeutral() + "\n");
			writer.write("Correct Negative: " + evaResult.getCorrectNegative() + "\n");
			writer.write("Correct Sum: " + evaResult.getCorrectSum() + "\n");
			writer.write("\n");
			writer.write("POSITIVE but was labeled by storm as...\n");
			writer.write("\tNegative: " + evaResult.getPositiveFalseNegative() + "\n");
			writer.write("\tNeutral: " + evaResult.getPositiveFalseNeutral() + "\n");
			writer.write("\n");
			writer.write("NEUTRAL but was labeled by storm as...\n");
			writer.write("\tNegative: " + evaResult.getNeutralFalseNegative() + "\n");
			writer.write("\tPositive: " + evaResult.getNeutralFalsePositive() + "\n");
			writer.write("\n");
			writer.write("NEGATIVE but was labeled by storm as...\n");
			writer.write("\tNeutral: " + evaResult.getNegativeFalseNeutral() + "\n");
			writer.write("\tPositive: " + evaResult.getNegativeFalsePositive() + "\n");
			writer.write("\n");
			writer.write("MIXED but was labeled by storm as...\n");
			writer.write("\tPositive: " + evaResult.getMixedFalsePositive() + "\n");
			writer.write("\tNeutral: " + evaResult.getMixedFalseNeutral() + "\n");
			writer.write("\tNegative: " + evaResult.getMixedFalseNegative() + "\n");
			writer.write("\n");
			writer.write("False sum: " + evaResult.getFalseSum() + "\n");
		}
	}

	public static void main(String[] args) {
		try {
			System.out.println("START EVALUATION");
			evaluateResult(EVALUATION_TWITCH_PATH, EVALUATION_STORM_PATH, EVALUATION_OUTPUT_PATH, false);
			System.out.println("END EVALUATION");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void evaluate(boolean withMixed) {
		try {
			System.out.println("START EVALUATION");
			evaluateResult(EVALUATION_TWITCH_PATH, EVALUATION_STORM_PATH, EVALUATION_OUTPUT_PATH, withMixed);
			System.out.println("END EVALUATION");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
