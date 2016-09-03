package at.lechner.labeling;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import at.lechner.commons.MyTupel;
import at.lechner.util.BasicUtil;

public class PrepareLocalLabel {

	private static final String LABELED_PATH = "src/main/resources/preparation/self-labeling/labeled.txt";
	private static final String ORIGINAL_LENN_PATH = "src/main/resources/preparation/lenn-labeling-data/lenn_merged_result.txt";
	private static final String ORIGINAL_SELF_PATH = "src/main/resources/preparation/self-labeling/original_all_labeled_messages_in_json_except_709_and_lenn.txt";
	private static final String SELF_AND_LENN_PATH = "src/main/resources/preparation/self-labeling/complete_self_labeling_and_lenn.txt";

	private static final String CERTAIN_PATH = "src/main/resources/preparation/self-labeling/certain.txt";
	private static final String UNCERTAIN_PATH = "src/main/resources/preparation/self-labeling/uncertain.txt";
	private static final String MERGE_CERTAIN_PATH = "src/main/resources/preparation/self-labeling/merge_certain.txt";
	private static final String MERGE_UNCERTAIN_PATH = "src/main/resources/preparation/self-labeling/merge_uncertain.txt";

	private static final String RESULT_PATH = "src/main/resources/preparation/complete_result.tsv";
	private static final String ORIGINAL_709_PATH = "/home/magnus/workspace/storm-topology/src/main/resources/preparation"
			+ "/original-labeling/unique-messages.txt";

	public static void getLocalMessages() {
		// TODO Step 1
		// String[] lines = BasicUtil.readLines(LABELED_PATH);
		//// String[] lines = BasicUtil.readLines(ORIGINAL_SELF_AND_LENN_PATH);
		// separateMessages(lines);

		// TODO Step 2:
		// add labeling to all_labeled_...

		// TODO Step 3
		// addCertainToCertain(SELF_AND_LENN_PATH, ORIGINAL_709_PATH);
	}

	public static void addCertainToCertain(String certain1Path, String certain2Path) {
		List<MyTupel> certain = new ArrayList<MyTupel>();
		HashSet<String> uncertain = new HashSet<String>();
		String[] lines1 = BasicUtil.readLines(certain1Path);
		String[] lines2 = BasicUtil.readLines(certain2Path);
		MyTupel[] t1 = getMyTuples(lines1);
		MyTupel[] t2 = getMyTuples(lines2);
		HashMap<String, String> messages = new HashMap<String, String>();
		for (MyTupel t : t1) {
			messages.put(t.getText(), t.getSentiment().toString());
		}
		for (MyTupel t : t2) {
			if (messages.get(t.getText()) == null) {
				if (!uncertain.contains(t.getText())) {
					messages.put(t.getText(), t.getSentiment().toString());
				}
			} else {
				if (!messages.get(t.getText()).equals(t.getSentiment().toString())) {
					uncertain.add(t.getText());
					messages.remove(t.getText());
				}
			}
		}

		int i = 1;
		for (Entry<String, String> entry : messages.entrySet()) {
			MyTupel tupel = new MyTupel(i, entry.getKey(), entry.getValue());
			i++;
			certain.add(tupel);
		}
		print(MERGE_CERTAIN_PATH, certain);
		print(MERGE_UNCERTAIN_PATH, uncertain);
	}

	// removes duplicates
	// separates certain from uncertain
	// removes Undefined
	public static void separateMessages(String[] lines) {
		HashMap<String, String> messages = new HashMap<String, String>();
		List<MyTupel> certain = new ArrayList<MyTupel>();
		HashSet<String> uncertain = new HashSet<String>();
		JsonParser parser = new JsonParser();
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].trim().length() == 0) {
				continue;
			}
			JsonObject jsonObject = (JsonObject) parser.parse(lines[i]);
			String sentiment = jsonObject.get("sentiment").getAsString();
			if (sentiment.equals("UNDEFINED")) {
				continue;
			}
			JsonObject json = jsonObject.get("json").getAsJsonObject();
			String msg = json.get("msg").getAsString();
			if (messages.get(msg) == null) {
				if (!uncertain.contains(msg)) {
					messages.put(msg, sentiment);
				}
			} else {
				if (!messages.get(msg).equals(sentiment)) {
					uncertain.add(msg);
					messages.remove(msg);
				}
			}
		}
		int i = 1;
		for (Entry<String, String> entry : messages.entrySet()) {
			MyTupel tupel = new MyTupel(i, entry.getKey(), entry.getValue());
			i++;
			certain.add(tupel);
		}
		print(CERTAIN_PATH, certain);
		print(UNCERTAIN_PATH, uncertain);
	}

	private static MyTupel[] getMyTuples(String[] lines) {
		List<MyTupel> tupels = new ArrayList<MyTupel>();
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			if (line.trim().length() == 0) {
				continue;
			}
			String[] parts = line.split("\t");
			String msg = "";
			for (int j = 2; j < parts.length; j++) {
				msg += parts[j];
				if (j < parts.length - 1) {
					msg += "\t";
				}
			}
			MyTupel tuple = new MyTupel(Integer.valueOf(parts[0]), msg, parts[1]);
			tupels.add(tuple);
		}
		return tupels.toArray(new MyTupel[tupels.size()]);
	}

	public static void print(String toPath, List<MyTupel> certain) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < certain.size(); i++) {
			sb.append(certain.get(i).getId());
			sb.append("\t");
			sb.append(certain.get(i).getSentiment());
			sb.append("\t");
			sb.append(certain.get(i).getText());
			sb.append("\n");
		}
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(toPath), "utf-8"))) {
			writer.write(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void print(String toPath, Set<String> toPrint) {
		StringBuilder sb = new StringBuilder();
		for (String entry : toPrint) {
			sb.append(entry);
			sb.append("\n");
		}
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(toPath), "utf-8"))) {
			writer.write(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		getLocalMessages();
	}

}
