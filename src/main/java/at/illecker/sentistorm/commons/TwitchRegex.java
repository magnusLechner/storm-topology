package at.illecker.sentistorm.commons;

import java.util.regex.Pattern;

import at.illecker.sentistorm.commons.dict.TwitchEmoticons;
import at.illecker.sentistorm.commons.util.RegexUtils;

public class TwitchRegex {

	private static final TwitchRegex INSTANCE = new TwitchRegex();

	private Pattern m_tokenizerPattern;
	private TwitchEmoticons m_twitchEmoticons;

	private TwitchRegex() {
		// Load Twitch emoticons
		m_twitchEmoticons = TwitchEmoticons.getInstance();
		String twitchEmoticonsRegex = "";
		for (String emoticon : m_twitchEmoticons.getEmoticons()) {
			twitchEmoticonsRegex += "(?i:" + emoticon + ")|";
		}
		m_tokenizerPattern = Pattern.compile(twitchEmoticonsRegex + RegexUtils.TOKENIZER_COMPLETE);
		m_tokenizerPattern = Pattern.compile(RegexUtils.TOKENIZER_COMPLETE);
	}

	public static TwitchRegex getInstance() {
		return INSTANCE;
	}

	public Pattern getTokenizerPattern() {
		return m_tokenizerPattern;
	}

}
