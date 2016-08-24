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
package at.illecker.sentistorm.commons.tfidf.nopos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.illecker.sentistorm.commons.dict.StopWords;
import at.illecker.sentistorm.commons.tfidf.TfIdf;
import at.illecker.sentistorm.commons.tfidf.TfIdfNormalization;
import at.illecker.sentistorm.commons.tfidf.TfType;
import at.illecker.sentistorm.commons.tfidf.TweetTfIdf;
import at.illecker.sentistorm.commons.util.StringUtils;

/**
 * Tweet Term Frequency - Inverse Document Frequency
 * 
 */
public class NoPOSTweetTfIdf {
	private static final Logger LOG = LoggerFactory.getLogger(TweetTfIdf.class);

	private TfType m_tfType;
	private TfIdfNormalization m_tfIdfNormalization;
	private List<Map<String, Double>> m_termFreqs;
	private Map<String, Double> m_inverseDocFreq;
	private Map<String, Integer> m_termIds;
	private boolean m_usePOSTags;

	private NoPOSTweetTfIdf(TfType type, TfIdfNormalization normalization, boolean usePOSTags) {
		this.m_tfType = type;
		this.m_tfIdfNormalization = normalization;
		this.m_usePOSTags = usePOSTags;
	}

	public TfType getTfType() {
		return m_tfType;
	}

	public TfIdfNormalization getTfIdfNormalization() {
		return m_tfIdfNormalization;
	}

	public List<Map<String, Double>> getTermFreqs() {
		return m_termFreqs;
	}

	public Map<String, Double> getInverseDocFreq() {
		return m_inverseDocFreq;
	}

	public Map<String, Integer> getTermIds() {
		return m_termIds;
	}

	public Map<String, Double> tfIdfFromTaggedTokens(List<String> preprocessedTweet) {
		return TfIdf.tfIdf(tfFromTaggedTokens(preprocessedTweet, m_tfType, m_usePOSTags), m_inverseDocFreq, m_tfIdfNormalization);
	}

	public static NoPOSTweetTfIdf createFromTaggedTokens(List<List<String>> preprocessedTweets, boolean usePOSTags) {
		return createFromTaggedTokens(preprocessedTweets, TfType.RAW, TfIdfNormalization.NONE, usePOSTags);
	}

	public static NoPOSTweetTfIdf createFromTaggedTokens(List<List<String>> preprocessedTweets, TfType type,
			TfIdfNormalization normalization, boolean usePOSTags) {

		NoPOSTweetTfIdf tweetTfIdfNoPOS = new NoPOSTweetTfIdf(type, normalization, usePOSTags);

		tweetTfIdfNoPOS.m_termFreqs = tfTaggedTokenTweets(preprocessedTweets, type, usePOSTags);
		tweetTfIdfNoPOS.m_inverseDocFreq = idf(tweetTfIdfNoPOS.m_termFreqs);

		tweetTfIdfNoPOS.m_termIds = new HashMap<String, Integer>();
		int i = 0;
		for (String key : tweetTfIdfNoPOS.m_inverseDocFreq.keySet()) {
			tweetTfIdfNoPOS.m_termIds.put(key, i);
			i++;
		}

		LOG.info("Found " + tweetTfIdfNoPOS.m_inverseDocFreq.size() + " terms");
		// Debug
		// print("Term Frequency", m_termFreqs, m_inverseDocFreq);
		// print("Inverse Document Frequency", m_inverseDocFreq);
		return tweetTfIdfNoPOS;
	}

	public static List<Map<String, Double>> tfTaggedTokenTweets(List<List<String>> preprocessedTweets, TfType type,
			boolean usePOSTags) {
		List<Map<String, Double>> termFreqs = new ArrayList<Map<String, Double>>();
		for (List<String> tweet : preprocessedTweets) {
			termFreqs.add(tfFromTaggedTokens(tweet, type, usePOSTags));
		}
		return termFreqs;
	}

	public static Map<String, Double> tfFromTaggedTokens(List<String> preprocessedTweet, TfType type, boolean usePOSTags) {
		Map<String, Double> termFreq = new LinkedHashMap<String, Double>();
		StopWords stopWords = StopWords.getInstance();

		List<String> words = new ArrayList<String>();
		for (String taggedToken : preprocessedTweet) {
			String word = taggedToken.toLowerCase();

			if (!stopWords.isStopWord(word)) {

				// Check if word starts with an alphabet
				if (!StringUtils.startsWithAlphabeticChar(word)) {
					continue;
				}
				words.add(word);
			}
		}
		termFreq = TfIdf.tf(termFreq, words);
		termFreq = TfIdf.normalizeTf(termFreq, type);
		return termFreq;
	}

	public static Map<String, Double> idf(List<Map<String, Double>> termFreq) {
		return TfIdf.idf(termFreq);
	}

	public static List<Map<String, Double>> tfIdf(List<Map<String, Double>> termFreqs,
			Map<String, Double> inverseDocFreq, TfIdfNormalization normalization) {

		List<Map<String, Double>> tfIdf = new ArrayList<Map<String, Double>>();
		// compute tfIdf for each document
		for (Map<String, Double> doc : termFreqs) {
			tfIdf.add(TfIdf.tfIdf(doc, inverseDocFreq, normalization));
		}

		return tfIdf;
	}

}
