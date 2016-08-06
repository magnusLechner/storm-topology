package at.illecker.sentistorm.bolt.values.data;

import org.apache.storm.tuple.Tuple;

import com.google.gson.JsonObject;

public class JsonValue extends DataValue {
	private static final long serialVersionUID = -8852108291994569661L;

	public JsonValue(Object returnInfo, JsonObject jsonObject) {
		super(returnInfo, jsonObject);
	}
	
	public static JsonValue getFromTuple(Tuple tuple) {
		Object returnInfo = tuple.getStringByField(RETURN_INFO_ATTRIBUTE);
		JsonObject jsonObject = (JsonObject) tuple.getValueByField(JSON_ATTRIBUTE);
	
		return new JsonValue(returnInfo, jsonObject);
	}
	
}
