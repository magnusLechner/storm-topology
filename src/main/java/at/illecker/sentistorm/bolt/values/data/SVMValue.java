package at.illecker.sentistorm.bolt.values.data;

import org.apache.storm.tuple.Fields;

import com.google.gson.JsonObject;

public class SVMValue extends DataValue {
	private static final long serialVersionUID = -6726509770940853896L;
	
	public SVMValue(Object returnInfo, JsonObject jsonObject) {
		super(returnInfo, jsonObject);
	}
	
	public static Fields getSchema() {
		return new Fields(RETURN_INFO_ATTRIBUTE, JSON_ATTRIBUTE);
	}

}
