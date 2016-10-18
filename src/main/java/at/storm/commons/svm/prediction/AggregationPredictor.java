package at.storm.commons.svm.prediction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import at.storm.commons.Dataset;
import at.storm.commons.FeaturedTweet;
import at.storm.commons.Tweet;
import at.storm.commons.featurevector.aggregate.AggregationFeatureVectorGenerator;
import at.storm.commons.featurevector.selector.AggregationFVGSelector;
import at.storm.commons.svm.box.SVMBox;
import libsvm.svm_model;

public class AggregationPredictor extends Predictor {

	private AggregationFeatureVectorGenerator aggregationFVG;
	private boolean generateARFF;

	public AggregationPredictor(List<SVMBox> boxes, svm_model svmModel,
			Class<? extends AggregationFeatureVectorGenerator> aggregationFVG, Dataset dataset, boolean generateARFF) {
		super(svmModel, dataset);
		this.aggregationFVG = AggregationFVGSelector.selectFVG(boxes, aggregationFVG);
		this.generateARFF = generateARFF;
	}

	public AggregationPredictor(List<SVMBox> boxes, svm_model svmModel,
			AggregationFeatureVectorGenerator aggregationFVG, Dataset dataset, boolean generateARFF) {
		super(svmModel, dataset);
		this.aggregationFVG = aggregationFVG;
		this.generateARFF = generateARFF;
	}

	@Override
	public FeaturedTweet prepareFeatureTweet(Tweet tweet) {
		Map<Integer, Double> featureVector = aggregationFVG.generateFeatureVector(tweet);
		return new FeaturedTweet(tweet, featureVector);
	}

	@Override
	public List<FeaturedTweet> prepareFeatureTweets(List<Tweet> tweets) {
		List<FeaturedTweet> featuredTweets = new ArrayList<FeaturedTweet>();
		for (int i = 0; i < tweets.size(); i++) {
			Tweet tweet = tweets.get(i);
			featuredTweets.add(prepareFeatureTweet(tweet));
		}
		
		if(generateARFF) {
			generateARFF(featuredTweets, getFeatureVectorSize());
		}
		
		return featuredTweets;
	}

	@Override
	public int getFeatureVectorSize() {
		return aggregationFVG.getFeatureVectorSize();
	}

}
