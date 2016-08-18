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

import at.illecker.sentistorm.bolt.values.data.JsonBoltData;
import at.illecker.sentistorm.bolt.values.data.TokenizerBoltData;
import at.illecker.sentistorm.components.Tokenizer;

public class TokenizerBolt extends BaseBasicBolt {
	public static final String ID = "tokenizer-bolt";
	public static final String CONF_LOGGING = ID + ".logging";
	private static final long serialVersionUID = -2447717633925641497L;
	private static final Logger LOG = LoggerFactory.getLogger(TokenizerBolt.class);
	private boolean m_logging = false;

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// key of output tuples
		declarer.declare(TokenizerBoltData.getSchema());
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map config, TopologyContext context) {
		// Optional set logging
		if (config.get(CONF_LOGGING) != null) {
			m_logging = (Boolean) config.get(CONF_LOGGING);
		} else {
			m_logging = false;
		}
	}

	@Override
	public void execute(Tuple tuple, BasicOutputCollector collector) {
		JsonBoltData jsonBoltValue = JsonBoltData.getFromTuple(tuple);
		JsonObject jsonObject = jsonBoltValue.getJsonObject();
		String msg = jsonObject.get("msg").getAsString();

		List<String> tokens = Tokenizer.tokenize(msg);

		if (m_logging) {
			LOG.info("Tweet: \"" + msg + "\" Tokenized: " + tokens + "  json: " + jsonObject.toString());
		}
		
		// Emit new tuples
		collector.emit(new TokenizerBoltData(jsonObject, tokens));
	}

}
