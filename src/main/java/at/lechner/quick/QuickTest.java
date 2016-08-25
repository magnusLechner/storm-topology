package at.lechner.quick;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class QuickTest {

	private static final String MSG_FIRST_PART = "{\"msg\":\"Kreygasm\",\"user\":\"theUser\",\"channel\":\"TheChannel\",\"timestamp\":";
	private static final String MSG_SECOND_PART = "}";

	private static final String TOPOLOGY = "topology";
	private static final String PROCESSED_TUPLES_COUNT = "processedTuplesCount";
	private static final String PIPELINE = "pipeline";
	private static final String REAL = "real";
	private static final String CYCLE = "cycle";
	private static final String MIN = "min";
	private static final String MAX = "max";
	private static final String AVG = "avg";
	private static final String STDDEV = "stdDev";
	
	public static void main(String[] args) {

		long timestamp = System.currentTimeMillis();
		JsonParser parser = new JsonParser();

		JsonObject jsonObject = (JsonObject) parser.parse(MSG_FIRST_PART + timestamp + MSG_SECOND_PART);
		System.out.println(jsonObject.toString());
		
		JsonObject pipeline = new JsonObject();
		JsonObject pipelineCycle = new JsonObject();
		pipelineCycle.addProperty(MIN, 0);
		pipelineCycle.addProperty(MAX, 10);
		pipelineCycle.addProperty(AVG, 5);
		pipelineCycle.addProperty(STDDEV, 1);
		pipeline.add(CYCLE, pipelineCycle);
		
		JsonObject real = new JsonObject();
		JsonObject realCycle = new JsonObject();
		realCycle.addProperty(MIN, 0);
		realCycle.addProperty(MAX, 10);
		realCycle.addProperty(AVG, 5);
		realCycle.addProperty(STDDEV, 1);
		real.add(CYCLE, realCycle);
		
		JsonObject topology = new JsonObject();
		topology.addProperty(PROCESSED_TUPLES_COUNT, 200);
		topology.add(PIPELINE, pipeline);
		topology.add(REAL, real);

		JsonObject json = new JsonObject();
		json.add(TOPOLOGY, topology);
		
		System.out.println(json);
	}
}
