/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package at.storm.commons.featurevector.nopos;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.storm.commons.Configuration;
import at.storm.commons.dict.SentimentDictionary;
import at.storm.commons.tfidf.nopos.NoPOSTweetTfIdf;

public class NoPOSTfIdfFeatureVectorGenerator extends NoPOSFeatureVectorGenerator {
	private static final Logger LOG = LoggerFactory.getLogger(NoPOSTfIdfFeatureVectorGenerator.class);
	private static final boolean LOGGING = Configuration.get("commons.featurevectorgenerator.tfidf.logging", false);

	private NoPOSTweetTfIdf m_noPOSTweetTfIdf = null;
	private SentimentDictionary m_sentimentDict;
	private int m_vectorStartId = 1;

	public NoPOSTfIdfFeatureVectorGenerator(NoPOSTweetTfIdf noPOSTweetTfIdf) {
		this.m_noPOSTweetTfIdf = noPOSTweetTfIdf;
		this.m_sentimentDict = SentimentDictionary.getInstance();
		LOG.info("VectorSize: " + getFeatureVectorSize());
	}

	public NoPOSTfIdfFeatureVectorGenerator(NoPOSTweetTfIdf noPOSTweetTfIdf, int vectorStartId) {
		this(noPOSTweetTfIdf);
		this.m_vectorStartId = vectorStartId;
	}

	public SentimentDictionary getSentimentDictionary() {
		return m_sentimentDict;
	}

	@Override
	public int getFeatureVectorSize() {
		return m_noPOSTweetTfIdf.getInverseDocFreq().size();
	}

	@Override
	public Map<Integer, Double> generateFeatureVector(List<String> preprocessedTweet) {
		return generateFeatureVector(m_noPOSTweetTfIdf.tfIdfFromPreprocessedTokens(preprocessedTweet));
	}

	public Map<Integer, Double> generateFeatureVector(Map<String, Double> tfIdf) {
		Map<Integer, Double> resultFeatureVector = new TreeMap<Integer, Double>();

		if (m_noPOSTweetTfIdf != null) {
			// Map<String, Double> idf = m_tweetTfIdf.getInverseDocFreq();
			Map<String, Integer> termIds = m_noPOSTweetTfIdf.getTermIds();

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
