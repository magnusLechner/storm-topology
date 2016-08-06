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
import at.illecker.sentistorm.bolt.JSONBolt;
import at.illecker.sentistorm.bolt.POSTaggerBolt;
import at.illecker.sentistorm.bolt.PreprocessorBolt;
import at.illecker.sentistorm.bolt.SVMBolt;
import at.illecker.sentistorm.bolt.StatisticBolt;
import at.illecker.sentistorm.bolt.TokenizerBolt;
import at.illecker.sentistorm.commons.Configuration;
import at.illecker.sentistorm.commons.util.io.kyro.TaggedTokenSerializer;
import at.illecker.sentistorm.spout.DatasetJsonSpout;
import cmu.arktweetnlp.Tagger.TaggedToken;

import com.esotericsoftware.kryo.serializers.DefaultSerializers.TreeMapSerializer;

public class SentiStormTopology {
	public static final String TOPOLOGY_NAME = "senti-storm-topology";

	public static void main(String[] args) throws Exception {
		Config conf = new Config();

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
//		IRichSpout spout = new DRPCSpout("getSentiment", drpc);

		 IRichSpout spout = new DRPCSpout("getSentiment");
		String spoutID = "DRPCSpout";

		// Create Bolts
		JSONBolt jsonBolt = new JSONBolt();
		TokenizerBolt tokenizerBolt = new TokenizerBolt();
		PreprocessorBolt preprocessorBolt = new PreprocessorBolt();
		POSTaggerBolt posTaggerBolt = new POSTaggerBolt();
		FeatureGenerationBolt featureGenerationBolt = new FeatureGenerationBolt();
		SVMBolt svmBolt = new SVMBolt();
		ReturnResults returnBolt = new ReturnResults();
		String returnBoltID = "return";

		StatisticBolt statisticBolt = new StatisticBolt();

		// Create Topology
		TopologyBuilder builder = new TopologyBuilder();

		// Set Spout
		builder.setSpout(spoutID, spout, Configuration.get("sentistorm.spout.parallelism", 1));

		// Set Spout --> JSONBolt
		builder.setBolt(JSONBolt.ID, jsonBolt, Configuration.get("sentistorm.bolt.json.parallelism", 1))
				.shuffleGrouping(spoutID);

		// Set JSONBolt --> TokenizerBolt
		builder.setBolt(TokenizerBolt.ID, tokenizerBolt, Configuration.get("sentistorm.bolt.tokenizer.parallelism", 1))
				.shuffleGrouping(JSONBolt.ID, JSONBolt.PIPELINE_STREAM);

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

//		builder.setBolt(StatisticBolt.ID, statisticBolt, Configuration.get("sentistorm.bolt.statistic.parallelism", 1))
//				.shuffleGrouping(JSONBolt.ID, JSONBolt.START_STATISTIC_STREAM)
//				.shuffleGrouping(SVMBolt.ID, SVMBolt.END_STATISTIC_STREAM);

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

		conf.put(JSONBolt.CONF_LOGGING, Configuration.get("sentistorm.bolt.json.logging", false));
		conf.put(TokenizerBolt.CONF_LOGGING, Configuration.get("sentistorm.bolt.tokenizer.logging", false));
		conf.put(PreprocessorBolt.CONF_LOGGING, Configuration.get("sentistorm.bolt.preprocessor.logging", false));
		conf.put(POSTaggerBolt.CONF_LOGGING, Configuration.get("sentistorm.bolt.postagger.logging", false));
		conf.put(POSTaggerBolt.CONF_MODEL, Configuration.get("sentistorm.bolt.postagger.model"));
		conf.put(FeatureGenerationBolt.CONF_LOGGING,
				Configuration.get("sentistorm.bolt.featuregeneration.logging", false));
		conf.put(SVMBolt.CONF_LOGGING, Configuration.get("sentistorm.bolt.svm.logging", false));

		conf.put(Config.TOPOLOGY_FALL_BACK_ON_JAVA_SERIALIZATION, false);
		conf.registerSerialization(TaggedToken.class, TaggedTokenSerializer.class);
		conf.registerSerialization(TreeMap.class, TreeMapSerializer.class);

//		LocalCluster cluster = new LocalCluster();
//		cluster.submitTopology("getSentiment", conf, builder.createTopology());
//		for (int i = 0; i < 1; i++) {
//			System.out.println("HALLO: " + drpc.execute("getSentiment",
//					"{\"msg\":\"Kreygasm\",\"user\":\"theUser\",\"channel\":\"TheChannel\",\"timeStamp\":\"TheTimeStamp\"}"));
//		}
//		cluster.shutdown();
//		drpc.shutdown();

		StormSubmitter.submitTopology(TOPOLOGY_NAME, conf, builder.createTopology());

		System.out.println("To kill the topology run (if started locally for testing purposes):");
		System.out.println("storm kill " + TOPOLOGY_NAME);
	}

}
