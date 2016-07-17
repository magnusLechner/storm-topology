package at.lechner.preparation;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import at.lechner.commons.MyEmoticon;
import at.lechner.util.BasicUtil;

public class EmoticonTSVCreator implements PreparationTool {

	private static final String EMOTICONS_ORIGINAL_PATH = "src/main/resources/preparation/twitch-emoticons-original.txt";
	// only emoticons with sentiment
	private static final String EMOTICONS_TSV_PATH = "src/main/resources/dictionaries/sentiment/twitch-emoticons.txt";
	// all emoticons for POS-Tagging
	//	private static final String EMOTICONS_TSV_PATH = "resources/dictionaries/TwitchEmoticons.txt";
	
	public static void parseTwitchEmotes(String inputPath, String outputPath) throws IOException {
		String[] lines = BasicUtil.readLines(inputPath);
		ArrayList<MyEmoticon> emotes = new ArrayList<MyEmoticon>(); 
		for(String line : lines) {
			Pattern pattern = Pattern.compile("(.*?)\\[(.*?)\\]");
		    Matcher matcher = pattern.matcher(line);
		    while(matcher.find()) {
		    	Double value = BasicUtil.getEmoticonValue(matcher.group(2));
		    	if(value != null) {
				    emotes.add(new MyEmoticon(matcher.group(1).toLowerCase(), value));
		    	}
//		    	emotes.add(new MyEmoticon(matcher.group(1), 0.0));
	        }
		}
		writeEmoticonTSV(emotes.toArray(new MyEmoticon[emotes.size()]), outputPath);
	}
	
	private static void writeEmoticonTSV(MyEmoticon[] emotes, String outputPath) throws IOException {
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath), "utf-8"))) {
			for(int i = 0; i < emotes.length; i++) {
				if(i == emotes.length -1) {
					writer.write(emotes[i].getEmote() + "\t" + emotes[i].getValue());
//					writer.write(emotes[i].getEmote());
				} else {
					writer.write(emotes[i].getEmote() + "\t" + emotes[i].getValue() + "\n");
//					writer.write(emotes[i].getEmote() + "\n");
				}	
			}
		}
	}
	
	
	public static void main(String[] args) {
		try {
			System.out.println("PARSE EMOTICON START");
			parseTwitchEmotes(EMOTICONS_ORIGINAL_PATH, EMOTICONS_TSV_PATH);
			System.out.println("PARSE EMOTICON STOP");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void prepare(boolean withMixed) {
		try {
			System.out.println("PARSE EMOTICON START");
			parseTwitchEmotes(EMOTICONS_ORIGINAL_PATH, EMOTICONS_TSV_PATH);
			System.out.println("PARSE EMOTICON STOP");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
