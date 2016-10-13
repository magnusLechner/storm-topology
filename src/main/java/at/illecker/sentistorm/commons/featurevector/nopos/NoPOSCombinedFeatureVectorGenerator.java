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
package at.illecker.sentistorm.commons.featurevector.nopos;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.illecker.sentistorm.commons.Configuration;
import at.illecker.sentistorm.commons.Tweet;
import at.illecker.sentistorm.commons.tfidf.TfIdfNormalization;
import at.illecker.sentistorm.commons.tfidf.TfType;
import at.illecker.sentistorm.commons.tfidf.ngram.MessageNGrams;
import at.illecker.sentistorm.commons.tfidf.nopos.NoPOSTweetTfIdf;
import at.illecker.sentistorm.components.Preprocessor;
import at.illecker.sentistorm.components.Tokenizer;

public class NoPOSCombinedFeatureVectorGenerator extends NoPOSFeatureVectorGenerator {
	private static final Logger LOG = LoggerFactory.getLogger(NoPOSCombinedFeatureVectorGenerator.class);

	private NoPOSSentimentFeatureVectorGenerator m_sentimentFeatureVectorGenerator = null;
	private NoPOSTfIdfFeatureVectorGenerator m_tfidfNoPOSFVG = null;
	private NoPOSSpecialFeatureVectorGenerator m_booleanNoPosFVG = null;
	private NoPOSNGramFeatureVectorGenerator m_nGramNoPosFVG = null;

	public NoPOSCombinedFeatureVectorGenerator(NoPOSTweetTfIdf tweetTfIdfNoPOS, MessageNGrams messageNGrams) {
		m_sentimentFeatureVectorGenerator = new NoPOSSentimentFeatureVectorGenerator(1);

		m_tfidfNoPOSFVG = new NoPOSTfIdfFeatureVectorGenerator(tweetTfIdfNoPOS,
				m_sentimentFeatureVectorGenerator.getFeatureVectorSize() + 1);

		m_booleanNoPosFVG = new NoPOSSpecialFeatureVectorGenerator(
				m_sentimentFeatureVectorGenerator.getFeatureVectorSize() + m_tfidfNoPOSFVG.getFeatureVectorSize() + 1);

//		m_nGramNoPosFVG = new NoPOSNGramFeatureVectorGenerator(messageNGrams,
//				m_sentimentFeatureVectorGenerator.getFeatureVectorSize() + m_tfidfNoPOSFVG.getFeatureVectorSize()
//						+ m_booleanNoPosFVG.getFeatureVectorSize() + 1);
	}

	@Override
	public int getFeatureVectorSize() {
		return m_sentimentFeatureVectorGenerator.getFeatureVectorSize() + m_tfidfNoPOSFVG.getFeatureVectorSize()
				+ m_booleanNoPosFVG.getFeatureVectorSize()
//				+ m_nGramNoPosFVG.getFeatureVectorSize()
				;
	}

	@Override
	public Map<Integer, Double> generateFeatureVector(List<String> tweet) {
		Map<Integer, Double> featureVector = m_sentimentFeatureVectorGenerator.generateFeatureVector(tweet);

		featureVector.putAll(m_tfidfNoPOSFVG.generateFeatureVector(tweet));

		featureVector.putAll(m_booleanNoPosFVG.generateFeatureVector(tweet));

//		featureVector.putAll(m_nGramNoPosFVG.generateFeatureVector(tweet));

		return featureVector;
	}

	public static void main(String[] args) {
		Preprocessor preprocessor = Preprocessor.getInstance();

		// Load tweets
		// List<Tweet> tweets =
		// Configuration.getDataSetTwitch().getTrainTweets(true);
		List<Tweet> tweets = Configuration.getDataSetMyTest().getTrainTweets(true);

		// Tokenize
		List<List<String>> tokenizedTweets = Tokenizer.tokenizeTweets(tweets);

		// Preprocess
		long startTime = System.currentTimeMillis();
		List<List<String>> preprocessedTweets = preprocessor.preprocessTweets(tokenizedTweets);
		LOG.info("Preprocess finished after " + (System.currentTimeMillis() - startTime) + " ms");

		// Generate CombinedFeatureVectorGenerator
		NoPOSTweetTfIdf noPOSTweetTfIdf = NoPOSTweetTfIdf.createFromPreprocessedTokens(preprocessedTweets, TfType.LOG,
				TfIdfNormalization.COS);
		MessageNGrams nGrams = MessageNGrams.createFromTokens(preprocessedTweets, TfType.LOG, TfIdfNormalization.COS);
		NoPOSCombinedFeatureVectorGenerator cfvg = new NoPOSCombinedFeatureVectorGenerator(noPOSTweetTfIdf, nGrams);

		// Combined Feature Vector Generation
		for (List<String> tokens : preprocessedTweets) {
			Map<Integer, Double> combinedFeatureVector = cfvg.generateFeatureVector(tokens);

			// Generate feature vector string
			String featureVectorStr = "";
			for (Map.Entry<Integer, Double> feature : combinedFeatureVector.entrySet()) {
				featureVectorStr += " " + feature.getKey() + ":" + feature.getValue();
			}
			LOG.info("Tweet: '" + tokens + "'");
			LOG.info("NoPOSCombinedFeatureVector: " + featureVectorStr);
		}
	}

}
