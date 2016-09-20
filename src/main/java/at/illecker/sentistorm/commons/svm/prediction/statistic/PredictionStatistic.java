package at.illecker.sentistorm.commons.svm.prediction.statistic;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import at.illecker.sentistorm.commons.Dataset;
import at.illecker.sentistorm.commons.FeaturedTweet;
import at.illecker.sentistorm.commons.Tweet;
import at.lechner.util.StopWatchCPU;
import at.lechner.weka.statistic.ConfusionMatrixStatistic;

public class PredictionStatistic {

	private StopWatchCPU stopWatchCPU;
	private int testSize = 0;

	// statistics
	private double elapsedTime = 0.0;

	// fv == 0
	private int emptyFV = 0;
	
	private ConfusionMatrixStatistic cms = new ConfusionMatrixStatistic();

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

	public void incrementEmptyFV() {
		emptyFV++;
	}

	public Double getPercentNotEmptyFV() {
		if (testSize == 0) {
			return -1.0;
		}
		return (double) (testSize - emptyFV) / testSize;
	}
	
	public ConfusionMatrixStatistic getCMS() {
		return cms;
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