package at.lechner.evaluation;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import at.illecker.sentistorm.commons.dict.StopWords;
import at.illecker.sentistorm.commons.dict.TwitchEmoticons;
import at.illecker.sentistorm.components.Tokenizer;
import at.lechner.commons.MyTupel;
import at.lechner.util.BasicUtil;
import at.lechner.util.EvaluationUtil;

public class DatasetAnalyser implements EvaluationTool {

	private static final String TWITCH_ORIGINAL_RESULTS_PATH = "resources/preparation/twitch-results-original.txt";
	private static final String ANALYSE_RESULT_PATH = "evaluation/analyse_result.txt";
	
	private static final String NO_FEATURE_VECTOR_MESSAGES_PATH = "evaluation/NoFeatureVectorMessages.tsv";
	private static final String WORD_COUNT_NO_FEATURE_VECTOR_MESSAGES_PATH = "evaluation/WordCountNoFeatureVectorMessages.tsv";

	public static void analyseDataset(boolean withMixed) throws IOException {
		String[] lines = BasicUtil.readLines(TWITCH_ORIGINAL_RESULTS_PATH);
		MyTupel[] tupels = BasicUtil.extractTwitchLabeling(lines, withMixed);
		List<String> linesToPrint = new ArrayList<String>();
		getAvgMessageSentenceLength(linesToPrint, tupels);
		countTokens(linesToPrint, tupels);
		createTSV(linesToPrint);
	}

	public static void getAvgMessageSentenceLength(List<String> linesToPrint, MyTupel[] tupels) {
		double allTokensSize = 0.0;
		for (int i = 0; i < tupels.length; i++) {
			List<String> tokens = Tokenizer.tokenize(tupels[i].getText());
			int words = 0;
			for(String token : tokens) {
				if(!TwitchEmoticons.getInstance().isTwitchEmoticon(token)) {		
					words++;
				}
			}
			allTokensSize += words;
		}
		linesToPrint.add("avg message length: " + Double.toString(allTokensSize/tupels.length));
		addSpace(linesToPrint);
	}

	public static void countTokens(List<String> linesToPrint, MyTupel[] tupels) throws IOException {
		HashMap<String, Integer> tokenMap = new HashMap<String, Integer>();
		for (int i = 0; i < tupels.length; i++) {
			List<String> tokens = Tokenizer.tokenize(tupels[i].getText());
			for (int j = 0; j < tokens.size(); j++) {
				if (tokenMap.get(tokens.get(j)) == null) {
					tokenMap.put(tokens.get(j), 1);
				} else {
					tokenMap.put(tokens.get(j), tokenMap.get(tokens.get(j)) + 1);
				}
			}
		}
		tokenMap = (HashMap<String, Integer>) BasicUtil.sortByValue(tokenMap);
		for (String key : tokenMap.keySet()) {
			linesToPrint.add((key + "\t" + tokenMap.get(key) + "\n"));
		}
		addSpace(linesToPrint);
	}
	
	public static void countWordsOfMsgsWithNoFV(String inputPath, String outputPath) throws IOException {
		String[] lines = BasicUtil.readLines(inputPath);
		Map<String, Integer> wordCount = new HashMap<String, Integer>();
		StopWords stopWords = StopWords.getInstance();
		for(int i = 0; i < lines.length; i++) {
			String[] splitLine = lines[i].split("\t");
			String message = "";
			for(int j = 0; j < splitLine.length - 1; j++) {
				message += splitLine[j];
			}
			List<String> tokens = Tokenizer.tokenize(message);
			for(String token : tokens) {
				if(stopWords.isStopWord(token)) {
					continue;
				}
				token = token.toLowerCase();
				if(wordCount.containsKey(token)) {
					wordCount.put(token, wordCount.get(token) + 1);
				} else {
					wordCount.put(token, 1);
				}
			}
		}
		Map<String, Integer> sortedWordCount = BasicUtil.sortByValue(wordCount);
		StringBuilder sb = new StringBuilder();
		for(Entry<String, Integer> entry : sortedWordCount.entrySet()) {
			sb.append(entry.getKey() + "\t" + entry.getValue() + "\n");
		}
		EvaluationUtil.generateTSV(outputPath, sb.toString());
	}

	private static void createTSV(List<String> linesToPrint) throws IOException {
		try (Writer writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(ANALYSE_RESULT_PATH), "utf-8"))) {
			for (int i = 0; i < linesToPrint.size(); i++) {
				writer.write(linesToPrint.get(i));
			}
		}
	}

	private static void addSpace(List<String> linesToPrint) {
		linesToPrint.add("\n\n");
		linesToPrint.add("####################");
		linesToPrint.add("\n\n");
	}

	public static void main(String[] args) {
		try {
			System.out.println("ANALYSE DATASET START");
//			analyseDataset(false);
			countWordsOfMsgsWithNoFV(NO_FEATURE_VECTOR_MESSAGES_PATH, WORD_COUNT_NO_FEATURE_VECTOR_MESSAGES_PATH);
			System.out.println("ANALYSE DATASET STOP");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void evaluate(boolean withMixed) {
		try {
			System.out.println("ANALYSE DATASET START");
			analyseDataset(withMixed);
			System.out.println("ANALYSE DATASET STOP");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
