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
package at.illecker.sentistorm.commons.featurevector;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.illecker.sentistorm.commons.Configuration;
import at.illecker.sentistorm.commons.dict.TwitchEmoticons;
import at.illecker.sentistorm.commons.util.io.FileUtils;
import at.illecker.sentistorm.commons.util.io.IOUtils;
import at.illecker.sentistorm.commons.util.io.SerializationUtils;
import cmu.arktweetnlp.Tagger.TaggedToken;

public class UndefinedTwitchFeatureVectorGenerator extends FeatureVectorGenerator {
	private static final Logger LOG = LoggerFactory.getLogger(UndefinedTwitchFeatureVectorGenerator.class);
	private static final int VECTOR_SIZE = 1;
	private int m_vectorStartId = 1;

	private Map<String, Double> emoticonsWithSentiment;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public UndefinedTwitchFeatureVectorGenerator() {
		List<Map> wordLists = Configuration.getSentimentWordlists();
		for (Map wordListEntry : wordLists) {
			String name = (String) wordListEntry.get("name");
			if (name.equals("twitch_emoticons")) {
				String file = (String) wordListEntry.get("path");
				String separator = (String) wordListEntry.get("delimiter");
				boolean containsPOSTags = (Boolean) wordListEntry.get("containsPOSTags");
				boolean featureScaling = (Boolean) wordListEntry.get("featureScaling");
				double minValue = (Double) wordListEntry.get("minValue");
				double maxValue = (Double) wordListEntry.get("maxValue");

				// Try deserialization of file
				String serializationFile = file + ".ser";
				if (IOUtils.exists(serializationFile)) {
					LOG.info("Deserialize WordList from: " + serializationFile);
					emoticonsWithSentiment = (Map<String, Double>) SerializationUtils.deserialize(serializationFile);
				} else {
					LOG.info("Load WordList from: " + file);
					emoticonsWithSentiment = FileUtils.readFile(file, separator, containsPOSTags, featureScaling,
							minValue, maxValue);
					SerializationUtils.serializeMap(emoticonsWithSentiment, serializationFile);
				}
			}
		}

		LOG.info("VectorSize: " + getFeatureVectorSize());
	}

	public UndefinedTwitchFeatureVectorGenerator(int vectorStartId) {
		this();
		this.m_vectorStartId = vectorStartId;
	}

	@Override
	public int getFeatureVectorSize() {
		return VECTOR_SIZE;
	}

	@Override
	public Map<Integer, Double> generateFeatureVector(List<TaggedToken> taggedTokens) {
		Map<Integer, Double> featureVector = new TreeMap<Integer, Double>();

		Double countUndefinedTwitchEmoticon = 0.0;
		for (TaggedToken taggedToken : taggedTokens) {
			String token = taggedToken.token;
			if (TwitchEmoticons.getInstance().isTwitchEmoticon(token) && !emoticonsWithSentiment.containsKey(token)) {
				countUndefinedTwitchEmoticon += 1.0;
			}
		}
		if (countUndefinedTwitchEmoticon != 0.0) {
			featureVector.put(m_vectorStartId + VECTOR_SIZE, countUndefinedTwitchEmoticon);
		}

		return featureVector;
	}

}
