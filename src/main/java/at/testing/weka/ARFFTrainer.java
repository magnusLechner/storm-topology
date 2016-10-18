package at.testing.weka;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import at.storm.commons.FeaturedTweet;
import at.storm.commons.Tweet;
import at.storm.commons.featurevector.nopos.NoPOSFeatureVectorGenerator;
import at.storm.commons.featurevector.pos.FeatureVectorGenerator;
import at.storm.components.POSTagger;
import at.storm.components.Preprocessor;
import at.storm.components.Tokenizer;
import at.testing.commons.MyTuple;
import at.testing.commons.Sentiment;
import at.testing.preparation.ARFFParser;
import at.testing.preparation.SVMPreparation;
import cmu.arktweetnlp.Tagger.TaggedToken;

public class ARFFTrainer {

	public static final String TEST_PATH = "src/main/resources/arff/Twitch/Test.arff";
	public static final String TRAINING_PATH = "src/main/resources/arff/Twitch/Training.arff";

	public static List<Tweet> createTweetsFromTSV(String input) {
		List<Tweet> tweets = new ArrayList<Tweet>();
		MyTuple[] tuples = SVMPreparation.getMyTuples(input);
		for (int i = 0; i < tuples.length; i++) {
			tweets.add(new Tweet(new Long(i), tuples[i].getText(),
					Sentiment.getSentimentScore(tuples[i].getSentiment().toString())));
		}
		return tweets;
	}

	public static void generateNoPOSARFF(String input, String output, NoPOSFeatureVectorGenerator noPOSFVG) {
		List<Tweet> tweets = createTweetsFromTSV(input);
		generateNoPOSARFF(output, tweets, noPOSFVG);
	}

	public static void generateNoPOSARFF(String out, List<Tweet> tweets, NoPOSFeatureVectorGenerator noPOSFVG) {
		List<FeaturedTweet> featuredTweets = new ArrayList<FeaturedTweet>();
		List<List<String>> tokenizedTweets = Tokenizer.tokenizeTweets(tweets);
		Preprocessor preprocessor = Preprocessor.getInstance();
		List<List<String>> preprocessedTweets = preprocessor.preprocessTweets(tokenizedTweets);

		featuredTweets = new ArrayList<FeaturedTweet>();
		for (int i = 0; i < preprocessedTweets.size(); i++) {
			List<String> preprocessedTweet = preprocessedTweets.get(i);
			Map<Integer, Double> featureVector = noPOSFVG.generateFeatureVector(preprocessedTweet);
			featuredTweets.add(
					new FeaturedTweet(tweets.get(i), tokenizedTweets.get(i), preprocessedTweets.get(i), featureVector));
		}
		ARFFParser.generateARFF(featuredTweets, noPOSFVG.getFeatureVectorSize(), out);
	}

	public static void generatePOSARFF(String input, String output, FeatureVectorGenerator fvg) {
		List<Tweet> tweets = createTweetsFromTSV(input);
		generatePOSARFF(output, tweets, fvg);
	}

	public static void generatePOSARFF(String out, List<Tweet> tweets, FeatureVectorGenerator fvg) {
		List<List<String>> tokenizedTweets = Tokenizer.tokenizeTweets(tweets);
		Preprocessor preprocessor = Preprocessor.getInstance();
		List<List<String>> preprocessedTweets = preprocessor.preprocessTweets(tokenizedTweets);
		POSTagger posTagger = POSTagger.getInstance();
		List<List<TaggedToken>> taggedTweets = posTagger.tagTweets(preprocessedTweets);

		List<FeaturedTweet> featuredTweets = new ArrayList<FeaturedTweet>();
		for (int i = 0; i < taggedTweets.size(); i++) {
			List<TaggedToken> taggedTweet = taggedTweets.get(i);
			Map<Integer, Double> featureVector = fvg.generateFeatureVector(taggedTweet);
			featuredTweets.add(new FeaturedTweet(tweets.get(i), tokenizedTweets.get(i), preprocessedTweets.get(i),
					taggedTweet, featureVector));
		}
		ARFFParser.generateARFF(featuredTweets, fvg.getFeatureVectorSize(), out);
	}

}
