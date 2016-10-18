package at.storm.bolt.values.statistic;

import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import at.storm.bolt.values.statistic.tuple.PipelineCycleTime;
import at.storm.bolt.values.statistic.tuple.RealCycleTime;

public class TopologyStatistic extends StatisticValue {
	private static final long serialVersionUID = -694924209832724027L;

	private static final String PROCESSED_TUPLES_COUNT_ATTRIBUTE = "processed-tuples-count";
	private static final String PIPELINE_CYCLE_TIME_ATTRIBUTE = "pipeline-cycle-time";
	private static final String REAL_CYCLE_TIME_ATTRIBUTE = "real-cycle-time";

	private static final int PROCESSED_TUPLES_COUNT_INDEX = 0;
	private static final int PIPELINE_CYCLE_TIME_INDEX = 1;
	private static final int REAL_CYCLE_TIME_INDEX = 2;

	public TopologyStatistic(int processedTuplesCount, PipelineCycleTime pipelineCycleTime,
			RealCycleTime realCycleTime) {
		super();
		super.add(PROCESSED_TUPLES_COUNT_INDEX, processedTuplesCount);
		super.add(PIPELINE_CYCLE_TIME_INDEX, pipelineCycleTime);
		super.add(REAL_CYCLE_TIME_INDEX, realCycleTime);
	}

	public static Fields getSchema() {
		return new Fields(PROCESSED_TUPLES_COUNT_ATTRIBUTE, PIPELINE_CYCLE_TIME_ATTRIBUTE, REAL_CYCLE_TIME_ATTRIBUTE);
	}

	public static TopologyStatistic getFromTuple(Tuple tuple) {
		int processedTuplesCount = tuple.getIntegerByField(PROCESSED_TUPLES_COUNT_ATTRIBUTE);
		PipelineCycleTime pipelineCycleTime = (PipelineCycleTime) tuple.getValueByField(PIPELINE_CYCLE_TIME_ATTRIBUTE);
		RealCycleTime realCycleTime = (RealCycleTime) tuple.getValueByField(REAL_CYCLE_TIME_ATTRIBUTE);

		return new TopologyStatistic(processedTuplesCount, pipelineCycleTime, realCycleTime);
	}

	public int getProcessedTuplesCount() {
		return (int) super.get(PROCESSED_TUPLES_COUNT_INDEX);
	}

	public PipelineCycleTime getPipelineCycleTime() {
		return (PipelineCycleTime) super.get(PIPELINE_CYCLE_TIME_INDEX);
	}

	public RealCycleTime getRealCycleTime() {
		return (RealCycleTime) super.get(REAL_CYCLE_TIME_INDEX);
	}

}
