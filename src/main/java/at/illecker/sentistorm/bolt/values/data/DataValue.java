package at.illecker.sentistorm.bolt.values.data;

import java.io.Serializable;

import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import com.google.gson.JsonObject;

public abstract class DataValue extends Values implements Serializable {
	private static final long serialVersionUID = 4291155892871610236L;
	
    private static final int JSON_INDEX = 0;
    
	public static final String JSON_ATTRIBUTE = "json";
	
    public DataValue(JsonObject jsonObject) {
        super();
        super.add(JSON_INDEX, jsonObject);
    }

	public static Fields getSchema() {
        return new Fields(JSON_ATTRIBUTE);
    }
    
    public JsonObject getJsonObject() {
    	return (JsonObject) super.get(JSON_INDEX);
    }
}
