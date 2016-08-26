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
package at.illecker.sentistorm.commons;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import org.apache.storm.shade.org.yaml.snakeyaml.Yaml;
import org.apache.storm.shade.org.yaml.snakeyaml.constructor.SafeConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.illecker.sentistorm.commons.util.io.IOUtils;

public class Configuration {
	private static final Logger LOG = LoggerFactory.getLogger(Configuration.class);

	public static final boolean RUNNING_WITHIN_JAR = Configuration.class.getResource("Configuration.class").toString()
			.startsWith("jar:");

	public static final String WORKING_DIR_PATH = (RUNNING_WITHIN_JAR) ? ""
			: System.getProperty("user.dir") + File.separator;

	public static final String TEMP_DIR_PATH = System.getProperty("java.io.tmpdir");

	public static final String GLOBAL_RESOURCES_DATASETS_SEMEVAL = "global.resources.datasets.semeval";
	public static final String GLOBAL_RESOURCES_DATASETS_SENTIMENT140 = "global.resources.datasets.sentiment140";
	public static final String GLOBAL_RESOURCES_DATASETS_TWITCH = "global.resources.datasets.twitch";
	public static final String GLOBAL_RESOURCES_DATASETS_MY_TEST = "global.resources.datasets.mytest";
	public static final String GLOBAL_RESOURCES_DICT = "global.resources.dict";
	public static final String GLOBAL_RESOURCES_DICT_SENTIMENT = "global.resources.dict.sentiment";
	public static final String GLOBAL_RESOURCES_DICT_SLANG = "global.resources.dict.slang";
	public static final String GLOBAL_RESOURCES_DICT_WORDNET_PATH = "global.resources.dict.wordnet.path";

	@SuppressWarnings("rawtypes")
	public static final Map CONFIG = readConfig();

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map readConfig() {
		// for running SVM local
//		 String file = "/home/magnus/workspace/storm-topology/src/main/conf/";
//		 Map conf = readConfigFile(file + "senti-defaults.yaml", true);

		Map conf = readConfigFile(WORKING_DIR_PATH + "senti-defaults.yaml", true);
		// read custom config
		LOG.info("Try to load user-specific config...");
		Map customConfig = readConfigFile(WORKING_DIR_PATH + "configuration.yaml", false);
		if (customConfig != null) {
			conf.putAll(customConfig);
		} else if (RUNNING_WITHIN_JAR) {
			customConfig = readConfigFile("../configuration.yaml", false);
			if (customConfig != null) {
				conf.putAll(customConfig);
			}
		}
		return conf;
	}

	@SuppressWarnings("rawtypes")
	public static Map readConfigFile(String file, boolean mustExist) {
		Yaml yaml = new Yaml(new SafeConstructor());
		Map ret = null;
		InputStream input = IOUtils.getInputStream(file);
		if (input != null) {
			ret = (Map) yaml.load(new InputStreamReader(input));
			LOG.info("Loaded " + file);
			try {
				input.close();
			} catch (IOException e) {
				LOG.error("IOException: " + e.getMessage());
			}
		} else if (mustExist) {
			LOG.error("Config file " + file + " was not found!");
		}
		if ((ret == null) && (mustExist)) {
			throw new RuntimeException("Config file " + file + " was not found!");
		}
		return ret;
	}

	public static <K, V> V get(K key) {
		return get(key, null);
	}

	@SuppressWarnings("unchecked")
	public static <K, V> V get(K key, V defaultValue) {
		return get((Map<K, V>) CONFIG, key, defaultValue);
	}

	public static <K, V> V get(Map<K, V> map, K key, V defaultValue) {
		V value = map.get(key);
		if (value == null) {
			value = defaultValue;
		}
		return value;
	}

	@SuppressWarnings("rawtypes")
	public static Dataset getDataSetSemEval2013() {
		return Dataset.readFromYaml((Map) ((Map) CONFIG.get(GLOBAL_RESOURCES_DATASETS_SEMEVAL)).get("2013"));
	}

	@SuppressWarnings("rawtypes")
	public static Dataset getDataSetSentiment140() {
		return Dataset.readFromYaml((Map) ((Map) CONFIG.get(GLOBAL_RESOURCES_DATASETS_SENTIMENT140)).get("140"));
	}

	@SuppressWarnings("rawtypes")
	public static Dataset getDataSetTwitch() {
		return Dataset.readFromYaml((Map) ((Map) CONFIG.get(GLOBAL_RESOURCES_DATASETS_TWITCH)).get("twitch"));
	}

	@SuppressWarnings("rawtypes")
	public static JSONDataset getJSONDataSetTwitch() {
		return JSONDataset.readFromYaml((Map) ((Map) CONFIG.get(GLOBAL_RESOURCES_DATASETS_TWITCH)).get("JSONtwitch"));
	}

	@SuppressWarnings("rawtypes")
	public static Dataset getDataSetMyTest() {
		return Dataset.readFromYaml((Map) ((Map) CONFIG.get(GLOBAL_RESOURCES_DATASETS_MY_TEST)).get("mytest"));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<String> getFirstNames() {
		return (List<String>) ((Map) CONFIG.get(GLOBAL_RESOURCES_DICT)).get("FirstNames");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<String> getStopWords() {
		return (List<String>) ((Map) CONFIG.get(GLOBAL_RESOURCES_DICT)).get("StopWords");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<Map> getSentimentWordlists() {
		return (List<Map>) CONFIG.get(GLOBAL_RESOURCES_DICT_SENTIMENT);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<Map> getSlangWordlists() {
		return (List<Map>) CONFIG.get(GLOBAL_RESOURCES_DICT_SLANG);
	}

	public static String getWordNetDict() {
		return (String) CONFIG.get(GLOBAL_RESOURCES_DICT_WORDNET_PATH);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<String> getTwitchEmoticons() {
		return (List<String>) ((Map) CONFIG.get(GLOBAL_RESOURCES_DICT)).get("TwitchEmoticons");
	}

}
