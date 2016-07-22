package at.lechner.dict;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import at.illecker.sentistorm.commons.dict.SentimentDictionary;
import at.illecker.sentistorm.components.Preprocessor;
import at.illecker.sentistorm.components.Tokenizer;
import at.lechner.util.BasicUtil;
import at.lechner.util.UnzipUtil;

public class TwitchLexicon {

	private static final int MIN_TOKEN_LENGTH = 3;
	private static final int MAX_TOKEN_LENGTH = 20;
	private static final int MIN_OCCURENCES = 50;
	
	private static Map<String, Integer> tokenMap = new HashMap<String, Integer>();

	public static void allAtOnce(String folderPath) {
		File folder = new File(folderPath);
		try {
			unzipAll(folder);
			searchForTextFilesInFolder(folder);
			removeNotNeededToken();
			String mapAsLines = convertMapToString();
			createTSV(folderPath + "/result.tsv", mapAsLines);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void searchForTextFilesInFolder(File folder) throws IOException {
		for (File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				searchForTextFilesInFolder(fileEntry);
			} else {
				if (fileEntry.getName().endsWith(".txt")) {
					addToMap(fileEntry.getPath());
				}
			}
		}
	}

	public static void addToMap(String inputPath) throws FileNotFoundException, IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(inputPath))) {
			String line;
			Preprocessor pp = Preprocessor.getInstance();
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.length() == 0) {
					continue;
				}
				JsonObject jsonObject = new JsonParser().parse(line).getAsJsonObject();
				JsonElement messageElement = jsonObject.get("msg");
				String message = messageElement.getAsString();

				List<String> tokens = Tokenizer.tokenize(message);
				List<String> ppTokens = pp.preprocess(tokens);
				for (int j = 0; j < ppTokens.size(); j++) {
					
					if(isUsefulToken(ppTokens.get(j))) {
						if (tokenMap.get(ppTokens.get(j)) == null) {
							tokenMap.put(ppTokens.get(j), 1);
						} else {
							tokenMap.put(ppTokens.get(j), tokenMap.get(ppTokens.get(j)) + 1);
						}	
					}
					
				}
			}
		}
	}

	private static void removeNotNeededToken() {
		Iterator<Entry<String, Integer>> iter = tokenMap.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, Integer> entry = iter.next();
			if (SentimentDictionary.getInstance().getWordSentiments(entry.getKey().toLowerCase()) != null
					|| entry.getValue() < MIN_OCCURENCES) {
				iter.remove();
			}
		}
		tokenMap = (HashMap<String, Integer>) BasicUtil.sortByValue(tokenMap);
	}
	
	public static void unzipAll(File folder) throws IOException {
		for (File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				unzipAll(fileEntry);
			} else {
				if (fileEntry.getName().endsWith(".zip")) {
					String filePath = fileEntry.getPath();
					UnzipUtil.unzip(folder.getPath() + File.separator + fileEntry.getName().replace(".txt.zip", ""),
							filePath);
				}
			}
		}
	}
	
	public static String convertMapToString() {
		StringBuilder sb = new StringBuilder();
		for(Entry<String, Integer> entry : tokenMap.entrySet()) {
			sb.append(entry.getKey());
			sb.append("\t");
			sb.append(entry.getValue());
			sb.append("\n");
		}
		return sb.toString();
	}
	
	public static void createTSV(String outputPath, String mapAsLines) throws IOException {
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath), "utf-8"))) {
			writer.write(mapAsLines);
		}
	}
	
	public static boolean isUsefulToken(String token) {
		boolean isUseful = true;
		
		if(token.length() < MIN_TOKEN_LENGTH || token.length() > MAX_TOKEN_LENGTH) {
			isUseful = false;
		}
		
		return isUseful;
	}
	
	public static void main(String[] args) {
		String globalLogPath = "/home/stud/lechner/globalLogs/1_6";

		allAtOnce(globalLogPath);
	}
}