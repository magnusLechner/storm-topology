package at.lechner.lenn;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import at.lechner.commons.MyTuple;
import at.lechner.util.BasicUtil;

public class LennMessages {

	private static final String INPUT_PATH = "/home/magnus/Schreibtisch/lenn_filtered_229msgs.txt";
	private static final String OUTPUT_PATH = "/home/magnus/Schreibtisch/lenn_messages.tsv";

	public static void getLennTestMessages(String inputPath, String outputPath) {
		try {
			String[] lines = BasicUtil.readLines(inputPath);
			MyTuple[] tupels = extractMessagesWithAlwaysNeutral(lines);
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < tupels.length; i++) {
				sb.append(tupels[i].getId());
				sb.append("\t");
				sb.append(tupels[i].getSentiment());
				sb.append("\t");
				sb.append(tupels[i].getText());
				sb.append("\n");
			}
			try (Writer writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(outputPath), "utf-8"))) {
				writer.write(sb.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static MyTuple[] extractMessagesWithAlwaysNeutral(String[] lines) {
		List<MyTuple> tupels = new ArrayList<MyTuple>();
		for (int i = 0; i < lines.length; i++) {
			JsonObject jsonObject = new JsonParser().parse(lines[i]).getAsJsonObject();
			JsonElement content = jsonObject.get("content");
			tupels.add(new MyTuple(i+1, content.getAsString(), "NEUTRAL"));
		}
		return tupels.toArray(new MyTuple[tupels.size()]);
	}

	public static void main(String[] args) throws IOException {
		getLennTestMessages(INPUT_PATH, OUTPUT_PATH);
	}
	
}
