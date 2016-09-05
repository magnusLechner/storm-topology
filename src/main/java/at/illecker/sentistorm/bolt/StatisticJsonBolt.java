package at.illecker.sentistorm.bolt;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import at.illecker.sentistorm.bolt.values.statistic.tuple.PipelineCycleTime;
import at.illecker.sentistorm.bolt.values.statistic.tuple.RealCycleTime;
import at.illecker.sentistorm.bolt.values.statistic.tuple.TupleStatistic;
import io.socket.client.IO;
import io.socket.client.Socket;

public class StatisticJsonBolt extends BaseRichBolt {
	private static final long serialVersionUID = 8434355720458732713L;
	public static final String ID = "statisticJson";
	public static final String CONF_LOGGING = ID + ".logging";
	private static final Logger LOG = LoggerFactory.getLogger(StatisticJsonBolt.class);
	private boolean m_logging;

	private static final String SOCKET_IO_IDENTIFIER = "sendSentimentStormStatistic";

	private static final String TOPOLOGY = "topology";
	private static final String PROCESSED_TUPLES_COUNT = "processedTuplesCount";
	private static final String PIPELINE = "pipeline";
	private static final String REAL = "real";
	private static final String CYCLE = "cycle";
	private static final String MIN = "min";
	private static final String MAX = "max";
	private static final String AVG = "avg";
	private static final String STDDEV = "stdDev";

	private OutputCollector collector;
	private Socket socket;
	private StandardDeviation mathStdDev;

	private boolean firstTupelFlag;

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
		firstTupelFlag = true;

		try {
			socket = IO.socket("http://eventstorm-back-sentiment-producer.os.cggstack.cheergg.com/").connect();
			LOG.error("SOCKET IO SUCCESSFULLY CONNECT");
		} catch (URISyntaxException e) {
			LOG.error("SOCKET IO COULDNT CONNECT");
			e.printStackTrace();
		}
	}

	public void execute(Tuple tuple) {
		TopologyRawStatistic rawStatistic = TopologyRawStatistic.getFromTuple(tuple);
		TopologyStatistic statistic = rawToAggregated(rawStatistic);
		JsonObject jsonObject = statisticToJson(statistic);

		if (m_logging) {
			LOG.info("STATISTIC JSON: " + jsonObject.toString());
		}

		if (!firstTupelFlag) {
//			 LOG.info("EMITTED JSON: " + jsonObject.toString());

			socket.emit(SOCKET_IO_IDENTIFIER, jsonObject.toString());
		} else {
			firstTupelFlag = false;
		}

		collector.ack(tuple);
	}

	// public TopologyStatistic rawToAggregated(TopologyRawStatistic
	// rawStatistic) {
	// double[] basicCycleAgg = {0,0,0,0};
	// return new TopologyStatistic(rawStatistic.getProcessingTuplesCount(),
	// rawStatistic.getCount(),
	// basicCycleAgg[0], basicCycleAgg[1], basicCycleAgg[2], basicCycleAgg[3]);
	// }

	public TopologyStatistic rawToAggregated(TopologyRawStatistic rawStatistic) {
		List<TupleStatistic> tupleStatistics = rawStatistic.getTupleStatistics();
		return new TopologyStatistic(tupleStatistics.size(), getPipelineCycleTime(tupleStatistics),
				getRealCylceTime(tupleStatistics));
	}

	public JsonObject statisticToJson(TopologyStatistic statistic) {
		JsonObject pipeline = new JsonObject();
		JsonObject pipelineCycle = new JsonObject();
		pipelineCycle.addProperty(MIN, statistic.getPipelineCycleTime().getMin());
		pipelineCycle.addProperty(MAX, statistic.getPipelineCycleTime().getMax());
		pipelineCycle.addProperty(AVG, statistic.getPipelineCycleTime().getAvg());
		pipelineCycle.addProperty(STDDEV, statistic.getPipelineCycleTime().getStdDev());
		pipeline.add(CYCLE, pipelineCycle);
		
		JsonObject real = new JsonObject();
		JsonObject realCycle = new JsonObject();
		realCycle.addProperty(MIN, statistic.getRealCycleTime().getMin());
		realCycle.addProperty(MAX, statistic.getRealCycleTime().getMax());
		realCycle.addProperty(AVG, statistic.getRealCycleTime().getAvg());
		realCycle.addProperty(STDDEV, statistic.getRealCycleTime().getStdDev());
		real.add(CYCLE, realCycle);
		
		JsonObject topology = new JsonObject();
		topology.addProperty(PROCESSED_TUPLES_COUNT, statistic.getProcessedTuplesCount());
		topology.add(PIPELINE, pipeline);
		topology.add(REAL, real);

		JsonObject json = new JsonObject();
		json.add(TOPOLOGY, topology);
		return json;
	}

	// TODO unify this and the method below
	private PipelineCycleTime getPipelineCycleTime(List<TupleStatistic> tupleStatistics) {
		List<Long> cycleTimes = new ArrayList<Long>();
		for (int i = 0; i < tupleStatistics.size(); i++) {
			long start = tupleStatistics.get(i).getPipelineStart();
			long end = tupleStatistics.get(i).getPipelineEnd();
			cycleTimes.add(end - start);
		}
		double[] cycleAgg = calcCycleTimeAggregations(cycleTimes);
		return new PipelineCycleTime(cycleAgg[0], cycleAgg[1], cycleAgg[2], cycleAgg[3]);
	}

	// TODO unify this and the upper method
	private RealCycleTime getRealCylceTime(List<TupleStatistic> tupleStatistics) {
		List<Long> cycleTimes = new ArrayList<Long>();
		for (int i = 0; i < tupleStatistics.size(); i++) {
			long start = tupleStatistics.get(i).getRealStart();
			long end = tupleStatistics.get(i).getRealEnd();
			cycleTimes.add(end - start);
		}
		double[] cycleAgg = calcCycleTimeAggregations(cycleTimes);
		return new RealCycleTime(cycleAgg[0], cycleAgg[1], cycleAgg[2], cycleAgg[3]);
	}

	private double[] calcCycleTimeAggregations(List<Long> values) {
		if (values.size() == 0) {
			double[] res = { 0.0, 0.0, 0.0, 0.0 };
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
		double[] res = { min, max, avg, stdDev };
		return res;
	}

}
