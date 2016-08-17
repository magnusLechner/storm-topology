package at.illecker.sentistorm.bolt;

import java.util.ArrayList;
import java.util.HashMap;
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

import at.illecker.sentistorm.bolt.values.statistic.JsonStatistic;
import at.illecker.sentistorm.bolt.values.statistic.SVMStatistic;
import at.illecker.sentistorm.bolt.values.statistic.TopologyRawStatistic;

public class StatisticBolt extends BaseStatefulBolt<KeyValueState<String, Object>> {
	public static final String ID = "statistic";
	public static final String CONF_LOGGING = ID + ".logging";
	public static final String CONF_INTERVAL = ID + ".interval";
	private static final long serialVersionUID = -1118183211053643981L;
	private static final Logger LOG = LoggerFactory.getLogger(StatisticBolt.class);
	private boolean m_logging = false;

	private static final String PROCESSING_TUPELS = "processing-tupels";
	private static final String CYCLE_TIMES = "cycle-time";

	private long last;
	private long interval;
	private OutputCollector collector;
	private KeyValueState<String, Object> state;

	@Override
	public void initState(KeyValueState<String, Object> state) {
		this.state = state;
		this.state.put(CYCLE_TIMES, new ArrayList<Long>());
		this.state.put(PROCESSING_TUPELS, new HashMap<String, Long>());
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

	// TODO if cycle time is not so important: set a long at index 2: every time
	// i get a json-bolt tuple i can increase the long
	// ,every time i get a svm tuple decrease the long -> count of tupels in
	// topology
	@Override
	public void execute(Tuple tuple) {
		String sourceID = tuple.getSourceComponent();

		if (sourceID.startsWith(JsonBolt.ID)) {
			JsonStatistic jsonStatistic = JsonStatistic.getFromTuple(tuple);
			addProcessingTuple(jsonStatistic.getID(), jsonStatistic.getTimestamp());
		} else if (sourceID.startsWith(SVMBolt.ID)) {
			SVMStatistic svmStatistic = SVMStatistic.getFromTuple(tuple);
//			Long startTimestamp = getStartTime(svmStatistic.getID());
//			removeProcessingTuple(svmStatistic.getID());
			Long startTimestamp = getStartTimeAndRemove(svmStatistic.getID());
			if(startTimestamp != null) {
				addCycleTime(svmStatistic.getTimestamp() - startTimestamp);	
			}
			final long current = System.currentTimeMillis();
			if (current - last >= interval) {
				collector.emit(tuple, new TopologyRawStatistic(getProcessingTuplesCount(), getCycleTimes()));
				clear();
				last = current;
			}
		}

		if (m_logging) {
			LOG.info("STATISTIC-BOLT LOGGING ACTIVE");
		}

		collector.ack(tuple);
	}

	private void clear() {
		clearCycleTimes();
	}

	@SuppressWarnings("unchecked")
	private void removeProcessingTuple(String id) {
		Map<String, Long> processingTupels = (Map<String, Long>) state.get(PROCESSING_TUPELS);
		processingTupels.remove(id);
		state.put(PROCESSING_TUPELS, processingTupels);
	}

	@SuppressWarnings("unchecked")
	private void addProcessingTuple(String id, Long timestamp) {
		Map<String, Long> processingTupels = (Map<String, Long>) state.get(PROCESSING_TUPELS);
		processingTupels.put(id, timestamp);
		state.put(PROCESSING_TUPELS, processingTupels);
	}

	@SuppressWarnings("unchecked")
	private int getProcessingTuplesCount() {
		Map<String, Long> processingTupels = (Map<String, Long>) state.get(PROCESSING_TUPELS);
		return processingTupels.size();
	}

	@SuppressWarnings("unchecked")
	private void addCycleTime(Long cycleTime) {
		List<Long> cycleTimes = (List<Long>) state.get(CYCLE_TIMES);
		cycleTimes.add(cycleTime);
		state.put(CYCLE_TIMES, cycleTimes);
	}

	@SuppressWarnings("unchecked")
	private Long getStartTime(String id) {
		Map<String, Long> processingTupels = (Map<String, Long>) state.get(PROCESSING_TUPELS);
		return processingTupels.get(id);
	}
	
	@SuppressWarnings("unchecked")
	private Long getStartTimeAndRemove(String id) {
		Map<String, Long> processingTupels = (Map<String, Long>) state.get(PROCESSING_TUPELS);
		Long startTime = processingTupels.get(id);
		processingTupels.remove(id);
		state.put(PROCESSING_TUPELS, processingTupels);
		return startTime;
	}

	@SuppressWarnings("unchecked")
	private List<Long> getCycleTimes() {
		return (List<Long>) state.get(CYCLE_TIMES);
	}

	private void clearCycleTimes() {
		state.put(CYCLE_TIMES, new ArrayList<Long>());
	}

}
