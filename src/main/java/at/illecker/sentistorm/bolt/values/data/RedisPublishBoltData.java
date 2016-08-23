package at.illecker.sentistorm.bolt.values.data;

import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import com.google.gson.JsonObject;

public class RedisPublishBoltData extends DataValue {
	private static final long serialVersionUID = -3364461251490664049L;

	public RedisPublishBoltData(JsonObject jsonObject) {
		super(jsonObject);
	}

	public static Fields getSchema() {
		return new Fields(JSON_ATTRIBUTE);
	}

	public static RedisPublishBoltData getFromTuple(Tuple tuple) {
		JsonObject jsonObject = (JsonObject) tuple.getValueByField(JSON_ATTRIBUTE);
		return new RedisPublishBoltData(jsonObject);
	}
	
}