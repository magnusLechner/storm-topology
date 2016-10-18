package at.storm.bolt.values.data;

import java.util.Map;

import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import com.google.gson.JsonObject;

import at.storm.bolt.values.statistic.tuple.TupleStatistic;

public class FeatureGenerationBoltData extends DataValue {
	private static final long serialVersionUID = -4540574656312690470L;

	private static final String FEATURE_VECTOR_ATTRIBUTE = "featureVector";

	private static final int FEATURE_VECTOR_INDEX = 2;

	public FeatureGenerationBoltData(JsonObject jsonObject, TupleStatistic tupleStatistic,
			Map<Integer, Double> featureVector) {
		super(jsonObject, tupleStatistic);
		super.add(featureVector);
	}

	public static Fields getSchema() {
		return new Fields(JSON_ATTRIBUTE, TUPLE_STATISTIC_ATTRIBUTE, FEATURE_VECTOR_ATTRIBUTE);
	}

	@SuppressWarnings("unchecked")
	public static FeatureGenerationBoltData getFromTuple(Tuple tuple) {
		JsonObject jsonObject = (JsonObject) tuple.getValueByField(JSON_ATTRIBUTE);
		TupleStatistic tupleStatistic = (TupleStatistic) tuple.getValueByField(TUPLE_STATISTIC_ATTRIBUTE);
		Map<Integer, Double> featureVector = (Map<Integer, Double>) tuple.getValueByField(FEATURE_VECTOR_ATTRIBUTE);

		return new FeatureGenerationBoltData(jsonObject, tupleStatistic, featureVector);
	}

	@SuppressWarnings("unchecked")
	public Map<Integer, Double> getFeatureVector() {
		return (Map<Integer, Double>) super.get(FEATURE_VECTOR_INDEX);
	}

}
