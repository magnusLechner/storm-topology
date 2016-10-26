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
package at.storm.commons.featurevector.pos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.storm.commons.Tweet;
import at.storm.commons.tfidf.TfIdfNormalization;
import at.storm.commons.tfidf.TfType;
import at.storm.commons.tfidf.pos.TweetTfIdf;
import at.storm.components.POSTagger;
import at.storm.components.Preprocessor;
import at.storm.components.Tokenizer;
import cmu.arktweetnlp.Tagger.TaggedToken;

public class CombinedFeatureVectorGenerator extends FeatureVectorGenerator {
	private static final Logger LOG = LoggerFactory.getLogger(CombinedFeatureVectorGenerator.class);

	private SentimentFeatureVectorGenerator m_sentimentFeatureVectorGenerator = null;
	private TfIdfFeatureVectorGenerator m_tfidfFeatureVectorGenerator = null;
	private POSFeatureVectorGenerator m_posFeatureVectorGenerator = null;
	private SpecialFeatureVectorGenerator m_booleanFeatureVectorGenerator = null;

	public CombinedFeatureVectorGenerator(boolean normalizePOSCounts, TweetTfIdf tweetTfIdf) {
		m_sentimentFeatureVectorGenerator = new SentimentFeatureVectorGenerator(1);

		m_posFeatureVectorGenerator = new POSFeatureVectorGenerator(normalizePOSCounts,
				m_sentimentFeatureVectorGenerator.getFeatureVectorSize() 
				+ 1);

		m_tfidfFeatureVectorGenerator = new TfIdfFeatureVectorGenerator(tweetTfIdf,
				m_sentimentFeatureVectorGenerator.getFeatureVectorSize()
						+ m_posFeatureVectorGenerator.getFeatureVectorSize() 
						+ 1);

		m_booleanFeatureVectorGenerator = new SpecialFeatureVectorGenerator(
				m_sentimentFeatureVectorGenerator.getFeatureVectorSize()
						+ m_posFeatureVectorGenerator.getFeatureVectorSize()
						+ m_tfidfFeatureVectorGenerator.getFeatureVectorSize() 
						+ 1);

		LOG.info("VectorSize: " + getFeatureVectorSize());
	}

	@Override
	public int getFeatureVectorSize() {
		return m_sentimentFeatureVectorGenerator.getFeatureVectorSize()
				+ m_posFeatureVectorGenerator.getFeatureVectorSize()
				+ m_tfidfFeatureVectorGenerator.getFeatureVectorSize()
				+ m_booleanFeatureVectorGenerator.getFeatureVectorSize();
	}

	@Override
	public Map<Integer, Double> generateFeatureVector(List<TaggedToken> tweet) {
		Map<Integer, Double> featureVector = m_sentimentFeatureVectorGenerator.generateFeatureVector(tweet);

		featureVector.putAll(m_posFeatureVectorGenerator.generateFeatureVector(tweet));

		featureVector.putAll(m_tfidfFeatureVectorGenerator.generateFeatureVector(tweet));

		featureVector.putAll(m_booleanFeatureVectorGenerator.generateFeatureVector(tweet));

		return featureVector;
	}

	public static void main(String[] args) {
		boolean usePOSTags = true; // use POS tags in terms
		Preprocessor preprocessor = Preprocessor.getInstance();
		POSTagger posTagger = POSTagger.getInstance();

		// Load tweets
		List<Tweet> tweets = new ArrayList<Tweet>();
		tweets.add(new Tweet(0L, "<3 <3 <3 <3"));

		// Tokenize
		List<List<String>> tokenizedTweets = Tokenizer.tokenizeTweets(tweets);

		// Preprocess
		long startTime = System.currentTimeMillis();
		List<List<String>> preprocessedTweets = preprocessor.preprocessTweets(tokenizedTweets);
		LOG.info("Preprocess finished after " + (System.currentTimeMillis() - startTime) + " ms");

		// POS Tagging
		startTime = System.currentTimeMillis();
		List<List<TaggedToken>> taggedTweets = posTagger.tagTweets(preprocessedTweets);
		LOG.info("POS Tagger finished after " + (System.currentTimeMillis() - startTime) + " ms");

		// Generate CombinedFeatureVectorGenerator
		TweetTfIdf tweetTfIdf = TweetTfIdf.createFromTaggedTokens(taggedTweets, TfType.LOG, TfIdfNormalization.COS,
				usePOSTags);
		CombinedFeatureVectorGenerator cfvg = new CombinedFeatureVectorGenerator(true, tweetTfIdf);

		// Combined Feature Vector Generation
		for (List<TaggedToken> taggedTokens : taggedTweets) {
			Map<Integer, Double> combinedFeatureVector = cfvg.generateFeatureVector(taggedTokens);

			// Generate feature vector string
			String featureVectorStr = "";
			for (Map.Entry<Integer, Double> feature : combinedFeatureVector.entrySet()) {
				featureVectorStr += " " + feature.getKey() + ":" + feature.getValue();
			}
			LOG.info("Tweet: '" + taggedTokens + "'");
			LOG.info("CombinedFeatureVector: " + featureVectorStr);
		}
	}

}
