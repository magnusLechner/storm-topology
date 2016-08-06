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

import java.util.Calendar;
import java.util.Map;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import at.illecker.sentistorm.bolt.values.data.JsonValue;

public class JSONBolt extends BaseBasicBolt {
	public static final String ID = "json-bolt";
	public static final String CONF_LOGGING = ID + ".logging";
	private static final long serialVersionUID = -8847712312925641497L;
	private static final Logger LOG = LoggerFactory.getLogger(JSONBolt.class);

	public static final String PIPELINE_STREAM = "pipeline-stream";
	public static final String START_STATISTIC_STREAM = "start-statistic-stream";

	private boolean m_logging = false;
	private JsonParser jsonParser;

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// key of output tuples
		declarer.declareStream(PIPELINE_STREAM, JsonValue.getSchema());
		declarer.declareStream(START_STATISTIC_STREAM, new Fields("id", "json-timestamp", "topology-timestamp"));
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

		jsonParser = new JsonParser();
	}

	@Override
	public void execute(Tuple tuple, BasicOutputCollector collector) {
		// String json = tuple.getStringByField("json");
		String json = tuple.getString(0);
		Object returnInfo = tuple.getValue(1);
		
		LOG.info("FIRST ARG: " + tuple.getString(0));
		LOG.info("SECOND ARG: " + tuple.getValue(0).toString());
		LOG.info("THIRD ARG: " + tuple.getValue(1));

		String topologyTimestamp = String.valueOf(Calendar.getInstance().getTimeInMillis());

		JsonObject jsonObject = (JsonObject) jsonParser.parse(json);
		JsonElement user = jsonObject.get("user");
		JsonElement channel = jsonObject.get("channel");
		JsonElement timestamp = jsonObject.get("timeStamp");

		if (m_logging) {
			LOG.info("JSON: " + jsonObject.toString());
		}

		// Emit new tuples
		collector.emit(PIPELINE_STREAM, new JsonValue(returnInfo, jsonObject));
		// Statistic
		collector.emit(START_STATISTIC_STREAM,
				new Values(user.getAsString() + "_" + timestamp.getAsString() + "_" + channel.getAsString(),
						timestamp.getAsString(), topologyTimestamp));
	}

}
