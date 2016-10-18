package at.storm.commons.featurevector.aggregate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.storm.commons.Tweet;

public abstract class AggregationFeatureVectorGenerator {
	private static final Logger LOG = LoggerFactory.getLogger(AggregationFeatureVectorGenerator.class);
	
	public abstract int getFeatureVectorSize();

	public abstract Map<Integer, Double> generateFeatureVector(Tweet tweet);

	public List<Map<Integer, Double>> generateFeatureVectors(List<Tweet> tweets) {
		return generateFeatureVectors(tweets, false);
	}

	public List<Map<Integer, Double>> generateFeatureVectors(List<Tweet> tweets, boolean logging) {
		List<Map<Integer, Double>> featuredVectors = new ArrayList<Map<Integer, Double>>();
		for (Tweet tweet : tweets) {
			Map<Integer, Double> featureVector = generateFeatureVector(tweet);
			if (logging) {
				LOG.info("Tweet: " + tweet);
				LOG.info("FeatureVector: " + featureVector);
			}
			featuredVectors.add(featureVector);
		}
		return featuredVectors;
	}
}
