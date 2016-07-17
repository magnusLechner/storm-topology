package at.illecker.sentistorm.commons.featurevector.aggregate;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.illecker.sentistorm.commons.FeaturedTweet;
import at.illecker.sentistorm.commons.Tweet;
import at.illecker.sentistorm.commons.svm.box.SVMBox;

public class ListResultsFeatureVectorGenerator extends AggregationFeatureVectorGenerator {
	private static final Logger LOG = LoggerFactory.getLogger(ListResultsFeatureVectorGenerator.class);

	private List<SVMBox> boxes;
	private int m_vectorStartId;
	private final int m_vectorSize;

	public ListResultsFeatureVectorGenerator(List<SVMBox> boxes) {
		this.boxes = boxes;
		m_vectorStartId = 1;
		m_vectorSize = boxes.size();
		LOG.info("VectorSize: " + m_vectorSize);
	}

	public ListResultsFeatureVectorGenerator(List<SVMBox> boxes, int m_vectorStartId) {
		this(boxes);
		this.m_vectorStartId = m_vectorStartId;
	}

	@Override
	public int getFeatureVectorSize() {
		return m_vectorSize;
	}

	@Override
	public Map<Integer, Double> generateFeatureVector(Tweet tweet) {
		Map<Integer, Double> resultFeatureVector = new TreeMap<Integer, Double>();
		for (int i = 0; i < boxes.size(); i++) {
			FeaturedTweet featuredTweet = boxes.get(i).getPredictor().prepareFeatureTweet(tweet);
			// If one box has empty feature vector then ignore its prediction
			// (at least for now)
			if (featuredTweet.getFeatureVector().size() > 0) {
				resultFeatureVector.put(m_vectorStartId + i, boxes.get(i).predict(featuredTweet));
			}
		}
		return resultFeatureVector;
	}
}
