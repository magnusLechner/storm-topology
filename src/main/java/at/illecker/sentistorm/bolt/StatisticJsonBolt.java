package at.illecker.sentistorm.bolt;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import at.illecker.sentistorm.bolt.values.statistic.TopologyRawStatistic;
import at.illecker.sentistorm.bolt.values.statistic.TopologyStatistic;
import io.socket.client.IO;
import io.socket.client.Socket;

public class StatisticJsonBolt extends BaseRichBolt {
	private static final long serialVersionUID = 8434355720458732713L;
	public static final String ID = "statisticJson";
	public static final String CONF_LOGGING = ID + ".logging";
	private static final Logger LOG = LoggerFactory.getLogger(StatisticJsonBolt.class);
	private boolean m_logging;

	private static final String SOCKET_IO_IDENTIFIER = "sendSentimentStormStatistic";

	// TODO
	private static final String TOPOLOGY = "topology";
	private static final String CYCLE = "cycle";
	private static final String COUNT = "count";
	private static final String MIN = "min";
	private static final String MAX = "max";
	private static final String AVG = "avg";
	private static final String STDDEV = "stdDev";

	private OutputCollector collector;
	private Socket socket;
	private StandardDeviation mathStdDev;

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// nothing here - result send in execute per socket.io to back-end
	}

	@SuppressWarnings("rawtypes")
	public void prepare(Map config, TopologyContext context, OutputCollector collector) {
		// Optional set logging
		if (config.get(CONF_LOGGING) != null) {
			m_logging = (Boolean) config.get(CONF_LOGGING);
		} else {
			m_logging = false;
		}

		this.collector = collector;
		mathStdDev = new StandardDeviation();

		try {
			socket = IO.socket("http://eventstorm-back-sentiment-producer.os.cggstack.cheergg.com/").connect();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public void execute(Tuple tuple) {
		TopologyRawStatistic rawStatistic = TopologyRawStatistic.getFromTuple(tuple);
		TopologyStatistic statistic = rawToAggregated(rawStatistic);
		JsonObject jsonObject = statisticToJson(statistic);

		if (m_logging) {
			LOG.info(jsonObject.toString());
		}

		socket.emit(SOCKET_IO_IDENTIFIER, jsonObject.toString());

		collector.ack(tuple);
	}

	public TopologyStatistic rawToAggregated(TopologyRawStatistic rawStatistic) {
		double[] basicCycleAgg = calcBasicAggregations(rawStatistic.getCycleTimes());
		return new TopologyStatistic(rawStatistic.getProcessingTuplesCount(), basicCycleAgg[0], basicCycleAgg[1],
				basicCycleAgg[2], basicCycleAgg[3]);
	}

	public JsonObject statisticToJson(TopologyStatistic statistic) {
		JsonObject obj = new JsonObject();
		// TODO
		final JsonObject cycleJsonObject = new JsonObject();
		// cycleJsonObject.addProperty(MAX, check(cycleValues.getMax()));
		// cycleJsonObject.addProperty(MEAN, check(cycleValues.getMean()));
		// cycleJsonObject.addProperty(MIN, check(cycleValues.getMin()));
		// cycleJsonObject.addProperty(DRV, check(cycleValues.getDrv()));
		return obj;
	}

	private double[] calcBasicAggregations(List<Long> values) {
		if(values.size() == 0) {
			double[] res = {0.0, 0.0, 0.0, 0.0};
			return res;
		}
		double[] tmp = new double[values.size()];
		double max = Double.NEGATIVE_INFINITY;
		double min = Double.POSITIVE_INFINITY;
		double sum = 0.0;
		for (int i = 0; i < values.size(); i++) {
			if (values.get(i) > max)
				max = values.get(i);
			if (values.get(i) < min)
				min = values.get(i);
			sum += values.get(i);
			tmp[i] = (double) values.get(i);
		}
		double avg = sum / values.size();
		double stdDev = mathStdDev.evaluate(tmp);
		double[] res = {min, max, avg, stdDev};
		return res;
	}
}
