package at.lechner.quick;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class QuickTest {

	public static void main(String[] args) {
		String json = "{\"msg\":\"good amazing Kreygasm\",\"user\":\"animayy\",\"channel\":\"biboyqg\",\"game\":\"Minecraft\",\"url\":\""
				+ "twitch.tv/biboyqg\",\"timeStamp\":1465527600008,\"sentiment\":{\"score\""
				+ ":0,\"comparative\":0,\"tokens\":[\"vroom\",\"vroom\",\"keemcar\",\"lool\"],\"words\":[],\"positive\":[],\""
				+ "negative\":[]}}";
		
		System.out.println(json);
		
		JsonParser parser = new JsonParser();
		JsonObject jsonObject = (JsonObject)parser.parse(json);
		JsonElement content = jsonObject.get("msg");
		
		System.out.println("THIS IS THE CONTENT: " + content.getAsString());
		System.out.println("THIS IS THE JSON: " + jsonObject.toString());
	}
}
