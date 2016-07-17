package at.lechner.commons;

public class MyEmoticon {

	private String emote;
	private double value;
	
	public MyEmoticon(String emote, double value) {
		super();
		this.emote = emote;
		this.value = value;
	}

	public String getEmote() {
		return emote;
	}

	public double getValue() {
		return value;
	}
	
}
