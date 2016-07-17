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

public class JSONBolt extends BaseBasicBolt {
	public static final String ID = "json-bolt";
	public static final String CONF_LOGGING = ID + ".logging";
	private static final long serialVersionUID = -8847712312925641497L;
	private static final Logger LOG = LoggerFactory.getLogger(JSONBolt.class);
	private boolean m_logging = false;

	private JsonParser jsonParser;

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// key of output tuples
		declarer.declare(new Fields("text", "json"));
	}

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
		JsonElement jsonElement = jsonParser.parse(tuple.getStringByField("json"));
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		JsonObject content = jsonObject.getAsJsonObject("content");

		if (m_logging) {
			 LOG.info("content: \"" + content.getAsString() + "\" JSON: " + jsonObject.getAsString());
		}
		
		System.out.println("content: \"" + content.getAsString() + "\" JSON: " + jsonObject.getAsString());

		// Emit new tuples
		 collector.emit(new Values(content.getAsString(), jsonObject));
	}

}
