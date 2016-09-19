package at.illecker.sentistorm.commons.svm.prediction.statistic;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import at.illecker.sentistorm.commons.Dataset;
import at.illecker.sentistorm.commons.FeaturedTweet;
import at.illecker.sentistorm.commons.Tweet;
import at.lechner.util.StopWatchCPU;

public class PredictionStatistic {

	private StopWatchCPU stopWatchCPU;
	private int testSize = 0;

	// statistics
	private double elapsedTime = 0.0;

	private int countPositive = 0;
	private int countNeutral = 0;
	private int countNegative = 0;

	private int countCorrectPositive = 0;
	private int countCorrectNeutral = 0;
	private int countCorrectNegative = 0;

	private int countIncorrectPositive = 0;
	private int countIncorrectNeutral = 0;
	private int countIncorrectNegative = 0;

	private int countMsgsWithTwitchEmote = 0;
	private int countMsgsWithSentimentTwitchEmote = 0;
	private List<List<String>> wrongTwitchEmotePrediction = new ArrayList<List<String>>();
	private Map<String, Integer> wrongPredictedClasses = new LinkedHashMap<String, Integer>();
	private List<List<String>> wrongPredictedMessages = new ArrayList<List<String>>();
	private List<FeaturedTweet> noFeatureVectorMessages = new ArrayList<FeaturedTweet>();

	private double lennSum = 0.0;

	public PredictionStatistic(Dataset dataset) {
		List<Tweet> testTweets = dataset.getTestTweets(true);
		this.testSize = testTweets.size();

		wrongPredictedClasses.put("isPositiveButPredictedAsNeutral", 0);
		wrongPredictedClasses.put("isPositiveButPredictedAsNegative", 0);

		wrongPredictedClasses.put("isNeutralButPredictedAsPositive", 0);
		wrongPredictedClasses.put("isNeutralButPredictedAsNegative", 0);

		wrongPredictedClasses.put("isNegativeButPredictedAsPositive", 0);
		wrongPredictedClasses.put("isNegativeButPredictedAsNeutral", 0);
	}

	public void incrementCountPositive() {
		countPositive++;
	}

	public void incrementCountNeutral() {
		countNeutral++;
	}

	public void incrementCountNegative() {
		countNegative++;
	}

	public void incrementCountCorrectPositive() {
		countCorrectPositive++;
	}

	public void incrementCountCorrectNeutral() {
		countCorrectNeutral++;
	}

	public void incrementCountCorrectNegative() {
		countCorrectNegative++;
	}

	public void incrementCountIncorrectPositive() {
		countIncorrectPositive++;
	}

	public void incrementCountIncorrectNeutral() {
		countIncorrectNeutral++;
	}

	public void incrementCountIncorrectNegative() {
		countIncorrectNegative++;
	}

	public void incrementIsNegativeButPredictedAsNeutral() {
		wrongPredictedClasses.put("isNegativeButPredictedAsNeutral",
				wrongPredictedClasses.get("isNegativeButPredictedAsNeutral") + 1);
	}

	public void incrementIsNegativeButPredictedAsPositive() {
		wrongPredictedClasses.put("isNegativeButPredictedAsPositive",
				wrongPredictedClasses.get("isNegativeButPredictedAsPositive") + 1);
	}

	public void incrementIsNeutralButPredictedAsNegative() {
		wrongPredictedClasses.put("isNeutralButPredictedAsNegative",
				wrongPredictedClasses.get("isNeutralButPredictedAsNegative") + 1);
	}

	public void incrementIsNeutralButPredictedAsPositive() {
		wrongPredictedClasses.put("isNeutralButPredictedAsPositive",
				wrongPredictedClasses.get("isNeutralButPredictedAsPositive") + 1);
	}

	public void incrementIsPositiveButPredictedAsNegative() {
		wrongPredictedClasses.put("isPositiveButPredictedAsNegative",
				wrongPredictedClasses.get("isPositiveButPredictedAsNegative") + 1);
	}

	public void incrementIsPositiveButPredictedAsNeutral() {
		wrongPredictedClasses.put("isPositiveButPredictedAsNeutral",
				wrongPredictedClasses.get("isPositiveButPredictedAsNeutral") + 1);
	}

	public Double getRecall() {
		if (testSize == 0) {
			return -1.0;
		}
		if ((countNegative + countNeutral + countPositive) == 0) {
			return 0.0;
		}
		return (double) (countNegative + countNeutral + countPositive) / testSize;
	}

	public Double getPrecision() {
		if ((countNegative + countNeutral + countPositive) == 0) {
			return -1.0;
		}
		if ((countCorrectNegative + countCorrectNeutral + countCorrectPositive) == 0) {
			return 0.0;
		}
		return (double) (countCorrectNegative + countCorrectNeutral + countCorrectPositive)
				/ (countNegative + countNeutral + countPositive);
	}

	public Double getRecallPositives() {
		if (countPositive == 0) {
			return -1.0;
		}
		if (countCorrectPositive == 0) {
			return 0.0;
		}
		return (double) countCorrectPositive / countPositive;
	}

	public Double getPrecisionPositives() {
		if ((countCorrectPositive + countIncorrectPositive) == 0) {
			return -1.0;
		}
		if (countCorrectPositive == 0) {
			return 0.0;
		}
		return (double) countCorrectPositive / (countCorrectPositive + countIncorrectPositive);
	}

	public Double getRecallNeutrals() {
		if (countNeutral == 0) {
			return -1.0;
		}
		if (countCorrectNeutral == 0) {
			return 0.0;
		}
		return (double) countCorrectNeutral / countNeutral;
	}

	public Double getPrecisionNeutrals() {
		if ((countCorrectNeutral + countIncorrectNeutral) == 0) {
			return -1.0;
		}
		if (countCorrectNeutral == 0) {
			return 0.0;
		}
		return (double) countCorrectNeutral / (countCorrectNeutral + countIncorrectNeutral);
	}

	public Double getRecallNegatives() {
		if (countNegative == 0) {
			return -1.0;
		}
		if (countCorrectNegative == 0) {
			return 0.0;
		}
		return (double) countCorrectNegative / countNegative;
	}

	public Double getPrecisionNegatives() {
		if ((countCorrectNegative + countIncorrectNegative) == 0) {
			return -1.0;
		}
		if (countCorrectNegative == 0) {
			return 0.0;
		}
		return (double) countCorrectNegative / (countCorrectNegative + countIncorrectNegative);
	}

	public Double getPositiveFMeasure() {
		Double recall = getRecallPositives();
		Double precision = getPrecisionPositives();
		return calcFMeasure(precision, recall);
	}

	public Double getNeutralFMeasure() {
		Double recall = getRecallNeutrals();
		Double precision = getPrecisionNeutrals();
		return calcFMeasure(precision, recall);
	}

	public Double getNegativeFMeasure() {
		Double recall = getRecallNegatives();
		Double precision = getPrecisionNegatives();
		return calcFMeasure(precision, recall);
	}

	public Double getMacroFMeasure() {
		Double numerator = getPositiveFMeasure() + getNeutralFMeasure() + getNegativeFMeasure();
		Double denominator = 3.0;
		return numerator / denominator;
	}

	public Double getMacroPosNegFMeasure() {
		Double numerator = getPositiveFMeasure() + getNegativeFMeasure();
		Double denominator = 2.0;
		return numerator / denominator;
	}

	public Double getMicroFMeasure() {
		Double precision = getPrecisionForMicroFMeasure();
		Double recall = getRecallForMicroFMeasure();
		return calcFMeasure(precision, recall);
	}

	public Double getPrecisionForMicroFMeasure() {
		Double numerator = (double) countCorrectPositive + countCorrectNeutral + countCorrectNegative;
		Double denominator = (double) (countCorrectPositive + countIncorrectPositive)
				+ (countCorrectNeutral + countIncorrectNeutral) + (countCorrectNegative + countIncorrectNegative);
		return check(numerator, denominator);
	}

	public Double getRecallForMicroFMeasure() {
		Double numerator = (double) countCorrectPositive + countCorrectNeutral + countCorrectNegative;
		Double denominator = (double) countPositive + countNeutral + countNegative;
		return check(numerator, denominator);
	}

	public Double getMicroPosNegFMeasure() {
		Double precision = getPrecisionForMicroPosNegFMeasure();
		Double recall = getRecallForMicroPosNegFMeasure();
		return calcFMeasure(precision, recall);
	}

	public Double getPrecisionForMicroPosNegFMeasure() {
		Double numerator = (double) countCorrectPositive + countCorrectNegative;
		Double denominator = (double) (countCorrectPositive + countIncorrectPositive)
				+ (countCorrectNegative + countIncorrectNegative);
		return check(numerator, denominator);
	}

	public Double getRecallForMicroPosNegFMeasure() {
		Double numerator = (double) countCorrectPositive + countCorrectNegative;
		Double denominator = (double) countPositive + countNegative;
		return check(numerator, denominator);
	}

	private Double calcFMeasure(Double precision, Double recall) {
		if (precision == 0 && recall == 0) {
			return -1.0;
		}
		return (2 * precision * recall) / (precision + recall);
	}

	private Double check(Double numerator, Double denominator) {
		if (denominator == 0) {
			return -1.0;
		}
		return numerator / denominator;
	}

	public void startNewStopWatch() {
		stopWatchCPU = new StopWatchCPU();
	}

	public void elapsedTime() {
		elapsedTime += stopWatchCPU.elapsedTime();
	}

	public double getSumElapsedTime() {
		return elapsedTime;
	}

	public void resetTime() {
		elapsedTime = 0.0;
	}

	// ######## everything below is for testing - remove it some day #################

	public int getIsNegativeButPredictedAsNeutral() {
		return wrongPredictedClasses.get("isNegativeButPredictedAsNeutral");
	}

	public int getIsNegativeButPredictedAsPositive() {
		return wrongPredictedClasses.get("isNegativeButPredictedAsPositive");
	}

	public int getIsNeutralButPredictedAsNegative() {
		return wrongPredictedClasses.get("isNeutralButPredictedAsNegative");
	}

	public int getIsNeutralButPredictedAsPositive() {
		return wrongPredictedClasses.get("isNeutralButPredictedAsPositive");
	}

	public int getIsPositiveButPredictedAsNegative() {
		return wrongPredictedClasses.get("isPositiveButPredictedAsNegative");
	}

	public int getIsPositiveButPredictedAsNeutral() {
		return wrongPredictedClasses.get("isPositiveButPredictedAsNeutral");
	}

	public void addToWrongTwitchEmotePrediction(List<String> data) {
		wrongTwitchEmotePrediction.add(data);
	}

	public List<List<String>> getWrongTwitchEmotePrediction() {
		return wrongTwitchEmotePrediction;
	}

	public void incrementCountMsgsWithTwitchEmote() {
		countMsgsWithTwitchEmote++;
	}

	public void incrementCountMsgsWithSentimentTwitchEmote() {
		countMsgsWithSentimentTwitchEmote++;
	}

	public Double getMsgsWithTwitchEmote() {
		if (testSize == 0) {
			return null;
		}
		return (double) countMsgsWithTwitchEmote / testSize;
	}

	public Double getMsgsWithSentimentTwitchEmote() {
		if (testSize == 0) {
			return null;
		}
		return (double) countMsgsWithSentimentTwitchEmote / testSize;
	}

	public void addWrongPredictedMessages(List<String> data) {
		wrongPredictedMessages.add(data);
	}

	public List<List<String>> getWrongPredictedMessages() {
		return wrongPredictedMessages;
	}

	public void addNoFeatureVectorMessages(FeaturedTweet featuredTweet) {
		noFeatureVectorMessages.add(featuredTweet);
	}

	public List<FeaturedTweet> getNoFeatureVectorMessages() {
		return noFeatureVectorMessages;
	}

	public void addToLennSum(double value) {
		lennSum += value;
	}

	public double getLennSum() {
		return lennSum;
	}

}