package at.illecker.sentistorm.commons.featurevector.nopos;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.illecker.sentistorm.commons.Configuration;

public class NoPOSBooleanFeatureVectorGenerator extends NoPOSFeatureVectorGenerator {
	private static final Logger LOG = LoggerFactory.getLogger(NoPOSBooleanFeatureVectorGenerator.class);
	private static final boolean LOGGING = Configuration.get("commons.featurevectorgenerator.boolean.logging", false);
	private final int VECTOR_SIZE = 2;
	private int m_vectorStartId;

	public NoPOSBooleanFeatureVectorGenerator() {
		m_vectorStartId = 1;
		LOG.info("VectorSize: " + VECTOR_SIZE);
	}

	public NoPOSBooleanFeatureVectorGenerator(int vectorStartId) {
		this.m_vectorStartId = vectorStartId;
		LOG.info("VectorSize: " + VECTOR_SIZE);
	}

	@Override
	public int getFeatureVectorSize() {
		return VECTOR_SIZE;
	}

	@Override
	public Map<Integer, Double> generateFeatureVector(List<String> tokens) {
		Map<Integer, Double> resultFeatureVector = new TreeMap<Integer, Double>();
		boolean containsKappa = false;
		boolean lastTokenIsNot = false;
		for (int i = 0; i < tokens.size(); i++) {
			if (tokens.get(i).toLowerCase().equals("kappa")) {
				containsKappa = true;
			}
			if (i == tokens.size() - 1) {
				if (tokens.get(i).toLowerCase().equals("not")) {
					lastTokenIsNot = true;
				}
			}
		}
		if (containsKappa) {
			resultFeatureVector.put(m_vectorStartId, 1.0);
		}
		if (lastTokenIsNot) {
			resultFeatureVector.put(m_vectorStartId + 1, 1.0);
		}
		if (LOGGING) {
			LOG.info("booleans: containsKappa: " + containsKappa + "   LastIsNot: " + lastTokenIsNot);
		}
		return resultFeatureVector;
	}

}
