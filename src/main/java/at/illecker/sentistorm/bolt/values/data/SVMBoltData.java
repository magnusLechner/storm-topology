package at.illecker.sentistorm.bolt.values.data;

import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import com.google.gson.JsonObject;

import at.illecker.sentistorm.bolt.values.statistic.tuple.TupleStatistic;

public class SVMBoltData extends DataValue {
	private static final long serialVersionUID = -6726509770940853896L;

	public SVMBoltData(JsonObject jsonObject, TupleStatistic tupleStatistic) {
		super(jsonObject, tupleStatistic);
	}

	public static Fields getSchema() {
		return new Fields(JSON_ATTRIBUTE, TUPLE_STATISTIC_ATTRIBUTE);
	}

	public static SVMBoltData getFromTuple(Tuple tuple) {
		JsonObject jsonObject = (JsonObject) tuple.getValueByField(JSON_ATTRIBUTE);
		TupleStatistic tupleStatistic = (TupleStatistic) tuple.getValueByField(TUPLE_STATISTIC_ATTRIBUTE);
		return new SVMBoltData(jsonObject, tupleStatistic);
	}

}
