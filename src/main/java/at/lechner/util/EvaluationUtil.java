package at.lechner.util;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import at.lechner.commons.MyTuple;

public class EvaluationUtil {

	public static MyTuple[] extractStormResult(String[] lines) {
		MyTuple[] tupels = new MyTuple[lines.length];
		Pattern patternText = Pattern.compile("Tweet: (.*?) predictedSentiment");
		Matcher matcher;
		for (int i = 0; i < lines.length; i++) {
			matcher = patternText.matcher(lines[i]);
			MyTuple tupel = new MyTuple();
			tupel.setId(i);
			if (matcher.find()) {
				tupel.setText(matcher.group(1));
			}
			String sentiment = lines[i].substring(lines[i].lastIndexOf(" ") + 1);
			tupel.setSentiment(sentiment);
			tupels[i] = tupel;
		}
		return tupels;
	}

	public static void generateTSV(String outputPath, String s) {
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath), "utf-8"))) {
			writer.write(s);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
