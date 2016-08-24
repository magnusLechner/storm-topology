package at.illecker.sentistorm.bolt.values.data;

import java.util.List;

import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import com.google.gson.JsonObject;

import at.illecker.sentistorm.bolt.values.statistic.tuple.TupleStatistic;
import cmu.arktweetnlp.Tagger.TaggedToken;

public class POSTaggerBoltData extends DataValue {
	private static final long serialVersionUID = -8728885794877326740L;

	private static final String TAGGED_TOKENS_ATTRIBUTE = "taggedTokens";

	private static final int TAGGED_TOKENS_INDEX = 2;

	public POSTaggerBoltData(JsonObject jsonObject, TupleStatistic tupleStatistic, List<TaggedToken> taggedTokens) {
		super(jsonObject, tupleStatistic);
		super.add(taggedTokens);
	}

	public static Fields getSchema() {
		return new Fields(JSON_ATTRIBUTE, TUPLE_STATISTIC_ATTRIBUTE, TAGGED_TOKENS_ATTRIBUTE);
	}

	@SuppressWarnings("unchecked")
	public static POSTaggerBoltData getFromTuple(Tuple tuple) {
		JsonObject jsonObject = (JsonObject) tuple.getValueByField(JSON_ATTRIBUTE);
		TupleStatistic tupleStatistic = (TupleStatistic) tuple.getValueByField(TUPLE_STATISTIC_ATTRIBUTE);
		List<TaggedToken> taggedTokens = (List<TaggedToken>) tuple.getValueByField(TAGGED_TOKENS_ATTRIBUTE);

		return new POSTaggerBoltData(jsonObject, tupleStatistic, taggedTokens);
	}

	@SuppressWarnings("unchecked")
	public List<TaggedToken> getTaggedTokens() {
		return (List<TaggedToken>) super.get(TAGGED_TOKENS_INDEX);
	}

}
