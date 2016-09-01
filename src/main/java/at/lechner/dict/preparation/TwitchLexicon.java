package at.lechner.dict.preparation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.Character.UnicodeBlock;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import at.illecker.sentistorm.commons.dict.SentimentDictionary;
import at.illecker.sentistorm.commons.dict.StopWords;
import at.illecker.sentistorm.commons.dict.TwitchEmoticons;

public class TwitchLexicon {

	public static final String TOP_X_PATH = "/home/magnus/workspace/storm-topology/src/main/resources/"
			+ "dictionaries/under_construction_twitch/13_to_20_july_top_5000_word_count_with_stopwords.txt";
	public static final String UNLABELED_PATH = "/home/magnus/workspace/storm-topology/src/main/resources/"
			+ "dictionaries/under_construction_twitch/unlabeled.txt";
	public static final String LABELED_PATH = "/home/magnus/workspace/storm-topology/src/main/resources/"
			+ "dictionaries/under_construction_twitch/labeled.txt";

	public static final int MIN_WORD_LENGTH = 2;
	public static final StopWords stopwords = StopWords.getInstance();
	public static final SentimentDictionary sentimentDictionary = SentimentDictionary.getInstance();
	public static final TwitchEmoticons twitchEmoticons = TwitchEmoticons.getInstance();

	public static void main(String[] args) {
		List<String> unlabeled = getUnlabeledSentiments();
		writeUnlabeledToFile(unlabeled);
	}

	@SuppressWarnings("serial")
	public static List<String> getUnlabeledSentiments() {
		List<String> unlabeled = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new FileReader(TOP_X_PATH))) {
			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.length() == 0) {
					continue;
				}
				String word = line.split(",")[0].toLowerCase();
				if (word.startsWith("\"") && word.endsWith("\"")) {
					word = word.substring(1, word.length() - 1);
				}

				boolean containsUnwantedLanguage = false;
				
				Set<UnicodeBlock> unwantedUnicodeBlocks = new HashSet<UnicodeBlock>() {{
				    add(UnicodeBlock.CJK_COMPATIBILITY);
				    add(UnicodeBlock.CJK_COMPATIBILITY_FORMS);
				    add(UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS);
				    add(UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT);
				    add(UnicodeBlock.CJK_RADICALS_SUPPLEMENT);
				    add(UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION);
				    add(UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS);
				    add(UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A);
				    add(UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B);
				    add(UnicodeBlock.KANGXI_RADICALS);
				    add(UnicodeBlock.IDEOGRAPHIC_DESCRIPTION_CHARACTERS);
				    add(UnicodeBlock.CYRILLIC);
				}};
				
				for (int i = 0; i < word.length(); i++) {
					if(unwantedUnicodeBlocks.contains(UnicodeBlock.of(word.charAt(i)))) {
						containsUnwantedLanguage = true;
						break;
					}
				}

				if (twitchEmoticons.isTwitchEmoticon(word)) {
					unlabeled.add(word);
				} else if (!containsUnwantedLanguage && word.length() >= MIN_WORD_LENGTH && !stopwords.isStopWord(word)
						&& sentimentDictionary.getWordSentiments(word) == null) {
					unlabeled.add(word);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return unlabeled;
	}

	public static void writeUnlabeledToFile(List<String> unlabeled) {
		try (Writer writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(UNLABELED_PATH), "utf-8"))) {
			StringBuilder sb = new StringBuilder();
			for (String word : unlabeled) {
				sb.append(word).append("\n");
			}
			writer.write(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
