package at.illecker.sentistorm.bolt.values.statistic;

import java.util.List;

import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import at.illecker.sentistorm.bolt.values.statistic.tuple.TupleStatistic;

public class TopologyRawStatistic extends StatisticValue {
	private static final long serialVersionUID = 8591981113194353425L;

	private static final String TUPLE_STATISTICS_ATTRIBUTE = "tuple-statistics";

	private static final int TUPLE_STATISTICS_INDEX = 0;

	public TopologyRawStatistic(List<TupleStatistic> tupleStatistics) {
		super();
		super.add(TUPLE_STATISTICS_INDEX, tupleStatistics);
	}

	public static Fields getSchema() {
		return new Fields(TUPLE_STATISTICS_ATTRIBUTE);
	}

	@SuppressWarnings("unchecked")
	public static TopologyRawStatistic getFromTuple(Tuple tuple) {
		List<TupleStatistic> tupleStatistics = (List<TupleStatistic>) tuple.getValueByField(TUPLE_STATISTICS_ATTRIBUTE);
		return new TopologyRawStatistic(tupleStatistics);
	}

	@SuppressWarnings("unchecked")
	public List<TupleStatistic> getTupleStatistics() {
		return (List<TupleStatistic>) super.get(TUPLE_STATISTICS_INDEX);
	}

}
