package at.lechner.preparation;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import at.lechner.commons.MyTuple;
import at.lechner.util.BasicUtil;

public class TwitchTSVCreator implements PreparationTool {
	
	private static final String TWITCH_ORIGINAL_RESULTS_PATH = "src/main/resources/preparation/twitch-results-original.txt";
	private static final String TWITCH_ALL_PATH = "src/main/resources/datasets/Twitch/twitch-all-msgs.tsv";
	
	public static void parseTwitchLabelToTSV(String inputPath, String outputPath, boolean withMixed)
			throws IOException {
		String[] linesArray = BasicUtil.readLines(inputPath);
		MyTuple[] tupels = BasicUtil.extractTwitchLabeling(linesArray, withMixed);
		createTwitchLabelTSV(tupels, outputPath);
	}
	
	private static void createTwitchLabelTSV(MyTuple[] tupels, String outputPath) throws IOException {
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath), "utf-8"))) {
			for (int i = 0; i < tupels.length; i++) {
				writer.write(tupels[i].getId() + "\t" + tupels[i].getSentiment() + "\t" + tupels[i].getText() + "\n");
			}
		}
	}
	
	public static void main(String[] args) {
		try {
			 System.out.println("START TWITCH PARSE");
			 parseTwitchLabelToTSV(TWITCH_ORIGINAL_RESULTS_PATH,
			 TWITCH_ALL_PATH, false);
			 System.out.println("END TWITCH PARSE");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void prepare(boolean withMixed) {
		try {
			 System.out.println("START TWITCH PARSE");
			 parseTwitchLabelToTSV(TWITCH_ORIGINAL_RESULTS_PATH,
			 TWITCH_ALL_PATH, withMixed);
			 System.out.println("END TWITCH PARSE");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
