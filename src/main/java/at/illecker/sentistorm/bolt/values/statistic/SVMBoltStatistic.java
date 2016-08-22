package at.illecker.sentistorm.bolt.values.statistic;

import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

public class SVMBoltStatistic extends StatisticValue {
	private static final long serialVersionUID = -7221624576546859291L;

	private static final String ID_ATTRIBUTE = "id";
	private static final String TIMESTAMP_ATTRIBUTE = "timestamp";

	private static final int ID_INDEX = 0;
	private static final int TIMESTAMP_INDEX = 1;

	public SVMBoltStatistic(String id, long timestamp) {
		super();
		super.add(ID_INDEX, id);
		super.add(TIMESTAMP_INDEX, timestamp);
	}

	public static Fields getSchema() {
		return new Fields(ID_ATTRIBUTE, TIMESTAMP_ATTRIBUTE);
	}

	public static SVMBoltStatistic getFromTuple(Tuple tuple) {
		String id = tuple.getStringByField(ID_ATTRIBUTE);
		long timestamp = tuple.getLongByField(TIMESTAMP_ATTRIBUTE);
		
		return new SVMBoltStatistic(id, timestamp);
	}

	public String getID() {
		return (String) super.get(ID_INDEX);
	}

	public long getTimestamp() {
		return (long) super.get(TIMESTAMP_INDEX);
	}
	
}
