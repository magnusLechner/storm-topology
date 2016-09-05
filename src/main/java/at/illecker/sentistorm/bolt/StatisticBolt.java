package at.illecker.sentistorm.bolt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.storm.state.KeyValueState;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseStatefulBolt;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.illecker.sentistorm.bolt.values.data.RedisPublishBoltData;
import at.illecker.sentistorm.bolt.values.statistic.TopologyRawStatistic;
import at.illecker.sentistorm.bolt.values.statistic.tuple.TupleStatistic;

public class StatisticBolt extends BaseStatefulBolt<KeyValueState<String, List<TupleStatistic>>> {
	public static final String ID = "statistic";
	public static final String CONF_LOGGING = ID + ".logging";
	public static final String CONF_INTERVAL = ID + ".interval";
	private static final long serialVersionUID = -1118183211053643981L;
	private static final Logger LOG = LoggerFactory.getLogger(StatisticBolt.class);
	private boolean m_logging = false;

	private static final String TUPLE_STATISTICS = "tuple-statistics";

	private long last;
	private long interval;
	private OutputCollector collector;
	private KeyValueState<String, List<TupleStatistic>> state;

	@Override
	public void initState(KeyValueState<String, List<TupleStatistic>> state) {
		this.state = state;
		this.state.put(TUPLE_STATISTICS, new ArrayList<TupleStatistic>());
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(TopologyRawStatistic.getSchema());
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map config, TopologyContext context, OutputCollector collector) {
		this.collector = collector;
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
	public void execute(Tuple tuple) {
		RedisPublishBoltData redisPublishBoltData = RedisPublishBoltData.getFromTuple(tuple);
		TupleStatistic tupleStatistic = redisPublishBoltData.getTupleStatistic();

		addTupleStatistic(tupleStatistic);

		final long current = System.currentTimeMillis();
		if (current - last >= interval) {
			collector.emit(tuple, new TopologyRawStatistic(getTupleStatistics()));
			clear();
			last = current;
		}

		if (m_logging) {
			LOG.info("STATISTIC-BOLT LOGGING ACTIVE");
		}

		collector.ack(tuple);
	}

	private void clear() {
		clearTupleStatistics();
	}

	private void addTupleStatistic(TupleStatistic tupleStatistic) {
		List<TupleStatistic> tupleStatistics = state.get(TUPLE_STATISTICS);
		if (tupleStatistics != null) {
			tupleStatistics.add(tupleStatistic);
		} else {
			throw new NullPointerException("Statistic Bolt: List is null");
		}
		state.put(TUPLE_STATISTICS, tupleStatistics);
	}

	private List<TupleStatistic> getTupleStatistics() {
		return state.get(TUPLE_STATISTICS);
	}

	private void clearTupleStatistics() {
		state.put(TUPLE_STATISTICS, new ArrayList<TupleStatistic>());
	}

}
