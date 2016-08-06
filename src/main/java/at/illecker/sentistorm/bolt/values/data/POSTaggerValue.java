package at.illecker.sentistorm.bolt.values.data;

import java.util.List;

import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import com.google.gson.JsonObject;

import cmu.arktweetnlp.Tagger.TaggedToken;

public class POSTaggerValue extends DataValue {
	private static final long serialVersionUID = -8728885794877326740L;

	private static final String TAGGED_TOKENS_ATTRIBUTE = "taggedTokens";
	
	private static final int TAGGED_TOKENS_INDEX = 2;
	
	public POSTaggerValue(JsonObject jsonObject, Object returnInfo, List<TaggedToken> taggedTokens) {
		super(jsonObject, returnInfo);
		super.add(taggedTokens);
	}
	
	public static Fields getSchema() {
		return new Fields(RETURN_INFO_ATTRIBUTE, JSON_ATTRIBUTE, TAGGED_TOKENS_ATTRIBUTE);
	}
	
	@SuppressWarnings("unchecked")
	public static POSTaggerValue getFromTuple(Tuple tuple) {
		JsonObject jsonObject = (JsonObject) tuple.getValueByField(JSON_ATTRIBUTE);
		Object returnInfo = tuple.getStringByField(RETURN_INFO_ATTRIBUTE);
		List<TaggedToken> taggedTokens = (List<TaggedToken>) tuple.getValueByField(TAGGED_TOKENS_ATTRIBUTE);
	
		return new POSTaggerValue(jsonObject, returnInfo, taggedTokens);
	}
	
	@SuppressWarnings("unchecked")
	public List<TaggedToken> getTaggedTokens() {
		return (List<TaggedToken>) super.get(TAGGED_TOKENS_INDEX);
	}
	
}
