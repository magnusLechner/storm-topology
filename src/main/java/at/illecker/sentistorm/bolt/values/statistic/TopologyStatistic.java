package at.illecker.sentistorm.bolt.values.statistic;

import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

public class TopologyStatistic extends StatisticValue {
	private static final long serialVersionUID = -694924209832724027L;

	private static final String PROCESSING_TUPLES_COUNT_ATTRIBUTE = "processing-tuples-count";
	private static final String PROCESSED_TUPLES_COUNT_ATTRIBUTE = "processed-tuples-count";
	private static final String CYCLE_TIME_MIN_ATTRIBUTE = "cycle-times-min";
	private static final String CYCLE_TIME_MAX_ATTRIBUTE = "cycle-times-max";
	private static final String CYCLE_TIME_AVG_ATTRIBUTE = "cycle-times-avg";
	private static final String CYCLE_TIME_STDDEV_ATTRIBUTE = "cycle-times-stdDev";

	private static final int PROCESSING_TUPLES_COUNT_INDEX = 0;
	private static final int PROCESSED_TUPLES_COUNT_INDEX = 1;
	private static final int CYCLE_TIME_MIN_INDEX = 2;
	private static final int CYCLE_TIME_MAX_INDEX = 3;
	private static final int CYCLE_TIME_AVG_INDEX = 4;
	private static final int CYCLE_TIME_STDDEV_INDEX = 5;

	public TopologyStatistic(int processingTuplesCount, int processedTuplesCount, double cycleTimeMin,
			double cycleTimeMax, double cycleTimeAvg, double cycleTimeStdDev) {
		super();
		super.add(PROCESSING_TUPLES_COUNT_INDEX, processingTuplesCount);
		super.add(PROCESSED_TUPLES_COUNT_INDEX, processedTuplesCount);
		super.add(CYCLE_TIME_MIN_INDEX, cycleTimeMin);
		super.add(CYCLE_TIME_MAX_INDEX, cycleTimeMax);
		super.add(CYCLE_TIME_AVG_INDEX, cycleTimeAvg);
		super.add(CYCLE_TIME_STDDEV_INDEX, cycleTimeStdDev);
	}

	public static Fields getSchema() {
		return new Fields(PROCESSING_TUPLES_COUNT_ATTRIBUTE, PROCESSED_TUPLES_COUNT_ATTRIBUTE, CYCLE_TIME_MIN_ATTRIBUTE,
				CYCLE_TIME_MAX_ATTRIBUTE, CYCLE_TIME_AVG_ATTRIBUTE, CYCLE_TIME_STDDEV_ATTRIBUTE);
	}

	public static TopologyStatistic getFromTuple(Tuple tuple) {
		int processingTuplesCount = (int) tuple.getValueByField(PROCESSING_TUPLES_COUNT_ATTRIBUTE);
		int processedTuplesCount = (int) tuple.getValueByField(PROCESSED_TUPLES_COUNT_ATTRIBUTE);
		double cycleTimeMin = (double) tuple.getValueByField(CYCLE_TIME_MIN_ATTRIBUTE);
		double cycleTimeMax = (double) tuple.getValueByField(CYCLE_TIME_MAX_ATTRIBUTE);
		double cycleTimeAvg = (double) tuple.getValueByField(CYCLE_TIME_AVG_ATTRIBUTE);
		double cycleTimeStdDev = (double) tuple.getValueByField(CYCLE_TIME_STDDEV_ATTRIBUTE);

		return new TopologyStatistic(processingTuplesCount, processedTuplesCount, cycleTimeMin, cycleTimeMax,
				cycleTimeAvg, cycleTimeStdDev);
	}

	public int getProcessingTuplesCount() {
		return (int) super.get(PROCESSING_TUPLES_COUNT_INDEX);
	}

	public int getProcessedTuplesCount() {
		return (int) super.get(PROCESSED_TUPLES_COUNT_INDEX);
	}
	
	public double getCycleTimeMin() {
		return (double) super.get(CYCLE_TIME_MIN_INDEX);
	}

	public double getCycleTimeMax() {
		return (double) super.get(CYCLE_TIME_MAX_INDEX);
	}

	public double getCycleTimeAvg() {
		return (double) super.get(CYCLE_TIME_AVG_INDEX);
	}

	public double getCycleTimeStdDev() {
		return (double) super.get(CYCLE_TIME_STDDEV_INDEX);
	}

}
