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
package at.illecker.sentistorm.commons.dict;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.illecker.sentistorm.commons.Configuration;
import at.illecker.sentistorm.commons.util.io.FileUtils;
import at.illecker.sentistorm.commons.util.io.IOUtils;
import at.illecker.sentistorm.commons.util.io.SerializationUtils;
import cmu.arktweetnlp.Tagger.TaggedToken;

public class EmoticonDictionary {
	private static final Logger LOG = LoggerFactory.getLogger(EmoticonDictionary.class);
	private static final boolean LOGGING = Configuration.get("commons.emoticondictionary.logging", false);
	private static final EmoticonDictionary INSTANCE = new EmoticonDictionary();

	private List<Map<String, Double>> m_emoticonLists = null;
	private List<WordListMap<Double>> m_emoticonListMaps = null;

	private List<String> namesEmoticonLists = new ArrayList<String>();
	private List<String> namesEmoticonListsWithRegex = new ArrayList<String>();
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private EmoticonDictionary() {
		m_emoticonLists = new ArrayList<Map<String, Double>>();
		m_emoticonListMaps = new ArrayList<WordListMap<Double>>();

		List<Map> wordLists = Configuration.getEmoticonLists();
		
		for (Map wordListEntry : wordLists) {
			String file = (String) wordListEntry.get("path");
			String separator = (String) wordListEntry.get("delimiter");
			boolean containsPOSTags = (Boolean) wordListEntry.get("containsPOSTags");
			boolean containsRegex = (Boolean) wordListEntry.get("containsRegex");
			boolean featureScaling = (Boolean) wordListEntry.get("featureScaling");
			double minValue = (Double) wordListEntry.get("minValue");
			double maxValue = (Double) wordListEntry.get("maxValue");
			boolean isEnabled = (Boolean) wordListEntry.get("enabled");
			String name = (String) wordListEntry.get("name");
			if (isEnabled) {
				if (containsRegex) {
					// Try deserialization of file
					String serializationFile = file + ".ser";
					if (IOUtils.exists(serializationFile)) {
						LOG.info("Deserialize EmoticonListMap from: " + serializationFile);
						m_emoticonListMaps.add((WordListMap<Double>) SerializationUtils.deserialize(serializationFile));
						namesEmoticonListsWithRegex.add(name);
					} else {
						LOG.info("Load EmoticonListMap including Regex from: " + file);
						WordListMap<Double> wordListMap = FileUtils.readWordListMap(file, separator, containsPOSTags,
								featureScaling, minValue, maxValue);
						SerializationUtils.serialize(wordListMap, serializationFile);
						m_emoticonListMaps.add(wordListMap);
						namesEmoticonListsWithRegex.add(name);
					}
				} else {
					// Try deserialization of file
					String serializationFile = file + ".ser";
					if (IOUtils.exists(serializationFile)) {
						LOG.info("Deserialize EmoticonList from: " + serializationFile);
						m_emoticonLists.add((Map<String, Double>) SerializationUtils.deserialize(serializationFile));
						namesEmoticonLists.add(name);
					} else {
						LOG.info("Load EmoticonList from: " + file);
						Map<String, Double> wordList = FileUtils.readFile(file, separator, containsPOSTags,
								featureScaling, minValue, maxValue);
						SerializationUtils.serializeMap(wordList, serializationFile);
						m_emoticonLists.add(wordList);
						namesEmoticonLists.add(name);
					}
				}
			}
		}
	}

	public static EmoticonDictionary getInstance() {
		return INSTANCE;
	}

	/**
	 * 
	 * @return Returns the number of emoticon lists used by the emoticon dictionary
	 */
	public int getEmoticonListsCount() {
		return m_emoticonLists.size() + m_emoticonListMaps.size();
	}

	public Map<Integer, Double> getEmoticonSentiments(String token) {
		Map<Integer, Double> sentimentScores = new HashMap<Integer, Double>();

		token = token.toLowerCase();
		
		// 1) check wordLists
		for (int i = 0; i < m_emoticonLists.size(); i++) {
			Double sentimentScore = m_emoticonLists.get(i).get(token);
			if (sentimentScore != null) {
				sentimentScores.put(i, sentimentScore);
			}
		}

		// 2) check emoticonListMaps including regex
		int emoticonListMapOffset = m_emoticonLists.size();
		for (int i = 0; i < m_emoticonListMaps.size(); i++) {
			Double sentimentScore = m_emoticonListMaps.get(i).matchKey(token);
			if (sentimentScore != null) {
				sentimentScores.put(i + emoticonListMapOffset, sentimentScore);
			}
		}

		if (LOGGING) {
			LOG.info("getEmoticonSentiment('" + token + "'): " + sentimentScores);
		}
		
		return (sentimentScores.size() > 0) ? sentimentScores : null;
	}
	
	public Map<Integer, SentimentResult> getSentenceEmoticonSentiment(List<TaggedToken> sentence) {
		Map<Integer, SentimentResult> sentenceSentiments = new HashMap<Integer, SentimentResult>();
		if (LOGGING) {
			LOG.info("Sentence: " + sentence.toString());
		}
		
		for (TaggedToken word : sentence) {
			Map<Integer, Double> emoticonSentiments = getEmoticonSentiments(word.token);
			if (emoticonSentiments != null) {
				for (Map.Entry<Integer, Double> emoticonSentiment : emoticonSentiments.entrySet()) {
					
					int key = emoticonSentiment.getKey();
					double sentimentScore = emoticonSentiment.getValue();

					SentimentResult sentimentResult = sentenceSentiments.get(key);
					if (sentimentResult == null) {
						sentimentResult = new SentimentResult();
					}
					// add score value
					sentimentResult.addScore(sentimentScore);
					// update sentimentResult
					sentenceSentiments.put(key, sentimentResult);
				}
			}
		}
		if (LOGGING) {
			LOG.info("Sentiment: " + sentenceSentiments);
		}
		return (sentenceSentiments.size() > 0) ? sentenceSentiments : null;
	}
	
	public Map<Integer, SentimentResult> getSentenceEmoticonSentimentNotTagged(List<String> sentence) {
		Map<Integer, SentimentResult> sentenceSentiments = new HashMap<Integer, SentimentResult>();
		if (LOGGING) {
			LOG.info("Sentence: " + sentence.toString());
		}
		
		for (String word : sentence) {
			Map<Integer, Double> emoticonSentiments = getEmoticonSentiments(word);
			if (emoticonSentiments != null) {
				for (Map.Entry<Integer, Double> emoticonSentiment : emoticonSentiments.entrySet()) {
					
					int key = emoticonSentiment.getKey();
					double sentimentScore = emoticonSentiment.getValue();

					SentimentResult sentimentResult = sentenceSentiments.get(key);
					if (sentimentResult == null) {
						sentimentResult = new SentimentResult();
					}
					// add score value
					sentimentResult.addScore(sentimentScore);
					// update sentimentResult
					sentenceSentiments.put(key, sentimentResult);
				}
			}
		}
		if (LOGGING) {
			LOG.info("Sentiment: " + sentenceSentiments);
		}
		return (sentenceSentiments.size() > 0) ? sentenceSentiments : null;
	}

	public static void main(String[] args) {
		SentimentDictionary sentimentDictionary = SentimentDictionary.getInstance();

		//with tagging
//		TaggedToken man = new TaggedToken("man", "!");
//		TaggedToken you = new TaggedToken("you", "O");
//		TaggedToken suck = new TaggedToken("suck", "V");
//		TaggedToken sad = new TaggedToken(":(", "E");
//		TaggedToken kappa = new TaggedToken("Kappa", "E");
//		TaggedToken wutface = new TaggedToken("WutFace", "E");
//		TaggedToken head = new TaggedToken("4Head", "E");
//		TaggedToken nonsense = new TaggedToken("S21AD3", "!");
//		TaggedToken dansgame = new TaggedToken("DANSGAME", "E");
//		
//		List<TaggedToken> sentence = new ArrayList<TaggedToken>();
		
		//no tagging
		String man = "man";
		String you = "you";
		String suck = "suck";
		String sad = ":(";
		String kappa = "KAPPA";
		String wutface = "WutFace";
		String head = "4heaD";
		String nonsense = "ASDaA21ASD";
		String dansgame = "DANSGAME";
		
		List<String> sentence = new ArrayList<String>();
		
		sentence.add(man);
		sentence.add(you);
		sentence.add(suck);
		sentence.add(sad);
		sentence.add(kappa);
		sentence.add(wutface);
		sentence.add(head);
		sentence.add(nonsense);
		sentence.add(dansgame);
		
//		Map<Integer, SentimentResult> test = sentimentDictionary.getSentenceSentiment(sentence);
		Map<Integer, SentimentResult> test = sentimentDictionary.getSentenceSentimentNotTagged(sentence);
		
		System.out.println("#### TEST EMOTICON SENTIMENT####");
		for (Map.Entry<Integer, SentimentResult> tweetSentiment : test.entrySet()) {
			System.out.println();
			System.out.println("KEY: " + tweetSentiment.getKey());
			System.out.println("Value: " + tweetSentiment.getValue());
			System.out.println();
		}
	}

}
