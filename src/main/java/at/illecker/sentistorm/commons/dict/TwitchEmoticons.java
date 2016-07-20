package at.illecker.sentistorm.commons.dict;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.illecker.sentistorm.commons.Configuration;
import at.illecker.sentistorm.commons.util.io.FileUtils;
import at.illecker.sentistorm.commons.util.io.IOUtils;
import at.illecker.sentistorm.commons.util.io.SerializationUtils;

public class TwitchEmoticons {

	private static final Logger LOG = LoggerFactory.getLogger(FirstNames.class);
	private static final TwitchEmoticons INSTANCE = new TwitchEmoticons();

	private Set<String> m_twitchEmoticons = null;

	@SuppressWarnings("unchecked")
	private TwitchEmoticons() {
		for (String file : Configuration.getTwitchEmoticons()) {
			// Try deserialization of file
			String serializationFile = file + ".ser";
			if (IOUtils.exists(serializationFile)) {
				LOG.info("Deserialize TwitchEmoticons from: " + serializationFile);
				if (m_twitchEmoticons == null) {
					m_twitchEmoticons = SerializationUtils.deserialize(serializationFile);
				} else {
					m_twitchEmoticons.addAll((Set<String>) SerializationUtils.deserialize(serializationFile));
				}
			} else {
				LOG.info("Load TwitchEmoticons from: " + file);
				if (m_twitchEmoticons == null) {
					m_twitchEmoticons = FileUtils.readFile(file, true);
					SerializationUtils.serializeCollection(m_twitchEmoticons, serializationFile);
				} else {
					Set<String> twitchEmoticons = FileUtils.readFile(file, true);
					SerializationUtils.serializeCollection(twitchEmoticons, serializationFile);
					m_twitchEmoticons.addAll(twitchEmoticons);
				}
			}
		}
	}

	public static TwitchEmoticons getInstance() {
		return INSTANCE;
	}

	public boolean isTwitchEmoticon(String value) {
		return m_twitchEmoticons.contains(value.toLowerCase());
	}
	
	public Set<String> getEmoticons() {
		return m_twitchEmoticons;
	}

	public static void main(String[] args) {
		TwitchEmoticons twitchEmoticons = TwitchEmoticons.getInstance();
		// Test TwitchEmoticons
		String[] testTwitchEmoticons = new String[] { "4Head", "WutFace", "wutface", "Kappa", "failfish" };
		for (String s : testTwitchEmoticons) {
			System.out.println("isTwitchEmoticons(" + s + "): " + twitchEmoticons.isTwitchEmoticon(s));
		}
	}
	
}
