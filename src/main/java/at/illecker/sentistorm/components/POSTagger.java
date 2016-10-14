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
package at.illecker.sentistorm.components;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.illecker.sentistorm.commons.Configuration;
import at.illecker.sentistorm.commons.Tweet;
import at.illecker.sentistorm.commons.dict.TwitchEmoticons;
import at.illecker.sentistorm.commons.util.io.IOUtils;
import at.illecker.sentistorm.commons.util.io.SerializationUtils;
import cmu.arktweetnlp.Tagger.TaggedToken;
import cmu.arktweetnlp.impl.Model;
import cmu.arktweetnlp.impl.ModelSentence;
import cmu.arktweetnlp.impl.Sentence;
import cmu.arktweetnlp.impl.features.FeatureExtractor;

public class POSTagger {
	private static final Logger LOG = LoggerFactory.getLogger(POSTagger.class);
	private static final POSTagger INSTANCE = new POSTagger();
	String m_taggingModel;
	private Model m_model;
	private FeatureExtractor m_featureExtractor;

	private POSTagger() {
		// Load POS Tagger
		try {
			m_taggingModel = Configuration.get("global.resources.postagger.model.path");
			if ((Configuration.RUNNING_WITHIN_JAR) && (!m_taggingModel.startsWith("/"))) {
				m_taggingModel = "/" + m_taggingModel;
			}
			if (IOUtils.exists(m_taggingModel)) {
				LOG.info("Load POS Tagger with model: " + m_taggingModel);
				m_model = Model.loadModelFromText(m_taggingModel);
				m_featureExtractor = new FeatureExtractor(m_model, false);
			} else {
				LOG.info("Load POS Tagger model: " + m_taggingModel + "_model.ser");
				m_model = SerializationUtils.deserialize(m_taggingModel + "_model.ser");
				LOG.info("Load POS Tagger featureExtractor : " + m_taggingModel + "_featureExtractor.ser");
				m_featureExtractor = SerializationUtils.deserialize(m_taggingModel + "_featureExtractor.ser");
			}
		} catch (IOException e) {
			LOG.error("IOException: " + e.getMessage());
		}
	}

	public static POSTagger getInstance() {
		return INSTANCE;
	}

	public List<List<TaggedToken>> tagTweets(List<List<String>> tweets) {
		List<List<TaggedToken>> taggedTweets = new ArrayList<List<TaggedToken>>();
		for (List<String> tweet : tweets) {
			taggedTweets.add(tag(tweet));
		}
		return taggedTweets;
	}

	public List<TaggedToken> tag(List<String> tokens) {
		List<TaggedToken> taggedTokens = new ArrayList<TaggedToken>();
		if(tokens.size() == 0) {
			return taggedTokens;
		}
		
		List<String> nonTwitchEmoticon = new ArrayList<String>();
		for(String token : tokens) {
			if(!TwitchEmoticons.getInstance().isTwitchEmoticon(token)) {
				nonTwitchEmoticon.add(token);
			}
		}
		boolean onlyTwitchEmoticons = false;
		if(nonTwitchEmoticon.size() == 0) {
			onlyTwitchEmoticons = true;
		}
		
		if(!onlyTwitchEmoticons) {
			Sentence sentence = new Sentence();
			sentence.tokens = nonTwitchEmoticon;
//			sentence.tokens = tokens;
			ModelSentence ms = new ModelSentence(sentence.T());
			
			m_featureExtractor.computeFeatures(sentence, ms);
			m_model.greedyDecode(ms, false);
			
			int counter = 0;
			for(int t = 0; t < tokens.size(); t++) {
				TaggedToken tt = null;
				String token = tokens.get(t);
				if(TwitchEmoticons.getInstance().isTwitchEmoticon(token)) {
					tt = new TaggedToken(token, "E");
				} else {
					tt = new TaggedToken(token, m_model.labelVocab.name(ms.labels[counter]));
					counter++;
				}
				taggedTokens.add(tt);
			}
		} else {
			for(int t = 0; t < tokens.size(); t++) {
				TaggedToken tt = null;
				String token = tokens.get(t);
				if(TwitchEmoticons.getInstance().isTwitchEmoticon(token)) {
					tt = new TaggedToken(token, "E");
				}
				taggedTokens.add(tt);
			}
		}

//		//original
//		for (int t = 0; t < sentence.T(); t++) {
//			TaggedToken tt = null;
//			String token = tokens.get(t);
//			if(TwitchEmoticons.getInstance().isTwitchEmoticon(token)) {
//				tt = new TaggedToken(token, "E");
//			} else {
//				tt = new TaggedToken(token, m_model.labelVocab.name(ms.labels[t]));
//			}
//			taggedTokens.add(tt);
//		}
		
		return taggedTokens;
	}

	public void serializeModel() {
		SerializationUtils.serialize(m_model, m_taggingModel + "_model.ser");
	}

	public void serializeFeatureExtractor() {
		SerializationUtils.serialize(m_featureExtractor, m_taggingModel + "_featureExtractor.ser");
	}

	public static void main(String[] args) {
		boolean useSerialization = false;
		Preprocessor preprocessor = Preprocessor.getInstance();
		POSTagger posTagger = POSTagger.getInstance();
		
		if (useSerialization) {
			posTagger.serializeModel();
			posTagger.serializeFeatureExtractor();
		}
		
		// load tweets
//		List<Tweet> tweets = Tweet.getTestTweets();
//		List<Tweet> tweets = Tweet.getSigleTestTweet();
		Tweet testTweet1 = new Tweet(0L, "man Kappa you :( suck Kappa WutFace 4Head");
//		Tweet testTweet2 = new Tweet(0L, "I book a WutFace flight and read a book.");
//		Tweet testTweet3 = new Tweet(0L, "The Duchess was entertaining last night.");
//		Tweet testTweet3 = new Tweet(0L, " ");
//		Tweet testTweet4 = new Tweet(0L, "i come from the future.");
//		Tweet testTweet4 = new Tweet(0L, "");
		
		List<Tweet> tweets = new ArrayList<Tweet>();
		tweets.add(testTweet1);
//		tweets.add(testTweet2);
//		tweets.add(testTweet3);
//		tweets.add(testTweet4);

		// process tweets
		long startTime = System.currentTimeMillis();
		for (Tweet tweet : tweets) {
			// Tokenize
			List<String> tokens = Tokenizer.tokenize(tweet.getText());

//			for(int i = 0; i < tokens.size(); i++) {
//				System.out.println("TOKENS: " + tokens.get(i));
//			}
			
			// Preprocess
			List<String> preprocessedTokens = preprocessor.preprocess(tokens);

//			for(int i = 0; i < preprocessedTokens.size(); i++) {
//				System.out.println("PREPROCESSED: " + preprocessedTokens.get(i));
//			}
			
			// POS Tagging
			List<TaggedToken> taggedTokens = posTagger.tag(preprocessedTokens);

			LOG.info("Tweet: '" + tweet + "'");
			LOG.info("TaggedTweet: " + taggedTokens);
		}
		long elapsedTime = System.currentTimeMillis() - startTime;
		LOG.info("POSTagger finished after " + elapsedTime + " ms");
		LOG.info("Total tweets: " + tweets.size());
		LOG.info((elapsedTime / (double) tweets.size()) + " ms per Tweet");
	}

}
