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
import at.storm.commons.dict.SentimentResult;

public class NoPOSSentimentFeatureVectorGenerator extends NoPOSFeatureVectorGenerator {
	private static final Logger LOG = LoggerFactory.getLogger(NoPOSSentimentFeatureVectorGenerator.class);
	private static final boolean LOGGING = Configuration.get("commons.featurevectorgenerator.sentiment.logging", false);
	private static final int VECTOR_SIZE = 7;
	private SentimentDictionary m_sentimentDict;
	private int m_vectorStartId;

	public NoPOSSentimentFeatureVectorGenerator() {
		this.m_sentimentDict = SentimentDictionary.getInstance();
		m_vectorStartId = 1;
		LOG.info("VectorSize: " + getFeatureVectorSize());
	}

	// TODO
	// public NoPOSSentimentFeatureVectorGenerator(int vectorStartId) {
	// this();
	// this.setVectorStartId(vectorStartId);
	// }

	public NoPOSSentimentFeatureVectorGenerator(int vectorStartId) {
		this();
		this.m_vectorStartId = vectorStartId;
	}

	public SentimentDictionary getSentimentDictionary() {
		return m_sentimentDict;
	}

	@Override
	public int getFeatureVectorSize() {
		// VECTOR_SIZE = {PosCount, NeutralCount, NegCount, Sum, Count, MaxPos,
		// MaxNeg}
		return VECTOR_SIZE * m_sentimentDict.getSentimentWordListCount();
	}

	@Override
	public Map<Integer, Double> generateFeatureVector(List<String> preprocessedTokens) {
		Map<Integer, SentimentResult> tweetSentiments = m_sentimentDict
				.getSentenceSentimentNotTagged(preprocessedTokens);
		return generateFeatureVector(tweetSentiments);
	}

	private Map<Integer, Double> generateFeatureVector(Map<Integer, SentimentResult> tweetSentiments) {
		Map<Integer, Double> featureVector = new TreeMap<Integer, Double>();

		if (tweetSentiments != null) {
			for (Map.Entry<Integer, SentimentResult> tweetSentiment : tweetSentiments.entrySet()) {

				int key = tweetSentiment.getKey();
				SentimentResult sentimentResult = tweetSentiment.getValue();
				// LOG.info("TweetSentiment: " + sentimentResult);

				if (sentimentResult.getPosCount() != 0) {
					featureVector.put(m_vectorStartId + (key * VECTOR_SIZE), (double) sentimentResult.getPosCount());
				}
				if (sentimentResult.getNeutralCount() != 0) {
					featureVector.put(m_vectorStartId + (key * VECTOR_SIZE) + 1,
							(double) sentimentResult.getNeutralCount());
				}
				if (sentimentResult.getNegCount() != 0) {
					featureVector.put(m_vectorStartId + (key * VECTOR_SIZE) + 2,
							(double) sentimentResult.getNegCount());
				}
				if (sentimentResult.getSum() != 0) {
					featureVector.put(m_vectorStartId + (key * VECTOR_SIZE) + 3, sentimentResult.getSum());
				}
				if (sentimentResult.getCount() != 0) {
					featureVector.put(m_vectorStartId + (key * VECTOR_SIZE) + 4, (double) sentimentResult.getCount());
				}
				if (sentimentResult.getMaxPos() != null) {
					featureVector.put(m_vectorStartId + (key * VECTOR_SIZE) + 5, sentimentResult.getMaxPos());
				}
				if (sentimentResult.getMaxNeg() != null) {
					featureVector.put(m_vectorStartId + (key * VECTOR_SIZE) + 6, sentimentResult.getMaxNeg());
				}

				if (LOGGING) {
					LOG.info("TweetSentiment: " + sentimentResult);
				}
			}
		}
		return featureVector;
	}

}
