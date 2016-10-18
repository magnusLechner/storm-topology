package at.storm.commons.featurevector.nopos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class NoPOSFeatureVectorGenerator {
	private static final Logger LOG = LoggerFactory.getLogger(NoPOSFeatureVectorGenerator.class);
	
	//TODO
//	private int vectorStartId = 1;
	
	public abstract int getFeatureVectorSize();

	public abstract Map<Integer, Double> generateFeatureVector(List<String> preprocessedTweets);

	public List<Map<Integer, Double>> generateFeatureVectors(List<List<String>> preprocessedTweets) {
		return generateFeatureVectors(preprocessedTweets, false);
	}

	public List<Map<Integer, Double>> generateFeatureVectors(List<List<String>> preprocessedTweets, boolean logging) {
		List<Map<Integer, Double>> featuredVectors = new ArrayList<Map<Integer, Double>>();
		for (List<String> tweet : preprocessedTweets) {
			Map<Integer, Double> featureVector = generateFeatureVector(tweet);
			if (logging) {
				LOG.info("Tweet: " + tweet);
				LOG.info("FeatureVector: " + featureVector);
			}
			featuredVectors.add(featureVector);
		}
		return featuredVectors;
	}

	//TODO
//	public int getVectorStartId() {
//		return vectorStartId;
//	}
//	
//	public void setVectorStartId(int vectorStartId) {
//		this.vectorStartId = vectorStartId;
//	}
	
}
