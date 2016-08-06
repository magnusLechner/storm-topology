package at.illecker.sentistorm.bolt.values.data;

import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import com.google.gson.JsonObject;

public class SVMData extends DataValue {
	private static final long serialVersionUID = -6726509770940853896L;
	
	public SVMData(JsonObject jsonObject, Object returnInfo) {
		super(jsonObject, returnInfo);
	}
	
	public static Fields getSchema() {
		return new Fields(JSON_ATTRIBUTE, RETURN_INFO_ATTRIBUTE);
	}

	public static Values modifyForReturnResult(SVMData svmData) {
		return new Values(svmData.getJsonObject().toString(), svmData.getReturnInfo());
	}
	
}
