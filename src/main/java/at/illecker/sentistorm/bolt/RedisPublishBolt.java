package at.illecker.sentistorm.bolt;

import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import at.illecker.sentistorm.bolt.values.data.SVMBoltData;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisPublishBolt implements IRichBolt {
	public static final String ID = "redis-publish-bolt";
	public static final String CONF_LOGGING = ID + ".logging";
	private static final long serialVersionUID = -3645873488892922627L;
	private static final Logger LOG = LoggerFactory.getLogger(RedisPublishBolt.class);

	private boolean m_logging = false;
	private OutputCollector collector;
	private JedisPool pool;
	private String host;
	private int port;
	private String channel;
	private Gson gson;

	public RedisPublishBolt(String host, int port, String channel) {
		this.host = host;
		this.port = port;
		this.channel = channel;
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// empty
	}

	@SuppressWarnings("rawtypes")
	public void prepare(Map config, TopologyContext context, OutputCollector collector) {
		if (config.get(CONF_LOGGING) != null) {
			m_logging = (Boolean) config.get(CONF_LOGGING);
		} else {
			m_logging = false;
		}

		this.gson = new Gson();
		this.collector = collector;
		pool = new JedisPool(new JedisPoolConfig(), host, port);
	}

	public void execute(Tuple tuple) {
		String current = generatePublishString(SVMBoltData.getFromTuple(tuple).getJsonObject());
		if (current != null) {

			if (m_logging) {
				LOG.info("PUBLISHED:  " + current);
			}

			publish(current);
			// collector.emit ... no emitting here
			collector.ack(tuple);
		}
	}

	private String generatePublishString(JsonObject payloadJsonObject) {
		JsonObject jsonObject = new JsonObject();

		JsonObject headersJsonObject = new JsonObject();
		String channel = payloadJsonObject.get("channel").getAsString();
		String uid = payloadJsonObject.get("_uid").getAsString();

		headersJsonObject.addProperty("cheergg-stomp-destination", "loudr.twitch." + channel + ".storm.sentiment");
		headersJsonObject.addProperty("cheergg-public-uniqueMsgId", uid);

		jsonObject.add("headers", headersJsonObject);
		jsonObject.add("payload", payloadJsonObject);

		return gson.toJson(jsonObject);
	}

	public void publish(String toBePublished) {
		Jedis jedis = pool.getResource();
		jedis.publish(channel, toBePublished);
		if(jedis != null) {
			jedis.close();	
		}
	}

	public void cleanup() {
		if (pool != null) {
			pool.destroy();
		}
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return null;
	}

}
