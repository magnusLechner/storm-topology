package at.lechner.quick;

import java.net.URISyntaxException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketIOTest {
	private static final String jsonString = "{\"topology\":{\"processingTuplesCount\":0,\"processedTuplesCount\":58,\"cycle\":{\"min\":9.0,\"max\":25.0,\"avg\":11.862068965517242,\"stdDev\":2.021362020077784}}}";
	private static final String SOCKET_IO_IDENTIFIER = "sendSentimentStormStatistic";

	private static Socket socket;

	public static void main(String[] args) {
		try {
			socket = IO.socket("http://eventstorm-back-sentiment-producer.os.cggstack.cheergg.com/").connect();
			System.out.println("SOCKET IO SUCCESSFULLY CONNECT");

			JsonElement jsonElement = new JsonParser().parse(jsonString);
			JsonObject jsonObject = jsonElement.getAsJsonObject();

			socket.emit(SOCKET_IO_IDENTIFIER, jsonObject.toString());
			System.out.println("EMITTED DONE");
			
			Thread.sleep(3000);
			
			socket.close();
		} catch (URISyntaxException e) {
			System.out.println("SOCKET IO COULDNT CONNECT");
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
