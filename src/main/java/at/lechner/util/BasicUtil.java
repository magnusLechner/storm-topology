package at.lechner.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import at.lechner.commons.MyTuple;
import at.lechner.commons.Sentiment;

public class BasicUtil {

	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			@Override
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		Map<K, V> result = new LinkedHashMap<>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
	
	public static String[] readLines(String inputPath) {
		List<String> lines = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new FileReader(inputPath))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	line = line.trim();
				if(line.length() == 0) {
					continue;
				}
				lines.add(line);
		    }
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines.toArray(new String[lines.size()]);
	}
	
	public static MyTuple[] extractTwitchLabeling(String[] lines, boolean withMixed) {
		List<MyTuple> tupels = new ArrayList<MyTuple>();
		for (int i = 0; i < lines.length; i++) {
			JsonObject jsonObject = new JsonParser().parse(lines[i]).getAsJsonObject();
			JsonArray labels = jsonObject.get("label").getAsJsonArray();
			JsonObject label = labels.get(0).getAsJsonObject();
			if (!withMixed && Sentiment.getSentiment(label.get("rating").getAsString()).equals(Sentiment.MIXED)) {
				continue;
			} else {
				tupels.add(new MyTuple(i, jsonObject.get("text").getAsString(), label.get("rating").getAsString()));
			}
		}
		return tupels.toArray(new MyTuple[tupels.size()]);
	}
	
	public static String[] splitTupel(MyTuple tupel, String regex) {
		return tupel.getText().split(regex);
	}
	
	public static String[] splitLine(String line, String regex) {
		return line.split(regex);
	}
	
	public static Double getEmoticonValue(String value) {
		switch (value) {
		//undefined != neutral -> don't use as neutral
//		case "u":		
//			return 0.5;
		case "p":
			return 1.0;
		case "n":
			return -1.0;
		default:
			return null;
		}
	}
	
}
