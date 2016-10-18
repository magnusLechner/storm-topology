package at.testing.labeling;

import com.google.gson.JsonObject;

import at.testing.commons.Sentiment;

public class LabelMessage {

	private JsonObject json;
	private Sentiment sentiment = null;
	
	public LabelMessage(JsonObject json) {
		super();
		this.json = json;
	}
	
	public LabelMessage(JsonObject json, Sentiment sentiment) {
		super();
		this.json = json;
		this.sentiment = sentiment;
	}
	
	public JsonObject getJson() {
		return json;
	}
	
	public Sentiment getSentiment() {
		return sentiment;
	}
	
	public void setSentiment(Sentiment sentiment) {
		this.sentiment = sentiment;
	}
	
	public String getMessage() {
		return json.get("msg").getAsString();
	}
	
}
