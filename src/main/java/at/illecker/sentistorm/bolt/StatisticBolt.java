package at.illecker.sentistorm.bolt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.illecker.sentistorm.bolt.values.data.RedisPublishBoltData;
import at.illecker.sentistorm.bolt.values.statistic.TopologyRawStatistic;
import at.illecker.sentistorm.bolt.values.statistic.tuple.TupleStatistic;

public class StatisticBolt extends BaseBasicBolt {
	public static final String ID = "statistic";
	public static final String CONF_LOGGING = ID + ".logging";
	public static final String CONF_INTERVAL = ID + ".interval";
	private static final long serialVersionUID = -1118183211053643981L;
	private static final Logger LOG = LoggerFactory.getLogger(StatisticBolt.class);
	private boolean m_logging = false;

	private long last;
	private long interval;
	private List<TupleStatistic> tupleStatistics = new ArrayList<TupleStatistic>();

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(TopologyRawStatistic.getSchema());
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map config, TopologyContext context) {
		this.interval = (Long) config.get(CONF_INTERVAL);

		// Optional set logging
		if (config.get(CONF_LOGGING) != null) {
			m_logging = (Boolean) config.get(CONF_LOGGING);
		} else {
			m_logging = false;
		}

		this.last = System.currentTimeMillis();
	}

	//TODO differ between pipeline and direct json-bolt stream for evaluation
	@Override
	public void execute(Tuple tuple, BasicOutputCollector collector) {
		RedisPublishBoltData redisPublishBoltData = RedisPublishBoltData.getFromTuple(tuple);
		TupleStatistic tupleStatistic = redisPublishBoltData.getTupleStatistic();

		addTupleStatistic(tupleStatistic);

		final long current = System.currentTimeMillis();
		if (current - last >= interval) {
			collector.emit(new TopologyRawStatistic(getTupleStatistics()));
			clear();
			last = current;
		}

		if (m_logging) {
			LOG.info("STATISTIC-BOLT LOGGING ACTIVE");
		}
	}

	private void clear() {
		clearTupleStatistics();
	}

	private void addTupleStatistic(TupleStatistic tupleStatistic) {
		if (tupleStatistics != null) {
			tupleStatistics.add(tupleStatistic);
		} else {
			throw new NullPointerException("Statistic Bolt: List is null");
		}
	}

	private List<TupleStatistic> getTupleStatistics() {
		return tupleStatistics;
	}

	private void clearTupleStatistics() {
		tupleStatistics = new ArrayList<TupleStatistic>();
	}

}
