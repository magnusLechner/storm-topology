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
package at.storm.commons.tfidf.nopos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.storm.commons.dict.StopWords;
import at.storm.commons.tfidf.TfIdf;
import at.storm.commons.tfidf.TfIdfNormalization;
import at.storm.commons.tfidf.TfType;
import at.storm.commons.util.StringUtils;

/**
 * Tweet Term Frequency - Inverse Document Frequency
 * 
 */
public class NoPOSTweetTfIdf {
	private static final Logger LOG = LoggerFactory.getLogger(NoPOSTweetTfIdf.class);

	private TfType m_tfType;
	private TfIdfNormalization m_tfIdfNormalization;
	private List<Map<String, Double>> m_termFreqs;
	private Map<String, Double> m_inverseDocFreq;
	private Map<String, Integer> m_termIds;

	private NoPOSTweetTfIdf(TfType type, TfIdfNormalization normalization) {
		this.m_tfType = type;
		this.m_tfIdfNormalization = normalization;
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

	public Map<String, Double> tfIdfFromPreprocessedTokens(List<String> preprocessedTweet) {
		return TfIdf.tfIdf(tfFromPreprocessedTokens(preprocessedTweet, m_tfType), m_inverseDocFreq, m_tfIdfNormalization);
	}

	public static NoPOSTweetTfIdf createFromPreprocessedTokens(List<List<String>> preprocessedTweets) {
		return createFromPreprocessedTokens(preprocessedTweets, TfType.LOG, TfIdfNormalization.COS);
	}

	public static NoPOSTweetTfIdf createFromPreprocessedTokens(List<List<String>> preprocessedTweets, TfType type,
			TfIdfNormalization normalization) {

		NoPOSTweetTfIdf tweetTfIdfNoPOS = new NoPOSTweetTfIdf(type, normalization);

		tweetTfIdfNoPOS.m_termFreqs = tfPreprocessedTokenTweets(preprocessedTweets, type);
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

	public static List<Map<String, Double>> tfPreprocessedTokenTweets(List<List<String>> preprocessedTweets, TfType type) {
		List<Map<String, Double>> termFreqs = new ArrayList<Map<String, Double>>();
		for (List<String> tweet : preprocessedTweets) {
			termFreqs.add(tfFromPreprocessedTokens(tweet, type));
		}
		return termFreqs;
	}

	public static Map<String, Double> tfFromPreprocessedTokens(List<String> preprocessedTweet, TfType type) {
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
