package at.illecker.sentistorm.bolt.values.data;

import java.util.Map;

import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import com.google.gson.JsonObject;

public class FeatureGenerationBoltData extends DataValue {
	private static final long serialVersionUID = -4540574656312690470L;

	private static final String FEATURE_VECTOR_ATTRIBUTE = "featureVector";
	
	private static final int FEATURE_VECTOR_INDEX = 1;
	
	public FeatureGenerationBoltData(JsonObject jsonObject, Map<Integer, Double> featureVector) {
		super(jsonObject);
		super.add(featureVector);
	}
	
	public static Fields getSchema() {
		return new Fields(JSON_ATTRIBUTE, FEATURE_VECTOR_ATTRIBUTE);
	}
	
	@SuppressWarnings("unchecked")
	public static FeatureGenerationBoltData getFromTuple(Tuple tuple) {
		JsonObject jsonObject = (JsonObject) tuple.getValueByField(JSON_ATTRIBUTE);
		Map<Integer, Double> featureVector = (Map<Integer, Double>) tuple.getValueByField(FEATURE_VECTOR_ATTRIBUTE);
	
		return new FeatureGenerationBoltData(jsonObject, featureVector);
	}
	
	@SuppressWarnings("unchecked")
	public Map<Integer, Double> getFeatureVector() {
		return (Map<Integer, Double>) super.get(FEATURE_VECTOR_INDEX);
	}
	
}
