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
package at.storm.bolt;

import java.util.Map;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import at.storm.bolt.values.data.JsonBoltData;
import at.storm.bolt.values.statistic.tuple.TupleStatistic;
import at.storm.commons.LanguageDetection;

public class JsonBolt extends BaseBasicBolt {
	public static final String ID = "json-bolt";
	public static final String CONF_LOGGING = ID + ".logging";
	private static final long serialVersionUID = -8847712312925641497L;
	private static final Logger LOG = LoggerFactory.getLogger(JsonBolt.class);

	public static final String PIPELINE_STREAM = "pipeline-stream";
	public static final String TO_REDIS_PUBLISH_STREAM = "not-english-stream";
	
	private boolean m_logging = false;
	private JsonParser jsonParser;

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declareStream(PIPELINE_STREAM, JsonBoltData.getSchema());
		declarer.declareStream(TO_REDIS_PUBLISH_STREAM, JsonBoltData.getSchema());
	}

	@SuppressWarnings("rawtypes")
	public void prepare(Map config, TopologyContext context) {
		// Optional set logging
		if (config.get(CONF_LOGGING) != null) {
			m_logging = (Boolean) config.get(CONF_LOGGING);
		} else {
			m_logging = false;
		}

		jsonParser = new JsonParser();
		
	}

	public void execute(Tuple tuple, BasicOutputCollector collector) {
		String jsonString = tuple.getStringByField("jsonString");
		TupleStatistic tupleStatistic = (TupleStatistic) tuple.getValueByField("tupleStatistic");

		JsonObject jsonObject = (JsonObject) jsonParser.parse(jsonString);

		if (m_logging) {
			LOG.info("JSON: " + jsonObject.toString());
		}
		
		tupleStatistic.setRealStart(jsonObject.get("timestamp").getAsLong());
		
		String message = jsonObject.get("msg").getAsString();
		
		if(!LanguageDetection.isEnglish(message)) {
			JsonObject score = new JsonObject();
			score.addProperty("score", 0);
			
			jsonObject.add("sentiment", score);
			
			collector.emit(TO_REDIS_PUBLISH_STREAM, new JsonBoltData(jsonObject, tupleStatistic));
		} else {
			collector.emit(PIPELINE_STREAM, new JsonBoltData(jsonObject, tupleStatistic));	
		}
		
	}

}
