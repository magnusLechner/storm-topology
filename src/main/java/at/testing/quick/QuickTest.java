package at.testing.quick;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import at.storm.commons.dict.TwitchEmoticons;
import at.storm.commons.util.io.FileUtils;
import at.storm.components.Tokenizer;
import at.testing.commons.MyTuple;
import at.testing.preparation.SVMPreparation;
import at.testing.util.BasicUtil;

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
		String file = "/home/magnus/workspace/storm-topology/src/main/resources/dictionaries/sentiment/emoticon-sentiment/emoticons-lines.txt";
		Map<String, Double> wordList = FileUtils.readFile(file, "\t", false, true, -1.0, 1.0);
	}
	
	
//	public static void main(String[] args) {
//		
//		JsonParser parser = new JsonParser();
//		StandardDeviation std = new StandardDeviation();
//		
//		long globalCount = 0;
//		long charCount = 0;
//		int lineCounter = 0;
//		int emoticonCount = 0;
//		
//		try (BufferedReader br = new BufferedReader(new FileReader("/home/magnus/Downloads/1_500_000_messages.txt"))) {
////		try (BufferedReader br = new BufferedReader(new FileReader("/home/magnus/Downloads/sentiment140.txt"))) {
//		    String line;
//		    while ((line = br.readLine()) != null) {
//		    	line = line.trim();
//				if(line.length() == 0) {
//					continue;
//				}
//				JsonObject jsonObject = (JsonObject) parser.parse(line);
//				String msg = jsonObject.get("msg").getAsString();
//				List<String> asd = Tokenizer.tokenize(msg);
//				
//				boolean contains = false;
//				for(String s : asd) {
//					if(TwitchEmoticons.getInstance().isTwitchEmoticon(s)) {
//						contains = true;
//					}	
//				}
//			
//				
////				String[] splits = line.split(",");
//				
//				
//				globalCount += asd.size();
//				charCount += msg.length();
//			
//				if(contains) {
//					emoticonCount++;
//				}
//				lineCounter++;
//		    }
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		System.out.println("RESULT words in msg: " + globalCount/ (double) lineCounter);
//		System.out.println("RESULT chars in msg: " + charCount/ (double) lineCounter);
//		System.out.println("Res emo: " + emoticonCount);
//	}
}
