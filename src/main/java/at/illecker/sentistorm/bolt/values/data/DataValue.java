package at.illecker.sentistorm.bolt.values.data;

import java.io.Serializable;

import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import com.google.gson.JsonObject;

public abstract class DataValue extends Values implements Serializable {
	private static final long serialVersionUID = 4291155892871610236L;
	
    private static final int RETURN_INFO_INDEX = 0;
    private static final int JSON_INDEX = 1;
    
	public static final String RETURN_INFO_ATTRIBUTE = "returnInfo";
	public static final String JSON_ATTRIBUTE = "json";

    public DataValue(Object returnInfo, JsonObject jsonObject) {
        super();
        super.add(RETURN_INFO_INDEX, returnInfo);
        super.add(JSON_INDEX, jsonObject);
    }

	public static Fields getSchema() {
        return new Fields(RETURN_INFO_ATTRIBUTE, JSON_ATTRIBUTE);
    }

    public Object getReturnInfo() {
        return super.get(RETURN_INFO_INDEX);
    }
    
    public JsonObject getJsonObject() {
    	return (JsonObject) super.get(JSON_INDEX);
    }
}
