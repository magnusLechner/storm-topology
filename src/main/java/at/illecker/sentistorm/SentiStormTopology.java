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

package at.illecker.sentistorm;

import java.util.TreeMap;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.LocalDRPC;
import org.apache.storm.StormSubmitter;
import org.apache.storm.drpc.DRPCSpout;
import org.apache.storm.drpc.ReturnResults;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.TopologyBuilder;

import at.illecker.sentistorm.bolt.FeatureGenerationBolt;
import at.illecker.sentistorm.bolt.JsonBolt;
import at.illecker.sentistorm.bolt.POSTaggerBolt;
import at.illecker.sentistorm.bolt.PreprocessorBolt;
import at.illecker.sentistorm.bolt.SVMBolt;
import at.illecker.sentistorm.bolt.StatisticBolt;
import at.illecker.sentistorm.bolt.StatisticJsonBolt;
import at.illecker.sentistorm.bolt.TokenizerBolt;
import at.illecker.sentistorm.commons.Configuration;
import at.illecker.sentistorm.commons.util.io.kyro.TaggedTokenSerializer;
import at.illecker.sentistorm.spout.DatasetJsonSpout;
import cmu.arktweetnlp.Tagger.TaggedToken;

import com.esotericsoftware.kryo.serializers.DefaultSerializers.TreeMapSerializer;

public class SentiStormTopology {
	public static final String TOPOLOGY_NAME = "senti-storm-topology";
	public static final String DRPC_SPOUT_ID = "DRPCSpoutID";
	public static final String DRPC_FUNCTION_CALL = "getSentiment";
	public static final String RETURN_RESULT_BOLT_ID = "return-result-bolt";

	public static void main(String[] args) throws Exception {
		Config conf = new Config();

		// TODO Things to remember:
		// - check Configuration class if local path or jar path
		// - check senti-defaults.yaml if local path or jar path in Twitch
		// - check if model.ser is there
		// - check senti-defaults.yaml for parallelism
		// - check senti-defaults.yaml and storm.yaml for RAM usage

		// no more storm log-output
		conf.put(Config.TOPOLOGY_DEBUG, false);

		// Create Spout
		// if (Configuration.get("sentistorm.spout.startup.sleep.ms") != null) {
		// conf.put(DatasetJSONSpout.CONF_STARTUP_SLEEP_MS,
		// (Integer) Configuration.get("sentistorm.spout.startup.sleep.ms"));
		// }
		// if (Configuration.get("sentistorm.spout.tuple.sleep.ms") != null) {
		// conf.put(DatasetJSONSpout.CONF_TUPLE_SLEEP_MS,
		// (Integer) Configuration.get("sentistorm.spout.tuple.sleep.ms"));
		// }
		// if (Configuration.get("sentistorm.spout.tuple.sleep.ns") != null) {
		// conf.put(DatasetJSONSpout.CONF_TUPLE_SLEEP_NS,
		// (Integer) Configuration.get("sentistorm.spout.tuple.sleep.ns"));
		// }
		// IRichSpout spout = new DatasetJSONSpout();
		// String spoutID = DatasetJSONSpout.ID;

//		LocalDRPC drpc = new LocalDRPC();
//		IRichSpout spout = new DRPCSpout(DRPC_FUNCTION_CALL, drpc);

		 IRichSpout spout = new DRPCSpout(DRPC_FUNCTION_CALL);
		String spoutID = DRPC_SPOUT_ID;

		// Create Bolts
		JsonBolt jsonBolt = new JsonBolt();
		TokenizerBolt tokenizerBolt = new TokenizerBolt();
		PreprocessorBolt preprocessorBolt = new PreprocessorBolt();
		POSTaggerBolt posTaggerBolt = new POSTaggerBolt();
		FeatureGenerationBolt featureGenerationBolt = new FeatureGenerationBolt();
		SVMBolt svmBolt = new SVMBolt();
		ReturnResults returnBolt = new ReturnResults();
		String returnBoltID = RETURN_RESULT_BOLT_ID;
		StatisticBolt statisticBolt = new StatisticBolt();
		StatisticJsonBolt statisticJsonBolt = new StatisticJsonBolt();

		// Create Topology
		TopologyBuilder builder = new TopologyBuilder();

		// Set Spout
		builder.setSpout(spoutID, spout, Configuration.get("sentistorm.spout.parallelism", 1));

		// Set Spout --> JSONBolt
		builder.setBolt(JsonBolt.ID, jsonBolt, Configuration.get("sentistorm.bolt.json.parallelism", 1))
				.shuffleGrouping(spoutID);

		// Set JSONBolt --> TokenizerBolt
		builder.setBolt(TokenizerBolt.ID, tokenizerBolt, Configuration.get("sentistorm.bolt.tokenizer.parallelism", 1))
				.shuffleGrouping(JsonBolt.ID, JsonBolt.PIPELINE_STREAM);

		// TokenizerBolt --> PreprocessorBolt
		builder.setBolt(PreprocessorBolt.ID, preprocessorBolt,
				Configuration.get("sentistorm.bolt.preprocessor.parallelism", 1)).shuffleGrouping(TokenizerBolt.ID);

		// PreprocessorBolt --> POSTaggerBolt
		builder.setBolt(POSTaggerBolt.ID, posTaggerBolt, Configuration.get("sentistorm.bolt.postagger.parallelism", 1))
				.shuffleGrouping(PreprocessorBolt.ID);

		// POSTaggerBolt --> FeatureGenerationBolt
		builder.setBolt(FeatureGenerationBolt.ID, featureGenerationBolt,
				Configuration.get("sentistorm.bolt.featuregeneration.parallelism", 1))
				.shuffleGrouping(POSTaggerBolt.ID);

		// FeatureGenerationBolt --> SVMBolt
		builder.setBolt(SVMBolt.ID, svmBolt, Configuration.get("sentistorm.bolt.svm.parallelism", 1))
				.shuffleGrouping(FeatureGenerationBolt.ID);

		// SVMBolt --> ReturnResults
		builder.setBolt(returnBoltID, returnBolt, Configuration.get("sentistorm.bolt.return.parallelism", 1))
				.shuffleGrouping(SVMBolt.ID, SVMBolt.PIPELINE_STREAM);

		// JSONBolt & SVMBolt --> StatisticBolt
		builder.setBolt(StatisticBolt.ID, statisticBolt, Configuration.get("sentistorm.bolt.statistic.parallelism", 1))
				.shuffleGrouping(JsonBolt.ID, JsonBolt.JSON_BOLT_STATISTIC_STREAM)
				.shuffleGrouping(SVMBolt.ID, SVMBolt.SVM_BOLT_STATISTIC_STREAM);
//
//		// StatisticBolt --> StatisticJsonBolt
		builder.setBolt(StatisticJsonBolt.ID, statisticJsonBolt,
				Configuration.get("sentistorm.bolt.statisticJson.parallelism", 1)).shuffleGrouping(StatisticBolt.ID);

		// Set topology config
		conf.setNumWorkers(Configuration.get("sentistorm.workers.num", 1));

		if (Configuration.get("sentistorm.spout.max.pending") != null) {
			conf.setMaxSpoutPending((Integer) Configuration.get("sentistorm.spout.max.pending"));
		}

		if (Configuration.get("sentistorm.workers.childopts") != null) {
			conf.put(Config.WORKER_CHILDOPTS, Configuration.get("sentistorm.workers.childopts"));
		}
		if (Configuration.get("sentistorm.supervisor.childopts") != null) {
			conf.put(Config.SUPERVISOR_CHILDOPTS, Configuration.get("sentistorm.supervisor.childopts"));
		}

		conf.put(JsonBolt.CONF_LOGGING, Configuration.get("sentistorm.bolt.json.logging", false));
		conf.put(TokenizerBolt.CONF_LOGGING, Configuration.get("sentistorm.bolt.tokenizer.logging", false));
		conf.put(PreprocessorBolt.CONF_LOGGING, Configuration.get("sentistorm.bolt.preprocessor.logging", false));
		conf.put(POSTaggerBolt.CONF_LOGGING, Configuration.get("sentistorm.bolt.postagger.logging", false));
		conf.put(POSTaggerBolt.CONF_MODEL, Configuration.get("sentistorm.bolt.postagger.model"));
		conf.put(FeatureGenerationBolt.CONF_LOGGING,
				Configuration.get("sentistorm.bolt.featuregeneration.logging", false));
		conf.put(SVMBolt.CONF_LOGGING, Configuration.get("sentistorm.bolt.svm.logging", false));
		conf.put(StatisticBolt.CONF_LOGGING, Configuration.get("sentistorm.bolt.statistic.logging", false));

		conf.put(StatisticBolt.CONF_INTERVAL, Configuration.get("sentistorm.bolt.statistic.interval", 500));

		conf.put(Config.TOPOLOGY_FALL_BACK_ON_JAVA_SERIALIZATION, false);
		conf.registerSerialization(TaggedToken.class, TaggedTokenSerializer.class);
		conf.registerSerialization(TreeMap.class, TreeMapSerializer.class);

//		LocalCluster cluster = new LocalCluster();
//		cluster.submitTopology("getSentiment", conf, builder.createTopology());
//		long time = System.currentTimeMillis();
//		for (int i = 0; i < 1000; i++) {
//			System.out.println("HALLO: " + drpc.execute("getSentiment",
//					"{\"msg\":\"Kreygasm\",\"user\":\"theUser\",\"channel\":\"TheChannel\",\"timestamp\":\"" + (time + i) +"\"}"));
//		}
//		cluster.shutdown();
//		drpc.shutdown();

		StormSubmitter.submitTopology(TOPOLOGY_NAME, conf, builder.createTopology());

		System.out.println("To kill the topology run (if started locally for testing purposes):");
		System.out.println("storm kill " + TOPOLOGY_NAME);
	}

}
