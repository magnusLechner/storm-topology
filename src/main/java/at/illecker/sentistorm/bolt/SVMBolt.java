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

import java.io.File;
import java.util.Map;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import at.illecker.sentistorm.commons.Configuration;
import at.illecker.sentistorm.commons.Dataset;
import at.illecker.sentistorm.commons.SentimentClass;
import at.illecker.sentistorm.commons.svm.SVM;
import at.illecker.sentistorm.commons.util.io.SerializationUtils;

public class SVMBolt extends BaseBasicBolt {
	public static final String ID = "support-vector-maschine-bolt";
	public static final String CONF_LOGGING = ID + ".logging";
	private static final long serialVersionUID = -6790858930924043126L;
	private static final Logger LOG = LoggerFactory.getLogger(SVMBolt.class);
	private boolean m_logging = false;
	private Dataset m_dataset;
	private svm_model m_model;

	private JsonParser jsonParser;

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("text", "predictedSentiment", "json"));
	}

	@Override
	public void prepare(Map config, TopologyContext context) {
		// Optional set logging
		if (config.get(CONF_LOGGING) != null) {
			m_logging = (Boolean) config.get(CONF_LOGGING);
		} else {
			m_logging = false;
		}

		LOG.info("Loading SVM model...");
		// m_dataset = Configuration.getDataSetSemEval2013();
		m_dataset = Configuration.getDataSetTwitch();
		m_model = SerializationUtils.deserialize(m_dataset.getDatasetPath() + File.separator + SVM.SVM_MODEL_FILE_SER);

		if (m_model == null) {
			LOG.error("Could not load SVM model! File: " + m_dataset.getDatasetPath() + File.separator
					+ SVM.SVM_MODEL_FILE_SER);
			throw new RuntimeException();
		}

		jsonParser = new JsonParser();
	}

	@Override
	public void execute(Tuple tuple, BasicOutputCollector collector) {
		String text = tuple.getStringByField("text");
		String json = tuple.getStringByField("json");
		Map<Integer, Double> featureVector = (Map<Integer, Double>) tuple.getValueByField("featureVector");

		// Create feature nodes
		svm_node[] testNodes = new svm_node[featureVector.size()];
		int i = 0;
		for (Map.Entry<Integer, Double> feature : featureVector.entrySet()) {
			svm_node node = new svm_node();
			node.index = feature.getKey();
			node.value = feature.getValue();
			testNodes[i] = node;
			i++;
		}

		double predictedClass = svm.svm_predict(m_model, testNodes);

		JsonObject jsonObject = (JsonObject) jsonParser.parse(json);
		jsonObject.addProperty("predictedSentiment",
				(SentimentClass.fromScore(m_dataset, (int) predictedClass)).toString());

		if (m_logging) {
			LOG.info("Tweet: " + text + " predictedSentiment: "
					+ SentimentClass.fromScore(m_dataset, (int) predictedClass) + " JSON: " + jsonObject.toString());
		}
		
		LOG.info("Tweet: " + text + " predictedSentiment: "
				+ SentimentClass.fromScore(m_dataset, (int) predictedClass) + " JSON: " + jsonObject.toString());

		// Emit new tuples
		collector.emit(new Values(text, SentimentClass.fromScore(m_dataset, (int) predictedClass), jsonObject.toString()));
	}

}
