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
package at.storm.spout;

import java.util.List;
import java.util.Map;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.storm.commons.Configuration;
import at.storm.commons.JSONDataset;
import at.storm.commons.util.TimeUtils;

public class DatasetJsonSpout extends BaseRichSpout {
	private static final Logger LOG = LoggerFactory.getLogger(DatasetJsonSpout.class);
	
	public static final String ID = "datasetJSON-spout";
	public static final String CONF_STARTUP_SLEEP_MS = ID + ".startup.sleep.ms";
	public static final String CONF_TUPLE_SLEEP_MS = ID + ".tuple.sleep.ms";
	public static final String CONF_TUPLE_SLEEP_NS = ID + ".spout.tuple.sleep.ns";
	private static final long serialVersionUID = 2312223846518561027L;
	private JSONDataset m_dataset;
	private SpoutOutputCollector m_collector;
	private List<String> m_jsons;
	private int m_index = 0;
	private long m_tupleSleepMs = 0;
	private long m_tupleSleepNs = 0;

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// key of output tuples
		declarer.declare(new Fields("json"));
	}

	@SuppressWarnings("rawtypes")
	public void open(Map config, TopologyContext context, SpoutOutputCollector collector) {
		this.m_collector = collector;

		//TODO i need json not tweet
		this.m_dataset = Configuration.getJSONDataSetTwitch();
		this.m_jsons = m_dataset.getTestJSON();

		// Optional sleep between tuples emitting
		if (config.get(CONF_TUPLE_SLEEP_MS) != null) {
			m_tupleSleepMs = (Long) config.get(CONF_TUPLE_SLEEP_MS);
		} else {
			m_tupleSleepMs = 0;
		}
		if (config.get(CONF_TUPLE_SLEEP_NS) != null) {
			m_tupleSleepNs = (Long) config.get(CONF_TUPLE_SLEEP_NS);
		} else {
			m_tupleSleepNs = 0;
		}

		// Optional startup sleep to finish bolt preparation
		// before spout starts emitting
		if (config.get(CONF_STARTUP_SLEEP_MS) != null) {
			long startupSleepMillis = (Long) config.get(CONF_STARTUP_SLEEP_MS);
			TimeUtils.sleepMillis(startupSleepMillis);
		}
		
	}

	public void nextTuple() {
		String json = m_jsons.get(m_index);

		// infinite loop
		m_index++;
		if (m_index >= m_jsons.size()) {
			m_index = 0;
		}
		
		LOG.info("DATASET-JSON-SPOUT JSON: " + json);
		
		// Emit tweet
		m_collector.emit(new Values(json));

		// Optional sleep
		if (m_tupleSleepMs != 0) {
			TimeUtils.sleepMillis(m_tupleSleepMs);
		}
		if (m_tupleSleepNs != 0) {
			TimeUtils.sleepNanos(m_tupleSleepNs);
		}
	}
}
