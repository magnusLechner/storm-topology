package at.illecker.sentistorm.bolt.values.data;

import java.io.Serializable;

import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import com.google.gson.JsonObject;

import at.illecker.sentistorm.bolt.values.statistic.tuple.TupleStatistic;

public abstract class DataValue extends Values implements Serializable {
	private static final long serialVersionUID = 4291155892871610236L;

	private static final int JSON_INDEX = 0;
	private static final int TUPLE_STATISTIC_INDEX = 1;

	public static final String JSON_ATTRIBUTE = "json";
	public static final String TUPLE_STATISTIC_ATTRIBUTE = "tupleStatistic";

	public DataValue(JsonObject jsonObject, TupleStatistic tupleStatistic) {
		super();
		super.add(JSON_INDEX, jsonObject);
		super.add(TUPLE_STATISTIC_INDEX, tupleStatistic);
	}

	public static Fields getSchema() {
		return new Fields(JSON_ATTRIBUTE, TUPLE_STATISTIC_ATTRIBUTE);
	}

	public JsonObject getJsonObject() {
		return (JsonObject) super.get(JSON_INDEX);
	}

	public TupleStatistic getTupleStatistic() {
		return (TupleStatistic) super.get(TUPLE_STATISTIC_INDEX);
	}
	
}
