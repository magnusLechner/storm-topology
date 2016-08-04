package at.illecker.sentistorm.bolt;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatisticBolt extends BaseBasicBolt {
	public static final String ID = "statistic-bolt";
	public static final String CONF_LOGGING = ID + ".logging";
	private static final long serialVersionUID = -1118183211053643981L;
	private static final Logger LOG = LoggerFactory.getLogger(StatisticBolt.class);
	private boolean m_logging = false;
	
	private Map<String, String> timestamps;
	private int count;
	
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
//		declarer.declare(new Fields("statistic-json"));
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map config, TopologyContext context) {
		 timestamps = new HashMap<String, String>();
		 count = 0;
		 
		// Optional set logging
		if (config.get(CONF_LOGGING) != null) {
			m_logging = (Boolean) config.get(CONF_LOGGING);
		} else {
			m_logging = false;
		}
	}

	@Override
	public void execute(Tuple tuple, BasicOutputCollector collector) {

		//TODO reset count after x seconds
		//TODO create json with max, min, avg, stdDev
		//TODO send to backend
		
		String id = (String) tuple.getValue(0);
		String timestamp = timestamps.get(id);
		if(timestamp == null) {
			timestamps.put(id, (String) tuple.getValue(2));
		} else {
//			long endTimestamp = Calendar.getInstance().getTimeInMillis();
			long endTimestamp =  (long) tuple.getValue(1);
			count++;
		}
		
		if(m_logging) {
			LOG.info("STATISTIC-BOLT LOGGING ACTIVE");
		}
	}

}
