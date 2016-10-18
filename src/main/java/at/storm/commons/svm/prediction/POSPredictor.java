package at.storm.commons.svm.prediction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import at.storm.commons.Dataset;
import at.storm.commons.FeaturedTweet;
import at.storm.commons.Tweet;
import at.storm.commons.featurevector.pos.FeatureVectorGenerator;
import at.storm.commons.featurevector.selector.FVGSelector;
import at.storm.components.POSTagger;
import at.storm.components.Preprocessor;
import at.storm.components.Tokenizer;
import cmu.arktweetnlp.Tagger.TaggedToken;
import libsvm.svm_model;

public class POSPredictor extends Predictor {

	private FeatureVectorGenerator fvg;
	private boolean generateARFF;

	public POSPredictor(svm_model svmModel, Class<? extends FeatureVectorGenerator> featureVectorGenerator,
			Dataset dataset, boolean generateARFF) {
		super(svmModel, dataset);
		List<Tweet> trainTweets = dataset.getTrainTweets(false, true);
		this.fvg = FVGSelector.selectFVG(featureVectorGenerator, getTaggedTrainingTweets(trainTweets));
		this.generateARFF = generateARFF;
	}

	public POSPredictor(svm_model svmModel, FeatureVectorGenerator fvg, Dataset dataset, boolean generateARFF) {
		super(svmModel, dataset);
		this.fvg = fvg;
		this.generateARFF = generateARFF;
	}

	@Override
	public FeaturedTweet prepareFeatureTweet(Tweet tweet) {
		List<String> tokenizedTweet = Tokenizer.tokenize(tweet.getText());
		Preprocessor preprocessor = Preprocessor.getInstance();
		List<String> preprocessedTweet = preprocessor.preprocess(tokenizedTweet);
		POSTagger posTagger = POSTagger.getInstance();
		List<TaggedToken> taggedTweet = posTagger.tag(preprocessedTweet);
		Map<Integer, Double> featureVector = fvg.generateFeatureVector(taggedTweet);
		return new FeaturedTweet(tweet, tokenizedTweet, preprocessedTweet, taggedTweet, featureVector);
	}

	@Override
	public List<FeaturedTweet> prepareFeatureTweets(List<Tweet> tweets) {
		
		List<FeaturedTweet> featuredTestTweets = new ArrayList<FeaturedTweet>();
		List<List<String>> tokenizedTweets = Tokenizer.tokenizeTweets(tweets);
		Preprocessor preprocessor = Preprocessor.getInstance();
		List<List<String>> preprocessedTweets = preprocessor.preprocessTweets(tokenizedTweets);
		POSTagger posTagger = POSTagger.getInstance();
		List<List<TaggedToken>> taggedTweets = posTagger.tagTweets(preprocessedTweets);
		
		for (int i = 0; i < taggedTweets.size(); i++) {
			List<TaggedToken> taggedTweet = taggedTweets.get(i);
			Map<Integer, Double> featureVector = fvg.generateFeatureVector(taggedTweet);
			featuredTestTweets.add(new FeaturedTweet(tweets.get(i), tokenizedTweets.get(i), preprocessedTweets.get(i),
					taggedTweet, featureVector));
		}
		
		if(generateARFF) {
			generateARFF(featuredTestTweets, getFeatureVectorSize());
		}
		
		return featuredTestTweets;
	}

	@Override
	public int getFeatureVectorSize() {
		return fvg.getFeatureVectorSize();
	}

	public static List<List<TaggedToken>> getTaggedTrainingTweets(List<Tweet> trainTweets) {
		List<List<String>> tokenizedTweets = Tokenizer.tokenizeTweets(trainTweets);
		Preprocessor preprocessor = Preprocessor.getInstance();
		List<List<String>> preprocessedTweets = preprocessor.preprocessTweets(tokenizedTweets);
		POSTagger posTagger = POSTagger.getInstance();
		return posTagger.tagTweets(preprocessedTweets);
	}

}
