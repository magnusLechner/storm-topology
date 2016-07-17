package at.illecker.sentistorm.commons.svm.prediction.statistic;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import at.illecker.sentistorm.commons.Dataset;
import at.illecker.sentistorm.commons.FeaturedTweet;
import at.illecker.sentistorm.commons.SentimentClass;
import at.illecker.sentistorm.commons.Tweet;
import at.lechner.util.StopWatchCPU;

public class PredictionStatistic {

	private StopWatchCPU stopWatchCPU;
	private int testSize = 0;

	// statistics
	private double elapsedTime = 0.0;

	private double countRecall = 0;
	private double countPrecision = 0;

	private double countTestPositive = 0;
	private double countTestNeutral = 0;
	private double countTestNegative = 0;

	private int countPositive = 0;
	private int countNeutral = 0;
	private int countNegative = 0;

	private int countCorrectPositive = 0;
	private int countCorrectNeutral = 0;
	private int countCorrectNegative = 0;

	//TODO remove everything below some day - testing stuff only
	private List<List<String>> wrongTwitchEmotePrediction = new ArrayList<List<String>>();
	private Map<String, Integer> wrongPredictedClasses = new LinkedHashMap<String, Integer>();
	private int countMsgsWithTwitchEmote = 0;
	private int countMsgsWithSentimentTwitchEmote = 0;
	private List<List<String>> wrongPredictedMessages = new ArrayList<List<String>>();
	private List<FeaturedTweet> noFeatureVectorMessages = new ArrayList<FeaturedTweet>();
	
	private double lennSum = 0.0;

	public PredictionStatistic(Dataset dataset) {
		List<Tweet> testTweets = dataset.getTestTweets(true);
		this.testSize = testTweets.size();
		for (Tweet tweet : testTweets) {
			if (SentimentClass.fromScore(dataset, tweet.getScore().intValue()) == SentimentClass.POSITIVE)
				countTestPositive++;
			if (SentimentClass.fromScore(dataset, tweet.getScore().intValue()) == SentimentClass.NEUTRAL)
				countTestNeutral++;
			if (SentimentClass.fromScore(dataset, tweet.getScore().intValue()) == SentimentClass.NEGATIVE)
				countTestNegative++;
		}

		wrongPredictedClasses.put("isPositiveButPredictedAsNeutral", 0);
		wrongPredictedClasses.put("isPositiveButPredictedAsNegative", 0);

		wrongPredictedClasses.put("isNeutralButPredictedAsPositive", 0);
		wrongPredictedClasses.put("isNeutralButPredictedAsNegative", 0);

		wrongPredictedClasses.put("isNegativeButPredictedAsPositive", 0);
		wrongPredictedClasses.put("isNegativeButPredictedAsNeutral", 0);
	}

	public void incrementCountRecall() {
		countRecall++;
	}

	public void incrementCountPrecision() {
		countPrecision++;
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
		if(countRecall == 0 && testSize == 0) {
			return 1.0;
		}
		if(testSize == 0) {
			return null;
		}
		return (double) countRecall / testSize;
	}

	public Double getPrecision() {
		if(countPrecision == 0 && countRecall == 0) {
			return 1.0;
		}
		if(countRecall == 0) {
			return null;
		}
		return (double) countPrecision / countRecall;
	}

	public Double getRecallPositives() {
		if(countPositive == 0 && countTestPositive == 0) {
			return 1.0;
		}
		if(countTestPositive == 0) {
			return null;
		}
		return (double) countPositive / countTestPositive;
	}

	public Double getPrecisionPositives() {
		if(countPrecision == 0 && countRecall == 0) {
			return 1.0;
		}
		if(countPositive == 0) {
			return null;
		}
		return (double) countCorrectPositive / countPositive;
	}

	public Double getRecallNeutrals() {
		if(countNeutral == 0 && countTestNeutral == 0) {
			return 1.0;
		}
		if(countTestNeutral == 0) {
			return null;
		}
		return (double) countNeutral / countTestNeutral;
	}

	public Double getPrecisionNeutrals() {
		if(countCorrectNeutral == 0 && countNeutral == 0) {
			return 1.0;
		}
		if(countNeutral == 0) {
			return null;
		}
		return (double) countCorrectNeutral / countNeutral;
	}

	public Double getRecallNegatives() {
		if(countNegative == 0 && countTestNegative == 0) {
			return 1.0;
		}
		if(countTestNegative == 0) {
			return null;
		}
		return (double) countNegative / countTestNegative;
	}

	public Double getPrecisionNegatives() {
		if(countCorrectNegative == 0 && countNegative == 0) {
			return 1.0;
		}
		if(countNegative == 0) {
			return null;
		}
		return (double) countCorrectNegative / countNegative;
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
	
	public Double getFMeasure() {
		if(getPrecision() == null || getRecall() == null) {
			return null;
		}
		if(getPrecision() == 0 && getRecall() == 0) {
			return null;
		}
		return (2 * getPrecision() * getRecall()) / (getPrecision() + getRecall()); 
	}
	
	//######## everything below is for testing - remove it some day #################
	
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
		if(testSize == 0) {
			return null;
		}
		return (double) countMsgsWithTwitchEmote / testSize;
	}

	public Double getMsgsWithSentimentTwitchEmote() {
		if(testSize == 0) {
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