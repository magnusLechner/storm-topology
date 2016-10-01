package at.illecker.sentistorm.commons.featurevector.nopos;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.illecker.sentistorm.commons.Configuration;
import at.illecker.sentistorm.commons.tfidf.ngram.MessageNGrams;

public class NoPOSNGramFeatureVectorGenerator extends NoPOSFeatureVectorGenerator {

	private static final Logger LOG = LoggerFactory.getLogger(NoPOSNGramFeatureVectorGenerator.class);
	private static final boolean LOGGING = Configuration.get("commons.featurevectorgenerator.tfidf.logging", false);

	private MessageNGrams m_messageNGrams = null;
	private int m_vectorStartId = 1;

	public NoPOSNGramFeatureVectorGenerator(MessageNGrams messageNGrams) {
		this.m_messageNGrams = messageNGrams;
		LOG.info("VectorSize: " + getFeatureVectorSize());
	}

	public NoPOSNGramFeatureVectorGenerator(MessageNGrams messageNGrams, int vectorStartId) {
		this(messageNGrams);
		this.m_vectorStartId = vectorStartId;
	}

	@Override
	public int getFeatureVectorSize() {
		return m_messageNGrams.getInverseDocFreq().size();
	}

	@Override
	public Map<Integer, Double> generateFeatureVector(List<String> preprocessedTweet) {
		return generateFeatureVector(m_messageNGrams.tfIdfFromTokens(preprocessedTweet));
	}

	public Map<Integer, Double> generateFeatureVector(Map<String, Double> tfIdf) {
		Map<Integer, Double> resultFeatureVector = new TreeMap<Integer, Double>();

		if (m_messageNGrams != null) {
			// Map<String, Double> idf = m_tweetTfIdf.getInverseDocFreq();
			Map<String, Integer> termIds = m_messageNGrams.getTermIds();

			for (Map.Entry<String, Double> element : tfIdf.entrySet()) {
				String key = element.getKey();
				if (termIds.containsKey(key)) {
					int vectorId = m_vectorStartId + termIds.get(key);
					resultFeatureVector.put(vectorId, element.getValue());
				}
			}
		}
		if (LOGGING) {
			LOG.info("TfIdsFeatureVector: " + resultFeatureVector);
		}
		return resultFeatureVector;
	}
	
}
