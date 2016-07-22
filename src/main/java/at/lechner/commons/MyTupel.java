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
	public String toString() {
		return "MyTupel [id=" + id + ", text=" + text + ", sentiment=" + sentiment + "]";
	}
	
}
