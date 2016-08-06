package at.illecker.sentistorm.bolt.values.data;

import java.util.List;

import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import com.google.gson.JsonObject;

public class PreprocessorValue extends DataValue {
	private static final long serialVersionUID = 4210822732548987886L;
	
	private static final String PREPROCESSED_TOKENS_ATTRIBUTE = "preprocessedTokens";
	
	private static final int PREPROCESSED_TOKENS_INDEX = 2;
	
	public PreprocessorValue(JsonObject jsonObject, Object returnInfo, List<String> preprocessedTokens) {
		super(jsonObject, returnInfo);
		super.add(preprocessedTokens);
	}

	public static Fields getSchema() {
		return new Fields(RETURN_INFO_ATTRIBUTE, JSON_ATTRIBUTE, PREPROCESSED_TOKENS_ATTRIBUTE);
	}
	
	@SuppressWarnings("unchecked")
	public static PreprocessorValue getFromTuple(Tuple tuple) {
		JsonObject jsonObject = (JsonObject) tuple.getValueByField(JSON_ATTRIBUTE);
		Object returnInfo = tuple.getStringByField(RETURN_INFO_ATTRIBUTE);
		List<String> preprocessedTokens = (List<String>) tuple.getValueByField(PREPROCESSED_TOKENS_ATTRIBUTE);
	
		return new PreprocessorValue(jsonObject, returnInfo, preprocessedTokens);
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getPreprocessedTokens() {
		return (List<String>) super.get(PREPROCESSED_TOKENS_INDEX);
	}
	
}
