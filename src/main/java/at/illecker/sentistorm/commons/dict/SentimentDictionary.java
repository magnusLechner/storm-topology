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
import at.illecker.sentistorm.commons.util.StringUtils;
import at.illecker.sentistorm.commons.util.io.FileUtils;
import at.illecker.sentistorm.commons.util.io.IOUtils;
import at.illecker.sentistorm.commons.util.io.SerializationUtils;
import at.illecker.sentistorm.commons.wordnet.POSTag;
import at.illecker.sentistorm.commons.wordnet.WordNet;
import cmu.arktweetnlp.Tagger.TaggedToken;
import edu.mit.jwi.item.POS;

public class SentimentDictionary {
	private static final Logger LOG = LoggerFactory.getLogger(SentimentDictionary.class);
	private static final boolean LOGGING = Configuration.get("commons.sentimentdictionary.logging", false);
	private static final SentimentDictionary INSTANCE = new SentimentDictionary();

	private WordNet m_wordnet;
	private List<Map<String, Double>> m_wordLists = null;
	private List<WordListMap<Double>> m_wordListMaps = null;

	private List<String> namesWordlists = new ArrayList<String>();
	private List<String> namesWordlistsWithRegex = new ArrayList<String>();

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private SentimentDictionary() {
		m_wordnet = WordNet.getInstance();
		m_wordLists = new ArrayList<Map<String, Double>>();
		m_wordListMaps = new ArrayList<WordListMap<Double>>();

		List<Map> wordLists = Configuration.getSentimentWordlists();

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
						LOG.info("Deserialize WordListMap from: " + serializationFile);
						m_wordListMaps.add((WordListMap<Double>) SerializationUtils.deserialize(serializationFile));
						namesWordlistsWithRegex.add(name);
					} else {
						LOG.info("Load WordListMap including Regex from: " + file);
						WordListMap<Double> wordListMap = FileUtils.readWordListMap(file, separator, containsPOSTags,
								featureScaling, minValue, maxValue);
						SerializationUtils.serialize(wordListMap, serializationFile);
						m_wordListMaps.add(wordListMap);
						namesWordlistsWithRegex.add(name);
					}
				} else {
					// Try deserialization of file
					String serializationFile = file + ".ser";
					if (IOUtils.exists(serializationFile)) {
						LOG.info("Deserialize WordList from: " + serializationFile);
						m_wordLists.add((Map<String, Double>) SerializationUtils.deserialize(serializationFile));
						namesWordlists.add(name);
					} else {
						LOG.info("Load WordList from: " + file);
						Map<String, Double> wordList = FileUtils.readFile(file, separator, containsPOSTags,
								featureScaling, minValue, maxValue);
						SerializationUtils.serializeMap(wordList, serializationFile);
						m_wordLists.add(wordList);
						namesWordlists.add(name);
					}
				}
			}
		}
	}

	public static SentimentDictionary getInstance() {
		return INSTANCE;
	}

	public void close() {
		m_wordnet.close();
	}

	/**
	 * 
	 * @return Returns the number of word lists used by the sentiment dictionary
	 */
	public int getSentimentWordListCount() {
		return m_wordLists.size() + m_wordListMaps.size();
	}

	public Map<Integer, Double> getWordSentimentsFromLists(String word) {
		Map<Integer, Double> sentimentScores = new HashMap<Integer, Double>();

		word = word.toLowerCase();

		// 1) check wordLists
		for (int i = 0; i < m_wordLists.size(); i++) {
			Double sentimentScore = m_wordLists.get(i).get(word);
			if (sentimentScore != null) {
				sentimentScores.put(i, sentimentScore);
			}
		}

		// 2) check wordListMaps including regex
		int wordListMapOffset = m_wordLists.size();
		for (int i = 0; i < m_wordListMaps.size(); i++) {
			Double sentimentScore = m_wordListMaps.get(i).matchKey(word);
			if (sentimentScore != null) {
				sentimentScores.put(i + wordListMapOffset, sentimentScore);
			}
		}

		if (LOGGING) {
			LOG.info("getWordSentiment('" + word + "'): " + sentimentScores);
		}

		return (sentimentScores.size() > 0) ? sentimentScores : null;
	}

	private Map<Integer, Double> getWordSentiment(String word, String tag, boolean usePTB) {
		// convert tag to POS (NOUN, VERB, ADJECTIVE, ADVERB)
		POS posTag;
		if (usePTB) {
			posTag = POSTag.convertPTB(tag);
		} else {
			posTag = POSTag.convertArk(tag);
		}

		boolean wordIsHashtag = tag.equals("#") || tag.equals("HT");
		boolean wordIsEmoticon = tag.equals("E") || tag.equals("UH");

		// check for Hashtags
		if (wordIsHashtag && (word.length() > 1)) {
			if (word.indexOf('@') == 1) {
				word = word.substring(2); // check for #@ HASHTAG_USER
			} else {
				word = word.substring(1);
			}
		} else if ((!wordIsEmoticon) && StringUtils.consitsOfPunctuations(word)) {
			// ignore all punctuations except emoticons
			return null;
		} else if (StringUtils.consitsOfUnderscores(word)) {
			// ignore tokens with one or more underscores
			return null;
		}

		// if word is not an emoticon then toLowerCase
		if (!wordIsEmoticon) {
			word = word.toLowerCase();
		}

		Map<Integer, Double> sentimentScores = getWordSentimentsFromLists(word);

		// use word stemming if sentimentScore is null
		if (sentimentScores == null) {

			// if word is Twitch-Emoticon but has no score -> no stemming
			// allowed!
			if (TwitchEmoticons.getInstance().isTwitchEmoticon(word)) {
				return null;
			}
			if (LOGGING) {
				LOG.info("findStems for (" + word + "," + posTag + ")");
			}
			List<String> stemmedWords = m_wordnet.findStems(word, posTag);
			for (String stemmedWord : stemmedWords) {
				if (!stemmedWord.equals(word)) {
					sentimentScores = getWordSentimentsFromLists(stemmedWord);
				}
				if (sentimentScores != null) {
					break;
				}
			}
		}

		if (LOGGING) {
			LOG.info("getWordSentimentWithStemming('" + word + "'\'" + posTag + "'): " + sentimentScores);
		}
		return sentimentScores;
	}

	private Map<Integer, Double> getWordSentiment(String word) {

		boolean wordIsEmoticon = StringUtils.isEmoticon(word) || TwitchEmoticons.getInstance().isTwitchEmoticon(word);

		if ((!wordIsEmoticon) && StringUtils.consitsOfPunctuations(word)) {
			// ignore all punctuations except emoticons
			return null;
		} else if (StringUtils.consitsOfUnderscores(word)) {
			// ignore tokens with one or more underscores
			return null;
		}

		Map<Integer, Double> sentimentScores = getWordSentimentsFromLists(word);

		if (LOGGING) {
			LOG.info("getWordSentimentNoPOS('" + word + "'): " + sentimentScores);
		}
		return sentimentScores;
	}

	public Map<Integer, SentimentResult> getSentenceSentiment(List<TaggedToken> sentence) {
		Map<Integer, SentimentResult> sentenceSentiments = new HashMap<Integer, SentimentResult>();
		if (LOGGING) {
			LOG.info("Sentence: " + sentence.toString());
		}

		for (TaggedToken word : sentence) {
			Map<Integer, Double> wordSentiments = getWordSentiment(word.token, word.tag, false);
			if (wordSentiments != null) {
				for (Map.Entry<Integer, Double> wordSentiment : wordSentiments.entrySet()) {

					int key = wordSentiment.getKey();
					double sentimentScore = wordSentiment.getValue();

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

	public Map<Integer, SentimentResult> getSentenceSentimentNotTagged(List<String> sentence) {
		Map<Integer, SentimentResult> sentenceSentiments = new HashMap<Integer, SentimentResult>();
		if (LOGGING) {
			LOG.info("Sentence: " + sentence.toString());
		}

		for (String word : sentence) {
			Map<Integer, Double> wordSentiments = getWordSentiment(word);
			if (wordSentiments != null) {
				for (Map.Entry<Integer, Double> wordSentiment : wordSentiments.entrySet()) {

					int key = wordSentiment.getKey();
					double sentimentScore = wordSentiment.getValue();

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

	// public List<Map<Integer, SentimentResult>>
	// getSentiment(List<List<TaggedToken>> tweets) {
	// List<Map<Integer, SentimentResult>> tweetSentiments = new
	// ArrayList<Map<Integer, SentimentResult>>();
	// for (List<TaggedToken> tweet : tweets) {
	// tweetSentiments.add(getSentenceSentiment(tweet));
	// }
	// return tweetSentiments;
	// }

	public static void main(String[] args) {
		SentimentDictionary sentimentDictionary = SentimentDictionary.getInstance();

		System.out.println(sentimentDictionary.getSentimentWordListCount());

		// with tagging
		// TaggedToken man = new TaggedToken("man", "!");
		// TaggedToken you = new TaggedToken("you", "O");
		// TaggedToken suck = new TaggedToken("suck", "V");
		// TaggedToken sad = new TaggedToken(":(", "E");
		// TaggedToken kappa = new TaggedToken("Kappa", "E");
		// TaggedToken wutface = new TaggedToken("WutFace", "E");
		// TaggedToken head = new TaggedToken("4Head", "E");
		// TaggedToken nonsense = new TaggedToken("S21AD3", "!");
		// TaggedToken dansgame = new TaggedToken("DANSGAME", "E");
		//
		// List<TaggedToken> sentence = new ArrayList<TaggedToken>();

		// no tagging
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

		// Map<Integer, SentimentResult> test =
		// sentimentDictionary.getSentenceSentiment(sentence);
		Map<Integer, SentimentResult> test = sentimentDictionary.getSentenceSentimentNotTagged(sentence);

		System.out.println("#### TEST ####");
		System.out.println(
				"#### REMEMBER: IF A SENTIMENT-DICTIONARY CONTAINS NOT A SINGLE WORD/EMOTICON FROM SENTENCE IT WILL NOT SHOW UP IN RESULT-MAP ####");
		for (Map.Entry<Integer, SentimentResult> tweetSentiment : test.entrySet()) {
			System.out.println("Value: " + tweetSentiment.getValue());
			System.out.println();
		}
	}

}
