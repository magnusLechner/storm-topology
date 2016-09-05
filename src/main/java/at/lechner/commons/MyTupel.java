package at.lechner.commons;

public class MyTupel {

	private int id;
	private String text;
	private Sentiment sentiment;
	
	public MyTupel() { }
	
	public MyTupel(String text, String sentiment) {
		super();
		this.text = text;
		this.sentiment = Sentiment.getSentiment(sentiment);
	}
	
	public MyTupel(int id, String text, String sentiment) {
		super();
		this.id = id;
		this.text = text;
		this.sentiment = Sentiment.getSentiment(sentiment);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Sentiment getSentiment() {
		return sentiment;
	}

	public void setSentiment(String sentiment) {
		this.sentiment = Sentiment.getSentiment(sentiment);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sentiment == null) ? 0 : sentiment.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MyTupel other = (MyTupel) obj;
		if (sentiment != other.sentiment)
			return false;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MyTupel [id=" + id + ", text=" + text + ", sentiment=" + sentiment + "]";
	}
	
}
