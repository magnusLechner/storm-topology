package at.storm.commons.svm.prediction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import at.storm.commons.Dataset;
import at.storm.commons.FeaturedTweet;
import at.storm.commons.Tweet;
import at.storm.commons.featurevector.nopos.NoPOSFeatureVectorGenerator;
import at.storm.commons.featurevector.selector.NoPOSFVGSelector;
import at.storm.components.Preprocessor;
import at.storm.components.Tokenizer;
import libsvm.svm_model;

public class NoPOSPredictor extends Predictor {

	private NoPOSFeatureVectorGenerator NoPOSfvg;
	private boolean generateARFF;

	public NoPOSPredictor(svm_model svmModel, Class<? extends NoPOSFeatureVectorGenerator> noPOSfeatureVectorGenerator,
			Dataset dataset, boolean generateARFF) {
		super(svmModel, dataset);
		List<Tweet> trainTweets = dataset.getTrainTweets(false, true);
		this.NoPOSfvg = NoPOSFVGSelector.selectFVG(noPOSfeatureVectorGenerator,
				getPreprocessedTrainingTweets(trainTweets));
		this.generateARFF = generateARFF;
	}

	public NoPOSPredictor(svm_model svmModel, NoPOSFeatureVectorGenerator NoPOSfvg, Dataset dataset, boolean generateARFF) {
		super(svmModel, dataset);
		this.NoPOSfvg = NoPOSfvg;
		this.generateARFF = generateARFF;
	}

	@Override
	public FeaturedTweet prepareFeatureTweet(Tweet tweet) {
		List<String> tokenizedTweet = Tokenizer.tokenize(tweet.getText());
		Preprocessor preprocessor = Preprocessor.getInstance();
		List<String> preprocessedTweet = preprocessor.preprocess(tokenizedTweet);
		Map<Integer, Double> featureVector = NoPOSfvg.generateFeatureVector(preprocessedTweet);
		return new FeaturedTweet(tweet, tokenizedTweet, preprocessedTweet, featureVector);
	}

	@Override
	public List<FeaturedTweet> prepareFeatureTweets(List<Tweet> tweets) {
		List<FeaturedTweet> featuredTestTweets = new ArrayList<FeaturedTweet>();
		List<List<String>> tokenizedTweets = Tokenizer.tokenizeTweets(tweets);
		Preprocessor preprocessor = Preprocessor.getInstance();
		List<List<String>> preprocessedTweets = preprocessor.preprocessTweets(tokenizedTweets);

		featuredTestTweets = new ArrayList<FeaturedTweet>();
		for (int i = 0; i < preprocessedTweets.size(); i++) {
			List<String> preprocessedTweet = preprocessedTweets.get(i);
			Map<Integer, Double> featureVector = NoPOSfvg.generateFeatureVector(preprocessedTweet);
			featuredTestTweets.add(
					new FeaturedTweet(tweets.get(i), tokenizedTweets.get(i), preprocessedTweets.get(i), featureVector));
		}
		
		if(generateARFF) {
			generateARFF(featuredTestTweets, getFeatureVectorSize());
		}

		return featuredTestTweets;
	}

	@Override
	public int getFeatureVectorSize() {
		return NoPOSfvg.getFeatureVectorSize();
	}

	public static List<List<String>> getPreprocessedTrainingTweets(List<Tweet> trainTweets) {
		List<List<String>> tokenizedTweets = Tokenizer.tokenizeTweets(trainTweets);
		Preprocessor preprocessor = Preprocessor.getInstance();
		return preprocessor.preprocessTweets(tokenizedTweets);
	}

}
