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

package at.storm;

import java.util.TreeMap;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.TopologyBuilder;

import cmu.arktweetnlp.Tagger.TaggedToken;

import com.esotericsoftware.kryo.serializers.DefaultSerializers.TreeMapSerializer;

import at.storm.bolt.FeatureGenerationBolt;
import at.storm.bolt.JsonBolt;
import at.storm.bolt.POSTaggerBolt;
import at.storm.bolt.PreprocessorBolt;
import at.storm.bolt.RedisPublishBolt;
import at.storm.bolt.SVMBolt;
import at.storm.bolt.StatisticBolt;
import at.storm.bolt.StatisticJsonBolt;
import at.storm.bolt.TokenizerBolt;
import at.storm.commons.Configuration;
import at.storm.commons.util.io.kyro.TaggedTokenSerializer;
import at.storm.spout.RedisSpout;

public class StormTopology {
	public static final String TOPOLOGY_NAME = "storm-topology";

	public static void main(String[] args) throws Exception {
		// TODO Things to remember:
		// - check if LocalCluster or SubmitTopology
		// - check if redis-spout is localhost or redis.test.svc.cluster.local
		// - check if redis-publish is localhost or redis-integration.test.svc.cluster.local
		// - check Configuration class if local path or jar path
		// - check senti-defaults.yaml if local path or jar path in Twitch
		// - check if model.ser is there
		// - check senti-defaults.yaml for parallelism
		// - check senti-defaults.yaml and storm.yaml for RAM usage

		Config conf = new Config();

		// No more storm log-output
		conf.put(Config.TOPOLOGY_DEBUG, false);

		String redisSpoutHost = Configuration.get("redis.spout.host");
		int redisSpoutPort = Configuration.get("redis.spout.port");
		String redisSpoutPattern = Configuration.get("redis.spout.topic");

		String redisPublishHost = Configuration.get("redis.publish.host");
		int redisPublishPort = Configuration.get("redis.publish.port");
		String redisPublishTopic = Configuration.get("redis.publish.topic");

		// Create Spout
		IRichSpout spout = new RedisSpout(redisSpoutHost, redisSpoutPort, redisSpoutPattern);
		// Create Bolts
		JsonBolt jsonBolt = new JsonBolt();
		TokenizerBolt tokenizerBolt = new TokenizerBolt();
		PreprocessorBolt preprocessorBolt = new PreprocessorBolt();
		POSTaggerBolt posTaggerBolt = new POSTaggerBolt();
		FeatureGenerationBolt featureGenerationBolt = new FeatureGenerationBolt();
		SVMBolt svmBolt = new SVMBolt();
		RedisPublishBolt redisPublishBolt = new RedisPublishBolt(redisPublishHost, redisPublishPort, redisPublishTopic);
		StatisticBolt statisticBolt = new StatisticBolt();
		StatisticJsonBolt statisticJsonBolt = new StatisticJsonBolt();

		// Create Topology
		TopologyBuilder builder = new TopologyBuilder();

		// Set Spout
		builder.setSpout(RedisSpout.ID, spout, Configuration.get("sentistorm.spout.parallelism", 1));

		// Set Spout --> JSONBolt
		builder.setBolt(JsonBolt.ID, jsonBolt, Configuration.get("sentistorm.bolt.json.parallelism", 1))
				.shuffleGrouping(RedisSpout.ID);

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

		// SVMBolt --> RedisPublishBolt
		builder.setBolt(RedisPublishBolt.ID, redisPublishBolt,
				Configuration.get("sentistorm.bolt.redis.publish.parallelism", 1)).shuffleGrouping(SVMBolt.ID)
				.shuffleGrouping(JsonBolt.ID, JsonBolt.TO_REDIS_PUBLISH_STREAM);

		// RedisPublishBolt --> StatisticBolt
		builder.setBolt(StatisticBolt.ID, statisticBolt, Configuration.get("sentistorm.bolt.statistic.parallelism", 1))
				.shuffleGrouping(RedisPublishBolt.ID);

		// StatisticBolt --> StatisticJsonBolt
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

		conf.put(RedisSpout.CONF_LOGGING, Configuration.get("sentistorm.spout.redis.logging", false));
		conf.put(JsonBolt.CONF_LOGGING, Configuration.get("sentistorm.bolt.json.logging", false));
		conf.put(TokenizerBolt.CONF_LOGGING, Configuration.get("sentistorm.bolt.tokenizer.logging", false));
		conf.put(PreprocessorBolt.CONF_LOGGING, Configuration.get("sentistorm.bolt.preprocessor.logging", false));
		conf.put(POSTaggerBolt.CONF_LOGGING, Configuration.get("sentistorm.bolt.postagger.logging", false));
		conf.put(POSTaggerBolt.CONF_MODEL, Configuration.get("sentistorm.bolt.postagger.model"));
		conf.put(FeatureGenerationBolt.CONF_LOGGING,
				Configuration.get("sentistorm.bolt.featuregeneration.logging", false));
		conf.put(SVMBolt.CONF_LOGGING, Configuration.get("sentistorm.bolt.svm.logging", false));
		conf.put(RedisPublishBolt.CONF_LOGGING, Configuration.get("sentistorm.bolt.redis.publish.logging", false));
		conf.put(StatisticBolt.CONF_LOGGING, Configuration.get("sentistorm.bolt.statistic.logging", false));
		conf.put(StatisticJsonBolt.CONF_LOGGING, Configuration.get("sentistorm.bolt.statisticJson.logging", false));

		conf.put(RedisSpout.CONF_STARTUP_SLEEP_MS, Configuration.get("sentistorm.spout.startup.sleep.ms"));
		conf.put(StatisticBolt.CONF_INTERVAL, Configuration.get("sentistorm.bolt.statistic.interval", 1000));

		conf.put(Config.TOPOLOGY_FALL_BACK_ON_JAVA_SERIALIZATION, false);
		conf.registerSerialization(TaggedToken.class, TaggedTokenSerializer.class);
		conf.registerSerialization(TreeMap.class, TreeMapSerializer.class);

		// LocalCluster cluster = new LocalCluster();
		// cluster.submitTopology(TOPOLOGY_NAME, conf,
		// builder.createTopology());
		// cluster.shutdown();

		StormSubmitter.submitTopology(TOPOLOGY_NAME, conf, builder.createTopology());

		System.out.println("To kill the topology run (if started locally for testing purposes):");
		System.out.println("storm kill " + TOPOLOGY_NAME);
	}

}
