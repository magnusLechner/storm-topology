package at.illecker.sentistorm.commons.featurevector.pos;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.illecker.sentistorm.commons.tfidf.pos.TweetTfIdf;
import cmu.arktweetnlp.Tagger.TaggedToken;

public class TfIdfAndPOSFeatureVectorGenerator extends FeatureVectorGenerator {
	private static final Logger LOG = LoggerFactory.getLogger(TfIdfAndPOSFeatureVectorGenerator.class);
	
	private TfIdfFeatureVectorGenerator m_tfIdfFeatureVectorGenerator = null;
	private POSFeatureVectorGenerator m_POSFeatureVectorGenerator = null;

	public TfIdfAndPOSFeatureVectorGenerator(boolean normalizePOSCounts, TweetTfIdf tfIdf) {
		m_tfIdfFeatureVectorGenerator = new TfIdfFeatureVectorGenerator(tfIdf, 1);

		m_POSFeatureVectorGenerator = new POSFeatureVectorGenerator(normalizePOSCounts,
				m_tfIdfFeatureVectorGenerator.getFeatureVectorSize() + 1);

		LOG.info("VectorSize: " + getFeatureVectorSize());
	}

	@Override
	public int getFeatureVectorSize() {
		return m_tfIdfFeatureVectorGenerator.getFeatureVectorSize()
				+ m_POSFeatureVectorGenerator.getFeatureVectorSize();
	}

	@Override
	public Map<Integer, Double> generateFeatureVector(List<TaggedToken> tweet) {
		Map<Integer, Double> featureVector = m_tfIdfFeatureVectorGenerator.generateFeatureVector(tweet);

		featureVector.putAll(m_POSFeatureVectorGenerator.generateFeatureVector(tweet));

		return featureVector;
	}
}
