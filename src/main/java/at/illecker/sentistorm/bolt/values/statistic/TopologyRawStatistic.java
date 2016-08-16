package at.illecker.sentistorm.bolt.values.statistic;

import java.util.List;

import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

public class TopologyRawStatistic extends StatisticValue {
	private static final long serialVersionUID = 8591981113194353425L;

	private static final String PROCESSING_TUPLES_COUNT_ATTRIBUTE = "processing-tuples-count";
	private static final String CYCLE_TIMES_ATTRIBUTE = "cycle-times";

	private static final int PROCESSING_TUPLES_COUNT_INDEX = 0;
	private static final int CYCLE_TIMES_INDEX = 1;

//	public TopologyRawStatistic(int processingTuplesCount, int count) {
//		super();
//		super.add(PROCESSING_TUPLES_COUNT_INDEX, processingTuplesCount);
//		super.add(CYCLE_TIMES_INDEX, count);
//	}
	
	public TopologyRawStatistic(int processingTuplesCount, List<Long> cycleTimes) {
		super();
		super.add(PROCESSING_TUPLES_COUNT_INDEX, processingTuplesCount);
		super.add(CYCLE_TIMES_INDEX, cycleTimes);
	}

	public static Fields getSchema() {
		return new Fields(PROCESSING_TUPLES_COUNT_ATTRIBUTE, CYCLE_TIMES_ATTRIBUTE);
	}

//	public static TopologyRawStatistic getFromTuple(Tuple tuple) {
//		int processingTuplesCount = tuple.getIntegerByField(PROCESSING_TUPLES_COUNT_ATTRIBUTE);
//		int cycleTimes = tuple.getIntegerByField(CYCLE_TIMES_ATTRIBUTE);
//
//		return new TopologyRawStatistic(processingTuplesCount, cycleTimes);
//	}
	
	@SuppressWarnings("unchecked")
	public static TopologyRawStatistic getFromTuple(Tuple tuple) {
		int processingTuplesCount = tuple.getIntegerByField(PROCESSING_TUPLES_COUNT_ATTRIBUTE);
		List<Long> cycleTimes = (List<Long>) tuple.getValueByField(CYCLE_TIMES_ATTRIBUTE);

		return new TopologyRawStatistic(processingTuplesCount, cycleTimes);
	}

	public int getProcessingTuplesCount() {
		return (int) super.get(PROCESSING_TUPLES_COUNT_INDEX);
	}
	
	public int getProcessedTuplesCount() {
		return (int) getCycleTimes().size();
	}

//	public int getCount() {
//		return (int) super.get(CYCLE_TIMES_INDEX);
//	}
	
	@SuppressWarnings("unchecked")
	public List<Long> getCycleTimes() {
		return (List<Long>) super.get(CYCLE_TIMES_INDEX);
	}
	
}