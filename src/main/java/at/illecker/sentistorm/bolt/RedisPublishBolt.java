package at.illecker.sentistorm.bolt;

import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	public RedisPublishBolt(String host, int port, String channel) {
		this.host = host;
		this.port = port;
		this.channel = channel;
	}

	@SuppressWarnings("rawtypes")
	public void prepare(Map config, TopologyContext context, OutputCollector collector) {
		if (config.get(CONF_LOGGING) != null) {
			m_logging = (Boolean) config.get(CONF_LOGGING);
		} else {
			m_logging = false;
		}
		
		this.collector = collector;
		pool = new JedisPool(new JedisPoolConfig(), host + ":" + port);
	}

	public void execute(Tuple tuple) {
		String current = tuple.getString(0);
		if (current != null) {
			
			if(m_logging) {
				LOG.info("PUBLISHED:  " + current);
			}
			
			publish(current);
			collector.emit(tuple, new Values(current));
			collector.ack(tuple);
		}
	}

	public void cleanup() {
		if (pool != null) {
			pool.destroy();
		}
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields(channel));
	}

	public void publish(String msg) {
		Jedis jedis = pool.getResource();
		jedis.publish(channel, msg);
		jedis.close();
	}

	protected void setupNonSerializableAttributes() {

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map getComponentConfiguration() {
		return null;
	}
}
