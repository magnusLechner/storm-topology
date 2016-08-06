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
package at.illecker.sentistorm.bolt;

import java.util.List;
import java.util.Map;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import at.illecker.sentistorm.bolt.values.data.FeatureGenerationValue;
import at.illecker.sentistorm.bolt.values.data.POSTaggerValue;
import at.illecker.sentistorm.commons.Configuration;
import at.illecker.sentistorm.commons.Dataset;
import at.illecker.sentistorm.commons.FeaturedTweet;
import at.illecker.sentistorm.commons.featurevector.CombinedFeatureVectorGenerator;
import at.illecker.sentistorm.commons.featurevector.FeatureVectorGenerator;
import at.illecker.sentistorm.commons.tfidf.TfIdfNormalization;
import at.illecker.sentistorm.commons.tfidf.TfType;
import at.illecker.sentistorm.commons.tfidf.TweetTfIdf;
import at.illecker.sentistorm.commons.util.io.SerializationUtils;
import cmu.arktweetnlp.Tagger.TaggedToken;

public class FeatureGenerationBolt extends BaseBasicBolt {
	public static final String ID = "feature-generation-bolt";
	public static final String CONF_LOGGING = ID + ".logging";
	private static final long serialVersionUID = 5340637976415982170L;
	private static final Logger LOG = LoggerFactory.getLogger(FeatureGenerationBolt.class);
	private boolean m_logging = false;
	private Dataset m_dataset;
	private FeatureVectorGenerator m_fvg = null;

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// key of output tuples
		declarer.declare(FeatureGenerationValue.getSchema());
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map config, TopologyContext context) {
//		this.m_dataset = Configuration.getDataSetSemEval2013();
		this.m_dataset = Configuration.getDataSetTwitch();

		// Optional set logging
		if (config.get(CONF_LOGGING) != null) {
			m_logging = (Boolean) config.get(CONF_LOGGING);
		} else {
			m_logging = false;
		}

		// TODO serialize CombinedFeatureVectorGenerator
		List<FeaturedTweet> featuredTrainTweets = SerializationUtils
				.deserialize(m_dataset.getTrainDataSerializationFile());
		if (featuredTrainTweets != null) {
			TweetTfIdf tweetTfIdf = TweetTfIdf.createFromTaggedTokens(
					FeaturedTweet.getTaggedTokensFromTweets(featuredTrainTweets), TfType.LOG, TfIdfNormalization.COS,
					true);

			LOG.info("Load CombinedFeatureVectorGenerator...");
			m_fvg = new CombinedFeatureVectorGenerator(true, tweetTfIdf);			
		} else {
			LOG.error("TaggedTweets could not be found! File is missing: " + m_dataset.getTrainDataSerializationFile());
		}
	}

	@Override
	public void execute(Tuple tuple, BasicOutputCollector collector) {
		POSTaggerValue posTaggerValue = POSTaggerValue.getFromTuple(tuple);
		JsonObject jsonObject = posTaggerValue.getJsonObject();
		Object returnInfo = posTaggerValue.getReturnInfo();
		List<TaggedToken> taggedTokens = posTaggerValue.getTaggedTokens();  
		
		// Generate Feature Vector
		Map<Integer, Double> featureVector = m_fvg.generateFeatureVector(taggedTokens);
		
		if (m_logging) {
			LOG.info("Tweet: " + jsonObject.get("msg").getAsString() + " FeatureVector: " + featureVector);
		}

		// Emit new tuples
		collector.emit(new FeatureGenerationValue(jsonObject, returnInfo, featureVector));
	}

}
