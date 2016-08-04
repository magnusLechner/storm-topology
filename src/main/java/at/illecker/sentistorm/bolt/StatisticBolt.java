package at.illecker.sentistorm.bolt;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.socket.client.IO;
import io.socket.client.Socket;

public class StatisticBolt extends BaseBasicBolt {
	public static final String ID = "statistic-bolt";
	public static final String CONF_LOGGING = ID + ".logging";
	private static final long serialVersionUID = -1118183211053643981L;
	private static final Logger LOG = LoggerFactory.getLogger(StatisticBolt.class);
	private boolean m_logging = false;
	
	private Map<String, String> timestamps;
	private List<Long> cycleTimes;
	private Socket socket;
	
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
//		declarer.declare(new Fields("statistic-json"));
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map config, TopologyContext context) {
		 timestamps = new HashMap<String, String>();
		 cycleTimes = new ArrayList<Long>();
		 try {
			socket = IO.socket("eventstorm-back-sentiment-producer.os.cggstack.cheergg.com");
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		// Optional set logging
		if (config.get(CONF_LOGGING) != null) {
			m_logging = (Boolean) config.get(CONF_LOGGING);
		} else {
			m_logging = false;
		}
	}

	@Override
	public void execute(Tuple tuple, BasicOutputCollector collector) {

		//TODO reset list after x seconds
		//TODO create json with max, min, avg, stdDev, count
		//TODO send to backend
		
		String id = (String) tuple.getValue(0);
		String timestamp = timestamps.get(id);
		if(timestamp == null) {
			timestamps.put(id, (String) tuple.getValue(2));
		} else {
//			long endTimestamp = Calendar.getInstance().getTimeInMillis();
			cycleTimes.add((Long) tuple.getValue(1));
			JSONObject obj = new JSONObject();
			try {
				obj.put("hello", "server");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			socket.emit("receiveSentimentStormStatistic", obj);
		}
		
		if(m_logging) {
			LOG.info("STATISTIC-BOLT LOGGING ACTIVE");
		}
	}

}
