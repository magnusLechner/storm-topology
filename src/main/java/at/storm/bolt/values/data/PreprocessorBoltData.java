package at.storm.bolt.values.data;

import java.util.List;

import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import com.google.gson.JsonObject;

import at.storm.bolt.values.statistic.tuple.TupleStatistic;

public class PreprocessorBoltData extends DataValue {
	private static final long serialVersionUID = 4210822732548987886L;

	private static final String PREPROCESSED_TOKENS_ATTRIBUTE = "preprocessedTokens";

	private static final int PREPROCESSED_TOKENS_INDEX = 2;

	public PreprocessorBoltData(JsonObject jsonObject, TupleStatistic tupleStatistic, List<String> preprocessedTokens) {
		super(jsonObject, tupleStatistic);
		super.add(preprocessedTokens);
	}

	public static Fields getSchema() {
		return new Fields(JSON_ATTRIBUTE, TUPLE_STATISTIC_ATTRIBUTE, PREPROCESSED_TOKENS_ATTRIBUTE);
	}

	@SuppressWarnings("unchecked")
	public static PreprocessorBoltData getFromTuple(Tuple tuple) {
		JsonObject jsonObject = (JsonObject) tuple.getValueByField(JSON_ATTRIBUTE);
		TupleStatistic tupleStatistic = (TupleStatistic) tuple.getValueByField(TUPLE_STATISTIC_ATTRIBUTE);
		List<String> preprocessedTokens = (List<String>) tuple.getValueByField(PREPROCESSED_TOKENS_ATTRIBUTE);

		return new PreprocessorBoltData(jsonObject, tupleStatistic, preprocessedTokens);
	}

	@SuppressWarnings("unchecked")
	public List<String> getPreprocessedTokens() {
		return (List<String>) super.get(PREPROCESSED_TOKENS_INDEX);
	}

}
