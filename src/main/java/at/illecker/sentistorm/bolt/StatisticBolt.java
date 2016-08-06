package at.illecker.sentistorm.bolt;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.illecker.sentistorm.commons.TopologyRawStatistic;

public class StatisticBolt extends BaseBasicBolt {
	public static final String ID = "statistic";
	public static final String CONF_LOGGING = ID + ".logging";
	public static final String CONFIG_INTERVAL = ID + ".interval";
	private static final long serialVersionUID = -1118183211053643981L;
	private static final Logger LOG = LoggerFactory.getLogger(StatisticBolt.class);
	private boolean m_logging = false;
	
	public static final String RESULT_STATISTIC_STREAM = "result-statistic-stream";
	public static final String START_STATISTIC_STREAM = "start-statistic-stream";
	public static final String END_STATISTIC_STREAM = "end-statistic-stream";

	private long last;
	private long interval;
	private List<Long> cycleTimes;
	private Map<String, String> timestamps;

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("statistic-json"));
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map config, TopologyContext context) {
		timestamps = new HashMap<String, String>();
		cycleTimes = new ArrayList<Long>();

		interval = (Long) config.get(CONFIG_INTERVAL);
		last = System.currentTimeMillis();

		// Optional set logging
		if (config.get(CONF_LOGGING) != null) {
			m_logging = (Boolean) config.get(CONF_LOGGING);
		} else {
			m_logging = false;
		}
	}

	@Override
	public void execute(Tuple tuple, BasicOutputCollector collector) {
		String sourceID = tuple.getSourceComponent();
		String id = (String) tuple.getValue(0);
		if (sourceID.startsWith(START_STATISTIC_STREAM)) {
			timestamps.put(id, (String) tuple.getValue(2));
		} else if (sourceID.startsWith(END_STATISTIC_STREAM)) {
			cycleTimes.add((Long) tuple.getValue(1));
			// long endTimestamp = Calendar.getInstance().getTimeInMillis();
			timestamps.remove(id);

			final long current = System.currentTimeMillis();

			if (current - last >= interval) {
				TopologyRawStatistic rawStatistic = new TopologyRawStatistic(cycleTimes);
				collector.emit(RESULT_STATISTIC_STREAM, rawStatistic);
				clear();
				last = current;
			}
		}

		if (m_logging) {
			LOG.info("STATISTIC-BOLT LOGGING ACTIVE");
		}
	}

	private void clear() {
		cycleTimes = new ArrayList<Long>();
	}

}
