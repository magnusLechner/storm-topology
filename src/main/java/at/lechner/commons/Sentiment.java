package at.lechner.commons;

public enum Sentiment {

	POSITIVE, NEGATIVE, MIXED, NEUTRAL, UNDEFINED;
	
	public static Sentiment getSentiment(String stringSentiment) {
		switch (stringSentiment) {
		case "POSITIVE":
			return Sentiment.POSITIVE;
		case "NEUTRAL":
			return Sentiment.NEUTRAL;
		case "NEGATIVE":
			return Sentiment.NEGATIVE;
		case "MIXED":
			return Sentiment.MIXED;
		case "UNDEFINED":
			return Sentiment.UNDEFINED;
		}
		return null;
	}
}
