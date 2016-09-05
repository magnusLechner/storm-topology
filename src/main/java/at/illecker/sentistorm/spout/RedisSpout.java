package at.illecker.sentistorm.spout;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.illecker.sentistorm.bolt.values.statistic.tuple.TupleStatistic;
import at.illecker.sentistorm.commons.util.TimeUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

public class RedisSpout extends BaseRichSpout {
	static final long serialVersionUID = 737015318988609460L;
	public static final String ID = "redis-spout";
	public static final String CONF_LOGGING = ID + ".logging";
	public static final String CONF_STARTUP_SLEEP_MS = ID + ".startup.sleep.ms";
	private static final Logger LOG = LoggerFactory.getLogger(RedisSpout.class);
	private boolean m_logging = false;

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

			TupleStatistic tupleStatistic = new TupleStatistic();
			//set realStart must happen in JsonBolt
			tupleStatistic.setPipelineStart(System.currentTimeMillis());
			collector.emit(new Values(jsonAsString, tupleStatistic));
		}
	}

	public void ack(Object msgId) {

	}

	public void fail(Object msgId) {

	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("jsonString", "tupleStatistic"));
	}

	public boolean isDistributed() {
		return false;
	}
}
