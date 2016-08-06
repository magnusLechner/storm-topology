package at.illecker.sentistorm.bolt.values.data;

import java.util.List;

import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import com.google.gson.JsonObject;

public class TokenizerValue extends DataValue {
	private static final long serialVersionUID = -3015944826822558999L;

	private static final String TOKENS_ATTRIBUTE = "tokens";
	
	private static final int TOKENS_INDEX = 2;
	
	public TokenizerValue(JsonObject jsonObject, Object returnInfo, List<String> tokens) {
		super(jsonObject, returnInfo);
		super.add(tokens);
	}

	public static Fields getSchema() {
		return new Fields(RETURN_INFO_ATTRIBUTE, JSON_ATTRIBUTE, TOKENS_ATTRIBUTE);
	}
	
	@SuppressWarnings("unchecked")
	public static TokenizerValue getFromTuple(Tuple tuple) {
		JsonObject jsonObject = (JsonObject) tuple.getValueByField(JSON_ATTRIBUTE);
		Object returnInfo = tuple.getStringByField(RETURN_INFO_ATTRIBUTE);
		List<String> tokens = (List<String>) tuple.getValueByField(TOKENS_ATTRIBUTE);
	
		return new TokenizerValue(jsonObject, returnInfo, tokens);
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getTokens() {
		return (List<String>) super.get(TOKENS_INDEX);
	}
}
