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

import java.io.File;
import java.util.Map;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import at.storm.bolt.values.data.FeatureGenerationBoltData;
import at.storm.bolt.values.data.SVMBoltData;
import at.storm.bolt.values.statistic.tuple.TupleStatistic;
import at.storm.commons.Configuration;
import at.storm.commons.Dataset;
import at.storm.commons.svm.SVM;
import at.storm.commons.util.io.SerializationUtils;

public class SVMBolt extends BaseRichBolt {
	public static final String ID = "support-vector-maschine-bolt";
	public static final String CONF_LOGGING = ID + ".logging";
	private static final long serialVersionUID = -6790858930924043126L;
	private static final Logger LOG = LoggerFactory.getLogger(SVMBolt.class);

	private boolean m_logging = false;
	private Dataset m_dataset;
	private svm_model m_model;
	private OutputCollector collector;

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(SVMBoltData.getSchema());
	}

	@SuppressWarnings("rawtypes")
	public void prepare(Map config, TopologyContext context, OutputCollector collector) {
		// Optional set logging
		if (config.get(CONF_LOGGING) != null) {
			m_logging = (Boolean) config.get(CONF_LOGGING);
		} else {
			m_logging = false;
		}

		this.collector = collector;

		LOG.info("Loading SVM model...");
		m_dataset = Configuration.getDataSetTwitch();
		m_model = SerializationUtils.deserialize(m_dataset.getDatasetPath() + File.separator + SVM.SVM_MODEL_FILE_SER);

		if (m_model == null) {
			LOG.error("Could not load SVM model! File: " + m_dataset.getDatasetPath() + File.separator
					+ SVM.SVM_MODEL_FILE_SER);
			throw new RuntimeException();
		}
	}

	public void execute(Tuple tuple) {
		FeatureGenerationBoltData featureGenerationValue = FeatureGenerationBoltData.getFromTuple(tuple);
		JsonObject jsonObject = featureGenerationValue.getJsonObject();
		TupleStatistic tupleStatistic = featureGenerationValue.getTupleStatistic();
		Map<Integer, Double> featureVector = featureGenerationValue.getFeatureVector();

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

		JsonObject score = new JsonObject();
		score.addProperty("score", convertRating(predictedClass));

		jsonObject.add("sentiment", score);

		if (m_logging) {
			LOG.info("SVM all done log: " + jsonObject.toString());
		}

		collector.emit(tuple, new SVMBoltData(jsonObject, tupleStatistic));
		collector.ack(tuple);
	}

	private static int convertRating(double predicted) {
		if (predicted == 0.0) {
			return -1;
		} else if (predicted == 1.0) {
			return 0;
		} else if (predicted == 2.0) {
			return 1;
		} else {
			throw new IllegalArgumentException("SVM prediction couldnt be converted to a rating between -1 and 1");
		}
	}

}
