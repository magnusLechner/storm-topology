package at.storm.bolt.values.data;

import java.util.List;

import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import com.google.gson.JsonObject;

import at.storm.bolt.values.statistic.tuple.TupleStatistic;

public class TokenizerBoltData extends DataValue {
	private static final long serialVersionUID = -3015944826822558999L;

	private static final String TOKENS_ATTRIBUTE = "tokens";

	private static final int TOKENS_INDEX = 2;

	public TokenizerBoltData(JsonObject jsonObject, TupleStatistic tupleStatistic, List<String> tokens) {
		super(jsonObject, tupleStatistic);
		super.add(tokens);
	}

	public static Fields getSchema() {
		return new Fields(JSON_ATTRIBUTE, TUPLE_STATISTIC_ATTRIBUTE, TOKENS_ATTRIBUTE);
	}

	@SuppressWarnings("unchecked")
	public static TokenizerBoltData getFromTuple(Tuple tuple) {
		JsonObject jsonObject = (JsonObject) tuple.getValueByField(JSON_ATTRIBUTE);
		TupleStatistic tupleStatistic = (TupleStatistic) tuple.getValueByField(TUPLE_STATISTIC_ATTRIBUTE);
		List<String> tokens = (List<String>) tuple.getValueByField(TOKENS_ATTRIBUTE);

		return new TokenizerBoltData(jsonObject, tupleStatistic, tokens);
	}

	@SuppressWarnings("unchecked")
	public List<String> getTokens() {
		return (List<String>) super.get(TOKENS_INDEX);
	}
}
