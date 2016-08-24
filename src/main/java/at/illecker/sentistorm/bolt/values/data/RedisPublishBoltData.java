package at.illecker.sentistorm.bolt.values.data;

import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import com.google.gson.JsonObject;

import at.illecker.sentistorm.bolt.values.statistic.tuple.TupleStatistic;

public class RedisPublishBoltData extends DataValue {
	private static final long serialVersionUID = -3364461251490664049L;

	public RedisPublishBoltData(JsonObject jsonObject, TupleStatistic tupleStatistic) {
		super(jsonObject, tupleStatistic);
	}

	public static Fields getSchema() {
		return new Fields(JSON_ATTRIBUTE, TUPLE_STATISTIC_ATTRIBUTE);
	}

	public static RedisPublishBoltData getFromTuple(Tuple tuple) {
		JsonObject jsonObject = (JsonObject) tuple.getValueByField(JSON_ATTRIBUTE);
		TupleStatistic tupleStatistic = (TupleStatistic) tuple.getValueByField(TUPLE_STATISTIC_ATTRIBUTE);
		return new RedisPublishBoltData(jsonObject, tupleStatistic);
	}

}
