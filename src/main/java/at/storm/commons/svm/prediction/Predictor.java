package at.storm.commons.svm.prediction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.storm.commons.Configuration;
import at.storm.commons.Dataset;
import at.storm.commons.FeaturedTweet;
import at.storm.commons.Tweet;
import at.storm.commons.dict.TwitchEmoticons;
import at.storm.commons.svm.prediction.statistic.PredictionStatistic;
import at.storm.commons.util.io.FileUtils;
import at.storm.commons.util.io.IOUtils;
import at.storm.commons.util.io.SerializationUtils;
import at.testing.preparation.ARFFParser;
import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;

public abstract class Predictor {
	private static final Logger LOG = LoggerFactory.getLogger(Predictor.class);
	// classes 0 = negative, 1 = neutral, 2 = positive
	private static final int TOTAL_CLASSES = 3;

	private svm_model svmModel;
	private PredictionStatistic predictionStatistic;

	// TODO throw this out when finished with testing
	private Map<String, Double> emoticonList = new HashMap<String, Double>();

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Predictor(svm_model svmModel, Dataset dataset) {
		super();
		this.svmModel = svmModel;
		this.predictionStatistic = new PredictionStatistic(dataset);

		// TODO throw this out when finished with testing
		// get only Sentiment Dictionary named "twitch_emoticons"
		List<Map> wordLists = Configuration.getSentimentWordlists();
		for (Map wordListEntry : wordLists) {
			String name = (String) wordListEntry.get("name");
			if (name.equals("twitch_emoticons")) {
				String file = (String) wordListEntry.get("path");
				String separator = (String) wordListEntry.get("delimiter");
				boolean containsPOSTags = (Boolean) wordListEntry.get("containsPOSTags");
				boolean featureScaling = (Boolean) wordListEntry.get("featureScaling");
				double minValue = (Double) wordListEntry.get("minValue");
				double maxValue = (Double) wordListEntry.get("maxValue");

				// Try deserialization of file
				String serializationFile = file + ".ser";
				if (IOUtils.exists(serializationFile)) {
					LOG.info("Deserialize WordList from: " + serializationFile);
					emoticonList = (Map<String, Double>) SerializationUtils.deserialize(serializationFile);
				} else {
					LOG.info("Load WordList from: " + file);
					emoticonList = FileUtils.readFile(file, separator, containsPOSTags, featureScaling, minValue,
							maxValue);
					SerializationUtils.serializeMap(emoticonList, serializationFile);
				}
			}
		}
	}

	public abstract FeaturedTweet prepareFeatureTweet(Tweet tweet);

	public abstract List<FeaturedTweet> prepareFeatureTweets(List<Tweet> tweets);

	public abstract int getFeatureVectorSize();

	public double predict(Tweet tweet) {
		FeaturedTweet featuredTweet = prepareFeatureTweet(tweet);
		return predict(featuredTweet);
	}

	public double predict(FeaturedTweet featuredTweet) {
		Map<Integer, Double> featureVector = featuredTweet.getFeatureVector();

		double predictedClass = evaluate(featureVector, svmModel, TOTAL_CLASSES);

		countTwitchEmoticons(featuredTweet);

		// for lenns evaluation
//		System.out.println("SENTIMENT:  " + predictedClass + "   TEXT: " + featuredTweet.getText());
		updateLennSum(predictedClass);

		// if FV.size == 0 -> always neutral => only count the message if FV is
		// not empty
		if (featureVector.size() > 0) {
			// class 0: Negative, 1: Neutral, 2: Positive
			int actualClass = featuredTweet.getScore().intValue();

			if (predictedClass != actualClass) {
				// compare predicted classes for wrong predicted messages
				analyseWrongPredictions(predictedClass, actualClass);
				// collect all messages which contain Sentiment-Twitch-Emoticons
				// but are wrong predicted
				collectWrongPredictedMsgs(featuredTweet, actualClass, predictedClass);
			}
			
			// thats the important step - updating the confusion matrix
			predictionStatistic.getCMS().updateCM((int) actualClass, (int) predictedClass);

		} else {
			predictionStatistic.incrementEmptyFV();
			collectNoFeatureVectorMessages(featuredTweet);
		}
		return predictedClass;
	}

	public List<Double> predict(List<Tweet> tweets) {
		List<Double> predictions = new ArrayList<Double>();
		List<FeaturedTweet> featuredTweets = prepareFeatureTweets(tweets);
		for (FeaturedTweet featuredTweet : featuredTweets) {
			predictions.add(predict(featuredTweet));
		}
		return predictions;
	}

	public static double evaluate(Map<Integer, Double> featureVector, svm_model svmModel, int totalClasses) {
		return evaluate(featureVector, svmModel, totalClasses, false);
	}

	public static double evaluate(Map<Integer, Double> featureVector, svm_model svmModel, int totalClasses,
			boolean logging) {

		// set feature nodes
		svm_node[] testNodes = new svm_node[featureVector.size()];
		int i = 0;
		for (Map.Entry<Integer, Double> feature : featureVector.entrySet()) {
			svm_node node = new svm_node();
			node.index = feature.getKey();
			node.value = feature.getValue();
			testNodes[i] = node;
			i++;
		}

		double predictedClass = svm.svm_predict(svmModel, testNodes);

		if (logging) {
			int[] labels = new int[totalClasses];
			svm.svm_get_labels(svmModel, labels);

			double[] probEstimates = new double[totalClasses];
			double predictedClassProb = svm.svm_predict_probability(svmModel, testNodes, probEstimates);

			for (i = 0; i < totalClasses; i++) {
				LOG.info("Label[" + i + "]: " + labels[i] + " Probability: " + probEstimates[i]);
			}
			LOG.info("PredictedClass: " + predictedClassProb);
		}

		return predictedClass;
	}

	public void shutdown() {
		svm.EXEC_SERV.shutdown();
	}

	public PredictionStatistic getPredictionStatistic() {
		return predictionStatistic;
	}

	public void generateARFF(List<FeaturedTweet> featuredTweets, int featureVectorSize) {
		String outputPath = "src/main/resources/arff/Twitch/Test.arff";
		ARFFParser.generateARFF(featuredTweets, featureVectorSize, outputPath);
	}

	private void analyseWrongPredictions(double predictedClass, int actualClass) {
		if (actualClass == 0) {
			if (predictedClass == 1) {
				predictionStatistic.incrementIsNegativeButPredictedAsNeutral();
			}
			if (predictedClass == 2) {
				predictionStatistic.incrementIsNegativeButPredictedAsPositive();
			}
		}
		if (actualClass == 1) {
			if (predictedClass == 0) {
				predictionStatistic.incrementIsNeutralButPredictedAsNegative();
			}
			if (predictedClass == 2) {
				predictionStatistic.incrementIsNeutralButPredictedAsPositive();
			}
		}
		if (actualClass == 2) {
			if (predictedClass == 0) {
				predictionStatistic.incrementIsPositiveButPredictedAsNegative();
			}
			if (predictedClass == 1) {
				predictionStatistic.incrementIsPositiveButPredictedAsNeutral();
			}
		}
	}

	private void updateLennSum(double predictedClass) {
		if (predictedClass == 0.0) {
			predictionStatistic.addToLennSum(-1.0);
		} else if (predictedClass == 1.0) {
			predictionStatistic.addToLennSum(0.0);
		} else if (predictedClass == 2.0) {
			predictionStatistic.addToLennSum(1.0);
		}
	}

	private void countTwitchEmoticons(FeaturedTweet featuredTweet) {
		boolean containsTwitchEmoticon = false;
		boolean containsTwitchSentimentEmoticon = false;
		for (String token : featuredTweet.getTokens()) {
			if (TwitchEmoticons.getInstance().isTwitchEmoticon(token)) {
				containsTwitchEmoticon = true;
			}
			if (emoticonList.containsKey(token.toLowerCase())) {
				containsTwitchSentimentEmoticon = true;
			}
		}
		if (containsTwitchEmoticon) {
			predictionStatistic.incrementCountMsgsWithTwitchEmote();
		}
		if (containsTwitchSentimentEmoticon) {
			predictionStatistic.incrementCountMsgsWithSentimentTwitchEmote();
		}
	}

	private void collectWrongPredictedMsgs(FeaturedTweet featuredTweet, int actualClass, double predictedClass) {
		List<String> data = new ArrayList<String>();

		data.add(featuredTweet.getText());
		data.add(String.valueOf(actualClass));
		data.add(String.valueOf(predictedClass));
		data.add(featuredTweet.getFeatureVector().toString());

		predictionStatistic.addWrongPredictedMessages(data);
	}

	private void collectNoFeatureVectorMessages(FeaturedTweet featuredTweet) {
		predictionStatistic.addNoFeatureVectorMessages(featuredTweet);
	}

}
