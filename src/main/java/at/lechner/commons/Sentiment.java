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

	public static Sentiment getSentiment(double value) {
		if (value == 0) {
			return Sentiment.NEGATIVE;
		} else if (value == 1) {
			return Sentiment.NEUTRAL;
		} else if (value == 2) {
			return Sentiment.POSITIVE;
		}
		return null;
	}

}
