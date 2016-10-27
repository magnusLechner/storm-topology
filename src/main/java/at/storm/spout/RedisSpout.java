package at.storm.spout;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import at.storm.bolt.values.data.RedisSpoutData;
import at.storm.bolt.values.statistic.tuple.TupleStatistic;
import at.storm.commons.LanguageDetection;
import at.storm.commons.util.TimeUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

public class RedisSpout extends BaseRichSpout {
	static final long serialVersionUID = 737015318988609460L;
	public static final String ID = "redis-spout";
	public static final String CONF_LOGGING = ID + ".logging";
	public static final String CONF_STARTUP_SLEEP_MS = ID + ".startup.sleep.ms";
	public static final String PIPELINE_STREAM = "pipeline-stream";
	public static final String TO_REDIS_PUBLISH_STREAM = "not-english-stream";
	private static final Logger LOG = LoggerFactory.getLogger(RedisSpout.class);
	private boolean m_logging = false;
	
	private JsonParser jsonParser;
	
	SpoutOutputCollector collector;
	final String host;
	final int port;
	final String pattern;
	LinkedBlockingQueue<String> queue;
	JedisPool pool;

	public RedisSpout(String host, int port, String pattern) {
		this.host = host;
		this.port = port;
		this.pattern = pattern;
	}

	class ListenerThread extends Thread {
		LinkedBlockingQueue<String> queue;
		JedisPool pool;
		String pattern;

		public ListenerThread(LinkedBlockingQueue<String> queue, JedisPool pool, String pattern) {
			this.queue = queue;
			this.pool = pool;
			this.pattern = pattern;
		}

		public void run() {

			JedisPubSub listener = new JedisPubSub() {

				@Override
				public void onMessage(String channel, String message) {
					queue.offer(message);
				}

				@Override
				public void onPMessage(String pattern, String channel, String message) {
					queue.offer(message);
				}

				@Override
				public void onPSubscribe(String channel, int subscribedChannels) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onPUnsubscribe(String channel, int subscribedChannels) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onSubscribe(String channel, int subscribedChannels) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onUnsubscribe(String channel, int subscribedChannels) {
					// TODO Auto-generated method stub

				}
			};

			Jedis jedis = pool.getResource();
			try {
				jedis.psubscribe(listener, pattern);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}
	};

	@SuppressWarnings("rawtypes")
	public void open(Map config, TopologyContext context, SpoutOutputCollector collector) {
		this.collector = collector;
		this.jsonParser = new JsonParser();
		
		queue = new LinkedBlockingQueue<String>(5000);
		pool = new JedisPool(new JedisPoolConfig(), host, port);
		
		// Optional set logging
		if (config.get(CONF_LOGGING) != null) {
			m_logging = (Boolean) config.get(CONF_LOGGING);
		} else {
			m_logging = false;
		}
		
		if (config.get(CONF_STARTUP_SLEEP_MS) != null) {
			long startupSleepMillis = (Long) config.get(CONF_STARTUP_SLEEP_MS);
			TimeUtils.sleepMillis(startupSleepMillis);
		}

		ListenerThread listener = new ListenerThread(queue, pool, pattern);
		listener.start();
	}

	public void close() {
		pool.destroy();
	}

	public void nextTuple() {
		String jsonAsString = queue.poll();
		if (jsonAsString == null) {
			Utils.sleep(20);
		} else {
			if (m_logging) {
				LOG.info("REDIS-SPOUT: " + jsonAsString);
			}
			
			JsonObject jsonObject = (JsonObject) jsonParser.parse(jsonAsString);
			
			if (m_logging) {
				LOG.info("JSON: " + jsonObject.toString());
			}
			
			TupleStatistic tupleStatistic = new TupleStatistic();
			tupleStatistic.setPipelineStart(System.currentTimeMillis());
			tupleStatistic.setRealStart(jsonObject.get("timestamp").getAsLong());
			
			String message = jsonObject.get("msg").getAsString();
			
			if(!LanguageDetection.isEnglish(message)) {
				JsonObject score = new JsonObject();
				score.addProperty("score", 0);
				
				jsonObject.add("sentiment", score);
				
				if (m_logging) {
					LOG.info("NOT ENGLISH: " + jsonObject.toString());
				}
				
				
				collector.emit(TO_REDIS_PUBLISH_STREAM, new RedisSpoutData(jsonObject, tupleStatistic));
			} else {
				
				if (m_logging) {
					LOG.info("ENGLISH: " + jsonObject.toString());
				}
				
				collector.emit(PIPELINE_STREAM, new RedisSpoutData(jsonObject, tupleStatistic));	
			}
		}
	}

	public void ack(Object msgId) {

	}

	public void fail(Object msgId) {

	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
//		declarer.declare(new Fields("jsonString", "tupleStatistic"));
		declarer.declareStream(PIPELINE_STREAM, RedisSpoutData.getSchema());
		declarer.declareStream(TO_REDIS_PUBLISH_STREAM, RedisSpoutData.getSchema());
	}

	public boolean isDistributed() {
		return false;
	}
}
