package at.illecker.sentistorm.bolt.values.data;

import java.util.Map;

import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import com.google.gson.JsonObject;

public class FeatureGenerationValue extends DataValue {
	private static final long serialVersionUID = -4540574656312690470L;

	private static final String FEATURE_VECTOR_ATTRIBUTE = "featureVector";
	
	private static final int FEATURE_VECTOR_INDEX = 2;
	
	public FeatureGenerationValue(JsonObject jsonObject, Object returnInfo, Map<Integer, Double> featureVector) {
		super(jsonObject, returnInfo);
		super.add(featureVector);
	}
	
	public static Fields getSchema() {
		return new Fields(RETURN_INFO_ATTRIBUTE, JSON_ATTRIBUTE, FEATURE_VECTOR_ATTRIBUTE);
	}
	
	@SuppressWarnings("unchecked")
	public static FeatureGenerationValue getFromTuple(Tuple tuple) {
		JsonObject jsonObject = (JsonObject) tuple.getValueByField(JSON_ATTRIBUTE);
		Object returnInfo = tuple.getStringByField(RETURN_INFO_ATTRIBUTE);
		Map<Integer, Double> featureVector = (Map<Integer, Double>) tuple.getValueByField(FEATURE_VECTOR_ATTRIBUTE);
	
		return new FeatureGenerationValue(jsonObject, returnInfo, featureVector);
	}
	
	@SuppressWarnings("unchecked")
	public Map<Integer, Double> getFeatureVector() {
		return (Map<Integer, Double>) super.get(FEATURE_VECTOR_INDEX);
	}
	
}
