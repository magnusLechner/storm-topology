package at.lechner.quick;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import at.illecker.sentistorm.commons.dict.TwitchEmoticons;
import at.illecker.sentistorm.components.Tokenizer;
import at.lechner.commons.MyTuple;
import at.lechner.preparation.SVMPreparation;
import at.lechner.util.BasicUtil;

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
		
		JsonParser parser = new JsonParser();
		StandardDeviation std = new StandardDeviation();
		
		long globalCount = 0;
		long charCount = 0;
		int lineCounter = 0;
		int emoticonCount = 0;
		
		try (BufferedReader br = new BufferedReader(new FileReader("/home/magnus/Downloads/1_500_000_messages.txt"))) {
//		try (BufferedReader br = new BufferedReader(new FileReader("/home/magnus/Downloads/sentiment140.txt"))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	line = line.trim();
				if(line.length() == 0) {
					continue;
				}
				JsonObject jsonObject = (JsonObject) parser.parse(line);
				String msg = jsonObject.get("msg").getAsString();
				List<String> asd = Tokenizer.tokenize(msg);
				
				boolean contains = false;
				for(String s : asd) {
					if(TwitchEmoticons.getInstance().isTwitchEmoticon(s)) {
						contains = true;
					}	
				}
			
				
//				String[] splits = line.split(",");
				
				
				globalCount += asd.size();
				charCount += msg.length();
			
				if(contains) {
					emoticonCount++;
				}
				lineCounter++;
		    }
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("RESULT words in msg: " + globalCount/ (double) lineCounter);
		System.out.println("RESULT chars in msg: " + charCount/ (double) lineCounter);
		System.out.println("Res emo: " + emoticonCount);
		
		
//		long timestamp = System.currentTimeMillis();
//		JsonParser parser = new JsonParser();
//
//		JsonObject jsonObject = (JsonObject) parser.parse(MSG_FIRST_PART + timestamp + MSG_SECOND_PART);
//		System.out.println(jsonObject.toString());
//		
//		JsonObject pipeline = new JsonObject();
//		JsonObject pipelineCycle = new JsonObject();
//		pipelineCycle.addProperty(MIN, 0);
//		pipelineCycle.addProperty(MAX, 10);
//		pipelineCycle.addProperty(AVG, 5);
//		pipelineCycle.addProperty(STDDEV, 1);
//		pipeline.add(CYCLE, pipelineCycle);
//		
//		JsonObject real = new JsonObject();
//		JsonObject realCycle = new JsonObject();
//		realCycle.addProperty(MIN, 0);
//		realCycle.addProperty(MAX, 10);
//		realCycle.addProperty(AVG, 5);
//		realCycle.addProperty(STDDEV, 1);
//		real.add(CYCLE, realCycle);
//		
//		JsonObject topology = new JsonObject();
//		topology.addProperty(PROCESSED_TUPLES_COUNT, 200);
//		topology.add(PIPELINE, pipeline);
//		topology.add(REAL, real);
//
//		JsonObject json = new JsonObject();
//		json.add(TOPOLOGY, topology);
//		
//		System.out.println(json);
	}
}
