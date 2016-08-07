package at.illecker.sentistorm.bolt.values.statistic;

import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import at.illecker.sentistorm.commons.TopologyRawStatistic;

public class OverallStatistic extends StatisticValue {
	private static final long serialVersionUID = 8591981113194353425L;

	private static final String TOPOLOGY_RAW_STATISTIC_ATTRIBUTE = "topology-raw-statistic";

	private static final int TOPOLOGY_RAW_STATISTIC_INDEX = 0;

	public OverallStatistic(TopologyRawStatistic topologyRawStatistic) {
		super();
		super.add(TOPOLOGY_RAW_STATISTIC_INDEX, topologyRawStatistic);
	}

	public static Fields getSchema() {
		return new Fields(TOPOLOGY_RAW_STATISTIC_ATTRIBUTE);
	}

	public static OverallStatistic getFromTuple(Tuple tuple) {
		TopologyRawStatistic topologyRawStatistic = (TopologyRawStatistic) tuple
				.getValueByField(TOPOLOGY_RAW_STATISTIC_ATTRIBUTE);

		return new OverallStatistic(topologyRawStatistic);
	}

	public TopologyRawStatistic getTopologyRawStatistic() {
		return (TopologyRawStatistic) super.get(TOPOLOGY_RAW_STATISTIC_INDEX);
	}

}
