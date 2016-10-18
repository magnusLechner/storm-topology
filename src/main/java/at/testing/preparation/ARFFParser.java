package at.testing.preparation;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map.Entry;

import at.storm.commons.FeaturedTweet;
import at.storm.commons.SentimentClass;

public class ARFFParser {

	public static void generateARFF(List<FeaturedTweet> featuredTweets, int featureVectorSize, String outputPath) {
		try {
			String header = createARFFHeader(featureVectorSize);
			String data = createARFFData(featuredTweets, featureVectorSize);
			createARFFFile(outputPath, header + data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String createARFFHeader(int featureVectorSize) {
		StringBuilder sb = new StringBuilder();
		sb.append("@RELATION twitch_sentiment\n\n");
		for (int i = 0; i < featureVectorSize; i++) {
			sb.append("@ATTRIBUTE feature" + i + " NUMERIC\n");
		}
		sb.append("@ATTRIBUTE class {NEGATIVE,NEUTRAL,POSITIVE}\n\n");
		sb.append("@DATA\n");
		return sb.toString();
	}
	
	public static String createARFFData(List<FeaturedTweet> featuredTweets, int featureVectorSize) {
		StringBuilder sb = new StringBuilder();
		for(FeaturedTweet featuredTweet : featuredTweets) {
			sb.append("{");
			for(Entry<Integer, Double> entry : featuredTweet.getFeatureVector().entrySet()) {
				sb.append(entry.getKey() - 1);
				sb.append(" ");
				sb.append(entry.getValue());
				sb.append(" ,");
			}
			sb.append(featureVectorSize);
			sb.append(" ");
			sb.append(SentimentClass.fromZeroToTwoScore(featuredTweet.getScore().intValue()));
			sb.append("}\n");
		}
		return sb.toString();
	}

	public static void createARFFFile(String outputPath, String arffFormat) throws IOException {
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath), "utf-8"))) {
			writer.write(arffFormat);
		}
	}

	// public static String createARFFLine(String line) {
	// line = line.replace("'", "");
	// line = line.replace("\\", "");
	// line = line.replace("/", "");
	// String[] splitLine = line.split("\t");
	// return "'" + splitLine[2].trim() + "', " + splitLine[1] + "\n";
	// }

}
