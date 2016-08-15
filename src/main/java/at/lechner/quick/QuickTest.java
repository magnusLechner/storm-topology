package at.lechner.quick;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class QuickTest {

	private static final String MSG_FIRST_PART = "{\"msg\":\"Kreygasm\",\"user\":\"theUser\",\"channel\":\"TheChannel\",\"timestamp\":";
	private static final String MSG_SECOND_PART = "}";
	
	public static void main(String[] args) {
		
		long timestamp = System.currentTimeMillis();
		JsonParser parser = new JsonParser();
		
		for(int i = 0; i < 10; i++) {
			JsonObject jsonObject = (JsonObject)parser.parse(MSG_FIRST_PART + (timestamp + i) + MSG_SECOND_PART);		
			System.out.println(jsonObject.toString());	
		}
		
//		String json = "{\"msg\":\"good amazing Kreygasm\",\"user\":\"animayy\",\"channel\":\"biboyqg\",\"game\":\"Minecraft\",\"url\":\""
//				+ "twitch.tv/biboyqg\",\"timeStamp\":1465527600008,\"sentiment\":{\"score\""
//				+ ":0,\"comparative\":0,\"tokens\":[\"vroom\",\"vroom\",\"keemcar\",\"lool\"],\"words\":[],\"positive\":[],\""
//				+ "negative\":[]}}";
//		
//		System.out.println(json);
//		
//		JsonParser parser = new JsonParser();
//		JsonObject jsonObject = (JsonObject)parser.parse(json);
//		JsonElement msg = jsonObject.get("msg");
//		JsonElement user = jsonObject.get("user");
//		JsonElement timestamp = jsonObject.get("timeStamp");
//		
//		System.out.println("THIS IS THE MSG: " + msg.getAsString());
//		System.out.println("THIS IS THE USER: " + user.getAsString());
//		System.out.println("THIS IS THE TIMESTAMP: " + timestamp.getAsString());
//		System.out.println("THIS IS THE JSON: " + jsonObject.toString());
		
//		System.out.println(String.valueOf(Calendar.getInstance().getTimeInMillis()));
//		System.out.println(System.currentTimeMillis());
	}
}
