package at.illecker.sentistorm.bolt.values.data;

import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import com.google.gson.JsonObject;

public class JsonBoltData extends DataValue {
	private static final long serialVersionUID = -8852108291994569661L;

	public JsonBoltData(JsonObject jsonObject) {
		super(jsonObject);
	}
	
	public static Fields getSchema() {
		return new Fields(JSON_ATTRIBUTE);
	}
	
	public static JsonBoltData getFromTuple(Tuple tuple) {
		JsonObject jsonObject = (JsonObject) tuple.getValueByField(JSON_ATTRIBUTE);
		return new JsonBoltData(jsonObject);
	}
	
}
