package at.illecker.sentistorm.bolt.values.data;

import org.apache.storm.tuple.Tuple;

import com.google.gson.JsonObject;

public class JsonData extends DataValue {
	private static final long serialVersionUID = -8852108291994569661L;

	public JsonData(JsonObject jsonObject, Object returnInfo) {
		super(jsonObject, returnInfo);
	}
	
	public static JsonData getFromTuple(Tuple tuple) {
		JsonObject jsonObject = (JsonObject) tuple.getValueByField(JSON_ATTRIBUTE);
		Object returnInfo = tuple.getStringByField(RETURN_INFO_ATTRIBUTE);
	
		return new JsonData(jsonObject, returnInfo);
	}
	
}
