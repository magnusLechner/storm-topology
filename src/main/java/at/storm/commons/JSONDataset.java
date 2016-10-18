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
package at.storm.commons;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import libsvm.svm_parameter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.storm.commons.svm.SVM;
import at.storm.commons.util.io.FileUtils;
import at.storm.commons.util.io.IOUtils;
import at.storm.commons.util.io.SerializationUtils;

public class JSONDataset implements Serializable {
	private static final long serialVersionUID = 9090067165542638937L;
	public static final String SERIAL_EXTENSION = ".ser";
	private static final Logger LOG = LoggerFactory.getLogger(JSONDataset.class);

	private String m_datasetPath;
	private String m_trainDataFile;
	private String m_devDataFile;
	private String m_testDataFile;

	private String m_delimiter;
	private int m_idIndex;
	private int m_labelIndex;
	private int m_textIndex;

	private List<String> m_positiveLabels;
	private List<String> m_negativeLabels;
	private List<String> m_neutralLabels;

	private int m_positiveValue;
	private int m_negativeValue;
	private int m_neutralValue;

	private svm_parameter m_svmParam;

	private List<String> m_trainJSON = null;
	private List<String> m_devJSON = null;
	private List<String> m_testJSON = null;

	public JSONDataset(String datasetPath, String trainDataFile, String devDataFile, String testDataFile, String delimiter,
			int idIndex, int labelIndex, int textIndex, List<String> positiveLabels, List<String> negativeLabels,
			List<String> neutralLabels, int positiveValue, int negativeValue, int neutralValue,
			svm_parameter svmParam) {
		this.m_datasetPath = datasetPath;
		this.m_trainDataFile = trainDataFile;
		this.m_devDataFile = devDataFile;
		this.m_testDataFile = testDataFile;

		this.m_delimiter = delimiter;

		this.m_idIndex = idIndex;
		this.m_labelIndex = labelIndex;
		this.m_textIndex = textIndex;

		this.m_positiveLabels = positiveLabels;
		this.m_negativeLabels = negativeLabels;
		this.m_neutralLabels = neutralLabels;

		this.m_positiveValue = positiveValue;
		this.m_negativeValue = negativeValue;
		this.m_neutralValue = neutralValue;

		this.m_svmParam = svmParam;
	}

	public String getDatasetPath() {
		return m_datasetPath;
	}

	public String getTrainDataFile() {
		return (m_trainDataFile != null) ? m_datasetPath + File.separator + m_trainDataFile : null;
	}

	public String getTrainDataSerializationFile() {
		return (m_trainDataFile != null) ? m_datasetPath + File.separator + m_trainDataFile + SERIAL_EXTENSION : null;
	}

	public String getDevDataFile() {
		return (m_devDataFile != null) ? m_datasetPath + File.separator + m_devDataFile : null;
	}

	public String getDevDataSerializationFile() {
		return (m_devDataFile != null) ? m_datasetPath + File.separator + m_devDataFile + SERIAL_EXTENSION : null;
	}

	public String getTestDataFile() {
		return (m_testDataFile != null) ? m_datasetPath + File.separator + m_testDataFile : null;
	}

	public String getTestDataSerializationFile() {
		return (m_testDataFile != null) ? m_datasetPath + File.separator + m_testDataFile + SERIAL_EXTENSION : null;
	}

	public String getDelimiter() {
		return m_delimiter;
	}

	public int getIdIndex() {
		return m_idIndex;
	}

	public int getLabelIndex() {
		return m_labelIndex;
	}

	public int getTextIndex() {
		return m_textIndex;
	}

	public List<String> getNegativeLabels() {
		return m_negativeLabels;
	}

	public List<String> getNeutralLabels() {
		return m_neutralLabels;
	}

	public List<String> getPositiveLabels() {
		return m_positiveLabels;
	}

	public int getNegativeValue() {
		return m_negativeValue;
	}

	public int getNeutralValue() {
		return m_neutralValue;
	}

	public int getPositiveValue() {
		return m_positiveValue;
	}

	public svm_parameter getSVMParam() {
		return m_svmParam;
	}

	public List<String> getTrainJSON(boolean includeDevJSON) {
		if ((m_trainJSON == null) && (getTrainDataFile() != null)) {
			// Try deserialization of file
			String serializationFile = getTrainDataSerializationFile();
			if (IOUtils.exists(serializationFile)) {
				LOG.info("Deserialize TrainJSON from: " + serializationFile);
				m_trainJSON = SerializationUtils.deserialize(serializationFile);
			} else {
				LOG.info("Read TrainJSON from: " + getTrainDataFile());
				m_trainJSON = FileUtils.readJSON(getTrainDataFile(), this);
				// optional include dev tweets in training dataset
				if (includeDevJSON) {
					m_trainJSON.addAll(getDevJSON());
				}
			}
		}
		return m_trainJSON;
	}

	public List<String> getTrainJSON(boolean includeDevJSON, boolean reload) {
		if (reload) {
			String serializationFile = getTrainDataSerializationFile();
			if (IOUtils.exists(serializationFile)) {
				LOG.info("Deserialize TrainJSON from: " + serializationFile);
				m_trainJSON = SerializationUtils.deserialize(serializationFile);
			} else {
				LOG.info("Read TrainJSON from: " + getTrainDataFile());
				m_trainJSON = FileUtils.readJSON(getTrainDataFile(), this);
				// optional include dev tweets in training dataset
				if (includeDevJSON) {
					m_trainJSON.addAll(getDevJSON());
				}
			}
			return m_trainJSON;
		} else {
			return getTrainJSON(includeDevJSON);
		}
	}

	public List<String> getDevJSON() {
		if ((m_devJSON == null) && (getDevDataFile() != null)) {
			m_devJSON = FileUtils.readJSON(getDevDataFile(), this);
		}
		return m_devJSON;
	}

	public List<String> getTestJSON() {
		if ((m_testJSON == null) && (getTestDataFile() != null)) {
			// Try deserialization of file
			String serializationFile = getTestDataSerializationFile();
			if (IOUtils.exists(serializationFile)) {
				LOG.info("Deserialize TestJSON from: " + serializationFile);
				m_testJSON = SerializationUtils.deserialize(serializationFile);
			} else {
				LOG.info("Read TestJSON from: " + getTestDataFile());
				m_testJSON = FileUtils.readJSON(getTestDataFile(), this);
			}
		}
		return m_testJSON;
	}

	public List<String> getTestJSON(boolean reload) {
		if (reload) {
			// Try deserialization of file
			String serializationFile = getTestDataSerializationFile();
			if (IOUtils.exists(serializationFile)) {
				LOG.info("Deserialize TestJSON from: " + serializationFile);
				m_testJSON = SerializationUtils.deserialize(serializationFile);
			} else {
				LOG.info("Read TestJSON from: " + getTestDataFile());
				m_testJSON = FileUtils.readJSON(getTestDataFile(), this);
			}
			return m_testJSON;
		} else {
			return getTestJSON();
		}
	}

//	public void printDatasetStats() {
//		LOG.info("Dataset: " + getDatasetPath());
//
//		LOG.info("Train Dataset: " + getTrainDataFile());
//		printTweetStats(getTrainTweets(false));
//
//		LOG.info("Dev Dataset: " + getDevDataFile());
//		printTweetStats(getDevTweets());
//
//		LOG.info("Test Dataset: " + getTestDataFile());
//		printTweetStats(getTestTweets());
//	}

	public static void printTweetStats(List<Tweet> tweets) {
		if (tweets != null) {
			Map<Integer, Integer> counts = new TreeMap<Integer, Integer>();
			for (Tweet tweet : tweets) {
				int key = tweet.getScore().intValue();
				Integer count = counts.get(key);
				counts.put(key, ((count != null) ? count + 1 : 1));
			}

			int total = 0;
			int max = 0;
			for (Map.Entry<Integer, Integer> entry : counts.entrySet()) {
				LOG.info("Class: \t" + entry.getKey() + "\t" + entry.getValue());
				total += entry.getValue();
				if (entry.getValue() > max) {
					max = entry.getValue();
				}
			}
			LOG.info("Total: " + total);

			LOG.info("Optimal Class Weights: ");
			for (Map.Entry<Integer, Integer> entry : counts.entrySet()) {
				LOG.info("Class: \t" + entry.getKey() + "\t" + (max / (double) entry.getValue()));
			}

			// for (Map.Entry<Integer, Integer> entry : counts.entrySet()) {
			// System.out.println("Class: \t" + entry.getKey() + "\t" + max +
			// "\t" + entry.getValue() + "\t"
			// + (max / (double) entry.getValue()));
			// }
		}
	}

	@Override
	public String toString() {
		return "Dataset [datasetPath=" + m_datasetPath + ", trainDataFile=" + m_trainDataFile + ", devDataFile="
				+ m_devDataFile + ", testDataFile=" + m_testDataFile + ", delimiter=" + m_delimiter + ", idIndex="
				+ m_idIndex + ", textIndex=" + m_textIndex + ", labelIndex=" + m_labelIndex + ", positiveLabels="
				+ m_positiveLabels + ", negativeLabel=" + m_negativeLabels + ", neutralLabel=" + m_neutralLabels
				+ ", positiveValue=" + m_positiveValue + ", negativeValue=" + m_negativeValue + ", neutralValue="
				+ m_neutralValue + "]";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static JSONDataset readFromYaml(Map dataset) {
		svm_parameter svmParam = SVM.getDefaultParameter();

		if (dataset.get("svm.kernel") != null) {
			svmParam.kernel_type = (Integer) dataset.get("svm.kernel");
		}

		if (dataset.get("svm.c") != null) {
			svmParam.C = (Double) dataset.get("svm.c");
		}

		if (dataset.get("svm.gamma") != null) {
			svmParam.gamma = (Double) dataset.get("svm.gamma");
		}

		if (dataset.get("svm.class.weights") != null) {
			Map<Integer, Double> classWeights = (Map<Integer, Double>) dataset.get("svm.class.weights");

			svmParam.nr_weight = classWeights.size();
			svmParam.weight_label = new int[svmParam.nr_weight];
			svmParam.weight = new double[svmParam.nr_weight];

			for (Map.Entry<Integer, Double> entry : classWeights.entrySet()) {
				svmParam.weight_label[entry.getKey()] = entry.getKey();
				svmParam.weight[entry.getKey()] = entry.getValue();
			}
		}

		return new JSONDataset((String) dataset.get("path"), (String) dataset.get("train.file"),
				(String) dataset.get("dev.file"), (String) dataset.get("test.file"), (String) dataset.get("delimiter"),
				(Integer) dataset.get("tweetId.index"), (Integer) dataset.get("label.index"),
				(Integer) dataset.get("text.index"), (List<String>) dataset.get("positive.labels"),
				(List<String>) dataset.get("negative.labels"), (List<String>) dataset.get("neutral.labels"),
				(Integer) dataset.get("positive.class.value"), (Integer) dataset.get("negative.class.value"),
				(Integer) dataset.get("neutral.class.value"), svmParam);
	}

	public static void main(String[] args) {
		// Dataset dataset = Configuration.getDataSetSemEval2013();
		// LOG.info("Dataset: SemEval2013");
		// Dataset dataset = Configuration.getDataSetSentiment140();
		// LOG.info("Dataset: Sentiment140");
		Dataset dataset = Configuration.getDataSetTwitch();
		LOG.info("Dataset: Twitch");
		dataset.printDatasetStats();
	}

}
